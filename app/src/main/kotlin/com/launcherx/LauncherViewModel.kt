package com.launcherx

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.launcherx.data.entities.*
import com.launcherx.data.repository.LauncherRepository
import com.launcherx.home.AppInfo
import com.launcherx.icons.IconPackManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val app: Application,
    private val repository: LauncherRepository,
    private val iconPackManager: IconPackManager
) : AndroidViewModel(app) {

    // SharedPreferences for time widget
    private val prefs: SharedPreferences = app.getSharedPreferences("launcher_prefs", Context.MODE_PRIVATE)

    // UI State
    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _isAppLibraryOpen = MutableStateFlow(false)
    val isAppLibraryOpen: StateFlow<Boolean> = _isAppLibraryOpen.asStateFlow()

    private val _isLockScreenVisible = MutableStateFlow(false)
    val isLockScreenVisible: StateFlow<Boolean> = _isLockScreenVisible.asStateFlow()

    private val _isVaultOpen = MutableStateFlow(false)
    val isVaultOpen: StateFlow<Boolean> = _isVaultOpen.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _isDefaultLauncher = MutableStateFlow(true) // assume true initially
    val isDefaultLauncher: StateFlow<Boolean> = _isDefaultLauncher.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Time widget state backed by SharedPreferences
    private val _timeWidgetOffsetX = MutableStateFlow(prefs.getFloat("widget_offset_x", -1f))
    val timeWidgetOffsetX: StateFlow<Float> = _timeWidgetOffsetX.asStateFlow()

    private val _timeWidgetOffsetY = MutableStateFlow(prefs.getFloat("widget_offset_y", 200f))
    val timeWidgetOffsetY: StateFlow<Float> = _timeWidgetOffsetY.asStateFlow()

    private val _timeWidgetScale = MutableStateFlow(prefs.getFloat("widget_scale", 1f))
    val timeWidgetScale: StateFlow<Float> = _timeWidgetScale.asStateFlow()

    private val _timeWidgetColor = MutableStateFlow(prefs.getLong("widget_color", 0xFFFFFFFF))
    val timeWidgetColor: StateFlow<Long> = _timeWidgetColor.asStateFlow()

    // App data
    private val _allApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val allApps: StateFlow<List<AppInfo>> = _allApps.asStateFlow()

    private val _homeScreenApps = MutableStateFlow<Map<Int, List<AppInfo?>>>(emptyMap())
    val homeScreenApps: StateFlow<Map<Int, List<AppInfo?>>> = _homeScreenApps.asStateFlow()

    private val _dockApps = MutableStateFlow<List<AppInfo?>>(listOf(null, null, null, null, null))
    val dockApps: StateFlow<List<AppInfo?>> = _dockApps.asStateFlow()

    private val _hiddenApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val hiddenApps: StateFlow<List<AppInfo>> = _hiddenApps.asStateFlow()

    // Icon bitmaps cache
    private val _iconBitmaps = MutableStateFlow<Map<String, Bitmap>>(emptyMap())
    val iconBitmaps: StateFlow<Map<String, Bitmap>> = _iconBitmaps.asStateFlow()

    // UI State
    data class DragState(
        val isDragging: Boolean = false,
        val draggedApp: AppInfo? = null,
        val dragPosition: androidx.compose.ui.geometry.Offset = androidx.compose.ui.geometry.Offset.Zero
    )

    private val _dragState = MutableStateFlow(DragState())
    val dragState: StateFlow<DragState> = _dragState.asStateFlow()

    // Page count
    // Single page only — always 1
    val pageCount: StateFlow<Int> = MutableStateFlow(1).asStateFlow()

    // Filtered apps for search
    val filteredApps: StateFlow<List<AppInfo>> = combine(_allApps, _searchQuery) { apps, query ->
        if (query.isBlank()) apps
        else apps.filter { it.label.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Category groups for App Library
    val categorizedApps: StateFlow<Map<String, List<AppInfo>>> = _allApps.map { apps ->
        apps.groupBy { appInfo ->
            when (appInfo.category) {
                ApplicationInfo.CATEGORY_SOCIAL -> "Social"
                ApplicationInfo.CATEGORY_PRODUCTIVITY -> "Productivity"
                ApplicationInfo.CATEGORY_GAME -> "Games"
                ApplicationInfo.CATEGORY_VIDEO -> "Entertainment"
                ApplicationInfo.CATEGORY_NEWS -> "News & Reading"
                ApplicationInfo.CATEGORY_AUDIO -> "Entertainment"
                else -> "Utilities"
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent ?: return
            val action = intent.action
            val packageName = intent.data?.schemeSpecificPart ?: return
            
            if (packageName == app.packageName) return // Ignore self

            when (action) {
                Intent.ACTION_PACKAGE_ADDED -> handlePackageAdded(packageName)
                Intent.ACTION_PACKAGE_REMOVED, Intent.ACTION_PACKAGE_FULLY_REMOVED -> handlePackageRemoved(packageName)
                Intent.ACTION_PACKAGE_REPLACED -> {
                    // Update icon/label
                    handlePackageAdded(packageName)
                }
            }
        }
    }

    init {
        loadInstalledApps()
        checkDefaultLauncher()

        // Register package receiver
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        app.registerReceiver(packageReceiver, filter)
    }

    fun checkDefaultLauncher() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }
        val resolveInfo = app.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        _isDefaultLauncher.value = resolveInfo?.activityInfo?.packageName == app.packageName
    }

    private fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = app.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val activities = pm.queryIntentActivities(mainIntent, 0)
            val hiddenPackages = repository.getHiddenPackageNames().toSet()

            val apps = activities.mapNotNull { resolveInfo ->
                val pkgName = resolveInfo.activityInfo.packageName
                if (pkgName == app.packageName) return@mapNotNull null // hide self

                val appInfo = try {
                    pm.getApplicationInfo(pkgName, 0)
                } catch (_: PackageManager.NameNotFoundException) { null }

                val label = resolveInfo.loadLabel(pm).toString()
                val category = appInfo?.category ?: -1

                AppInfo(
                    packageName = pkgName,
                    label = label,
                    icon = resolveInfo.loadIcon(pm),
                    category = category,
                    isSystemApp = appInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM) != 0,
                    installTime = try {
                        pm.getPackageInfo(pkgName, 0).firstInstallTime
                    } catch (_: Exception) { 0L }
                )
            }.distinctBy { it.packageName }.toMutableList()

            // Inject virtual Settings app
            apps.add(
                AppInfo(
                    packageName = "com.launcherx.settings",
                    label = "Launcher Settings",
                    icon = androidx.core.content.ContextCompat.getDrawable(app, com.launcherx.R.drawable.ic_launcher_foreground),
                    category = -1,
                    isSystemApp = true,
                    installTime = 0L
                )
            )

            _allApps.value = apps

            // Load icon bitmaps
            val bitmaps = mutableMapOf<String, Bitmap>()
            apps.forEach { info ->
                val appInfoAndroid = try {
                    pm.getApplicationInfo(info.packageName, 0)
                } catch (_: Exception) { null }
                bitmaps[info.packageName] = iconPackManager.getIconForPackage(info.packageName, appInfoAndroid)
            }
            _iconBitmaps.value = bitmaps

            // Clean up old virtual settings ghost app
            try {
                repository.deleteIconByPackageName("com.launcherx.settings")
                repository.deleteDockApp(DockAppEntity(0, "com.launcherx.settings"))
                repository.deleteDockApp(DockAppEntity(1, "com.launcherx.settings"))
                repository.deleteDockApp(DockAppEntity(2, "com.launcherx.settings"))
                repository.deleteDockApp(DockAppEntity(3, "com.launcherx.settings"))
            } catch (_: Exception) {}

            // Initialize positions if empty
            val positions = repository.getAllIconPositions().first()
            if (positions.isEmpty() && apps.isNotEmpty()) {
                val nonHiddenApps = apps.filter { it.packageName !in hiddenPackages }
                initializeDefaultLayout(nonHiddenApps)
                // Force an immediate reload of the layout
                val initialDock = repository.getAllDockApps().first()
                val initialGrid = repository.getAllIconPositions().first()
                
                val allAppsMap = apps.associateBy { it.packageName }
                
                // Dock
                val dock = MutableList<AppInfo?>(5) { null }
                initialDock.forEach { entity ->
                    if (entity.slot in 0..4) dock[entity.slot] = allAppsMap[entity.packageName]
                }
                _dockApps.value = dock
                
                // Grid
                val pages = mutableMapOf<Int, MutableList<AppInfo?>>()
                val maxPage = initialGrid.maxOfOrNull { it.page } ?: 1
                for (p in 1..maxPage) {
                    pages[p] = MutableList(24) { null }
                }
                initialGrid.forEach { pos ->
                    allAppsMap[pos.packageName]?.let { appInfo ->
                        val list = pages.getOrPut(pos.page) { MutableList(24) { null } }
                        val index = (pos.row * 4 + pos.col).coerceIn(0, 23)
                        list[index] = appInfo
                    }
                }
                _homeScreenApps.value = pages
            }
            
            // Critical: Only start observing the UI flows now that _allApps is loaded
            observeDatabase()
        }
    }

    private fun observeDatabase() {
        viewModelScope.launch {
            // Observe icon positions
            repository.getAllIconPositions().collect { positions ->
                val allAppsMap = _allApps.value.associateBy { it.packageName }
                val pages = mutableMapOf<Int, MutableList<AppInfo?>>()
                val maxPage = positions.maxOfOrNull { it.page } ?: 1
                for (p in 1..maxPage) {
                    pages[p] = MutableList(24) { null }
                }
                positions.forEach { pos ->
                    allAppsMap[pos.packageName]?.let { appInfo ->
                        val list = pages.getOrPut(pos.page) { MutableList(24) { null } }
                        val index = (pos.row * 4 + pos.col).coerceIn(0, 23)
                        list[index] = appInfo
                    }
                }
                _homeScreenApps.value = pages
            }
        }

        viewModelScope.launch {
            // Observe dock apps
            repository.getAllDockApps().collect { dockEntities ->
                val allAppsMap = _allApps.value.associateBy { it.packageName }
                val dock = MutableList<AppInfo?>(5) { null }
                dockEntities.forEach { entity ->
                    if (entity.slot in 0..4) {
                        dock[entity.slot] = allAppsMap[entity.packageName]
                    }
                }
                _dockApps.value = dock
                
                // Auto-populate dock if completely empty
                if (dock.all { it == null } && _allApps.value.isNotEmpty()) {
                    val dockCandidates = listOf(
                        "com.android.dialer", "com.google.android.dialer",
                        "net.one97.paytm",
                        "com.whatsapp",
                        "com.railyatri.in", "in.gov.railtel.railone", "in.gov.railtel.RailOne",
                        "com.android.camera", "com.google.android.GoogleCamera", "com.android.camera2"
                    )
                    val availableApps = _allApps.value
                    val dockFill = mutableListOf<String>()
                    for (candidate in dockCandidates) {
                        if (availableApps.any { it.packageName == candidate } && dockFill.size < 5) {
                            dockFill.add(candidate)
                        }
                    }
                    // Fill remaining slots with first available apps
                    for (a in availableApps) {
                        if (dockFill.size >= 5) break
                        if (a.packageName !in dockFill && a.packageName != "com.launcherx.settings") {
                            dockFill.add(a.packageName)
                        }
                    }
                    val entities = dockFill.take(5).mapIndexed { index, pkg ->
                        DockAppEntity(slot = index, packageName = pkg)
                    }
                    viewModelScope.launch(Dispatchers.IO) {
                        repository.insertDockApps(entities)
                    }
                }
            }
        }

        viewModelScope.launch {
            // Observe hidden apps
            repository.getAllHiddenApps().collect { hiddenEntities ->
                val allAppsMap = _allApps.value.associateBy { it.packageName }
                _hiddenApps.value = hiddenEntities.mapNotNull { allAppsMap[it.packageName] }
            }
        }
    }

    private suspend fun initializeDefaultLayout(apps: List<AppInfo>) {
        // Reserve first 5 common apps for dock
        val dockCandidates = listOf(
            "com.android.dialer", "com.google.android.dialer",
            "net.one97.paytm",
            "com.whatsapp",
            "com.railyatri.in", "in.gov.railtel.railone", "in.gov.railtel.RailOne",
            "com.android.camera", "com.google.android.GoogleCamera", "com.android.camera2"
        )
        val dockApps = mutableListOf<String>()
        for (candidate in dockCandidates) {
            if (apps.any { it.packageName == candidate } && dockApps.size < 5) {
                dockApps.add(candidate)
            }
        }
        // Fill dock with first available apps if needed
        for (a in apps) {
            if (dockApps.size >= 5) break
            if (a.packageName !in dockApps) dockApps.add(a.packageName)
        }

        val dockEntities = dockApps.take(5).mapIndexed { index, pkg ->
            DockAppEntity(slot = index, packageName = pkg)
        }
        repository.insertDockApps(dockEntities)
        
        // Home screen grid is left completely empty as requested by user
    }

    // Actions
    fun setCurrentPage(page: Int) { _currentPage.value = page }
    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun openSettings() { _isSettingsOpen.value = true }
    fun closeSettings() { _isSettingsOpen.value = false }
    fun openVault() { _isVaultOpen.value = true }
    fun closeVault() { _isVaultOpen.value = false }
    fun openAppLibrary() { _isAppLibraryOpen.value = true }
    fun closeAppLibrary() { 
        _isAppLibraryOpen.value = false
        _searchQuery.value = "" // Reset search when closing
    }

    // Time widget actions
    fun updateTimeWidgetOffset(x: Float, y: Float) {
        _timeWidgetOffsetX.value = x
        _timeWidgetOffsetY.value = y
        prefs.edit().putFloat("widget_offset_x", x).putFloat("widget_offset_y", y).apply()
    }

    fun updateTimeWidgetScale(scale: Float) {
        _timeWidgetScale.value = scale.coerceIn(0.6f, 1.5f)
        prefs.edit().putFloat("widget_scale", _timeWidgetScale.value).apply()
    }

    fun updateTimeWidgetColor(colorLong: Long) {
        _timeWidgetColor.value = colorLong
        prefs.edit().putLong("widget_color", colorLong).apply()
    }

    fun launchApp(packageName: String) {
        if (packageName == "com.launcherx.settings") {
            openSettings()
            return
        }

        val pm = app.packageManager
        val intent = pm.getLaunchIntentForPackage(packageName)
        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            app.startActivity(it)
        }
    }

    fun hideApp(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.hideApp(packageName)
            repository.deleteIconByPackageName(packageName)
        }
    }

    fun openAppInfo(packageName: String) {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.parse("package:$packageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        app.startActivity(intent)
    }

    // Drag & Drop Actions
    fun startDrag(app: AppInfo, position: androidx.compose.ui.geometry.Offset) {
        _dragState.value = DragState(isDragging = true, draggedApp = app, dragPosition = position)
    }

    fun updateDrag(position: androidx.compose.ui.geometry.Offset) {
        _dragState.value = _dragState.value.copy(dragPosition = position)
    }

    var homeGridBounds: androidx.compose.ui.geometry.Rect = androidx.compose.ui.geometry.Rect.Zero
    var dockBounds: androidx.compose.ui.geometry.Rect = androidx.compose.ui.geometry.Rect.Zero

    fun updateHomeGridBounds(bounds: androidx.compose.ui.geometry.Rect) { homeGridBounds = bounds }
    fun updateDockBounds(bounds: androidx.compose.ui.geometry.Rect) { dockBounds = bounds }

    fun endDrag() {
        val state = _dragState.value
        if (state.isDragging && state.draggedApp != null) {
            val globalPosition = state.dragPosition
            val draggedPackage = state.draggedApp.packageName
            
            if (dockBounds.contains(globalPosition)) {
                val dockWidth = dockBounds.width
                val col = ((globalPosition.x - dockBounds.left) / (dockWidth / 5)).toInt().coerceIn(0, 4)
                moveAppToDock(draggedPackage, col)
            } else {
                val gridWidth = if (homeGridBounds.width > 0) homeGridBounds.width else 1080f
                val gridHeight = if (homeGridBounds.height > 0) homeGridBounds.height else 1920f
                val left = homeGridBounds.left
                val top = homeGridBounds.top

                val cellWidth = gridWidth / 4
                val rowHeight = gridHeight / 6
                val col = ((globalPosition.x - left) / cellWidth).toInt().coerceIn(0, 3)
                val row = ((globalPosition.y - top) / rowHeight).toInt().coerceIn(0, 5)
                val linearIndex = row * 4 + col
                moveAppToGrid(draggedPackage, currentPage.value, linearIndex)
            }
        }
        _dragState.value = DragState()
    }

    fun addAppToGrid(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val positions = repository.getAllIconPositions().first()
            val targetPage = currentPage.value
            val existingPositionsOnPage = positions.filter { it.page == targetPage }
            
            var emptyIndex = -1
            for (i in 0 until 24) { // 4x6 grid = 24 slots
                val r = i / 4
                val c = i % 4
                if (existingPositionsOnPage.none { it.row == r && it.col == c }) {
                    emptyIndex = i
                    break
                }
            }
            
            if (emptyIndex != -1) {
                moveAppToGrid(packageName, targetPage, emptyIndex)
            } else {
                moveAppToGrid(packageName, targetPage + 1, 0)
            }
        }
    }

    fun moveAppToGrid(packageName: String, targetPage: Int, targetIndex: Int) {
        val row = targetIndex / 4
        val col = targetIndex % 4
        
        viewModelScope.launch(Dispatchers.IO) {
            val positions = repository.getAllIconPositions().first()
            val dockApps = repository.getAllDockApps().first()
            
            // Note: targetIndex ranges from 0-23 (4 cols x 6 rows). 
            // Find if there's an existing app exactly at this physical slot.
            val existingAppAtTarget = positions.find { it.page == targetPage && it.row == row && it.col == col }
            
            // Remove the incoming app from its old position (Grid or Dock)
            val oldPos = positions.find { it.packageName == packageName }
            repository.deleteIconByPackageName(packageName)
            val oldDock = dockApps.find { it.packageName == packageName }
            if (oldDock != null) {
                repository.deleteDockApp(oldDock)
            }
            
            // Case 1: Slot is empty -> purely drop it there
            if (existingAppAtTarget == null) {
                repository.insertIconPosition(IconPositionEntity(packageName, targetPage, row, col))
            } 
            // Case 2: Slot is occupied by another app
            else if (existingAppAtTarget.packageName != packageName) {
                // Swap logic
                // Overwrite the target slot with the incoming drag app
                repository.insertIconPosition(IconPositionEntity(packageName, targetPage, row, col))
                
                // Now, decide where to dump the app that was sitting there.
                if (oldPos != null) {
                    // Swapped from somewhere on the grid -> send it to the dragged app's old location!
                    repository.deleteIconByPackageName(existingAppAtTarget.packageName)
                    repository.insertIconPosition(IconPositionEntity(existingAppAtTarget.packageName, oldPos.page, oldPos.row, oldPos.col))
                } else if (oldDock != null) {
                    // Swapped from the dock -> dump the existing app into the dragged app's old dock slot!
                    repository.deleteIconByPackageName(existingAppAtTarget.packageName)
                    repository.insertDockApp(DockAppEntity(oldDock.slot, existingAppAtTarget.packageName))
                }
            }
        }
    }


    fun moveAppToDock(packageName: String, targetSlot: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val dockApps = repository.getAllDockApps().first()
            val positions = repository.getAllIconPositions().first()
            
            val occupant = dockApps.find { it.slot == targetSlot }
            val oldPos = positions.find { it.packageName == packageName }
            val oldDock = dockApps.find { it.packageName == packageName }
            
            if (occupant != null && occupant.packageName != packageName) {
                if (oldPos != null) {
                    repository.insertIconPosition(IconPositionEntity(occupant.packageName, oldPos.page, oldPos.row, oldPos.col))
                    repository.deleteIconByPackageName(packageName)
                } else if (oldDock != null) {
                    repository.insertDockApp(DockAppEntity(oldDock.slot, occupant.packageName))
                }
            } else {
                if (oldPos != null) repository.deleteIconByPackageName(packageName)
                if (oldDock != null) repository.deleteDockApp(oldDock)
            }
            
            repository.insertDockApp(DockAppEntity(targetSlot, packageName))
        }
    }

    fun unhideApp(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.unhideApp(packageName)
            // Note: Does not automatically add to home screen anymore,
            // as user requested an empty home screen.
        }
    }

    fun removeAppFromHome(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteIconByPackageName(packageName)
            val dockAppsList = repository.getAllDockApps().first()
            val dockEntity = dockAppsList.find { it.packageName == packageName }
            if (dockEntity != null) {
                repository.deleteDockApp(dockEntity)
            }
        }
    }

    fun uninstallApp(packageName: String) {
        val intent = Intent(Intent.ACTION_DELETE).apply {
            data = android.net.Uri.parse("package:$packageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        app.startActivity(intent)
    }

    fun resetLayout() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllIconPositions()
            repository.deleteAllDockApps()
            val hiddenPackages = repository.getHiddenPackageNames().toSet()
            initializeDefaultLayout(_allApps.value.filter { it.packageName !in hiddenPackages })
        }
    }

    fun resetDock() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllDockApps()
            val apps = _allApps.value
            val dockCandidates = listOf(
                "com.android.dialer", "com.google.android.dialer",
                "net.one97.paytm",
                "com.whatsapp",
                "com.railyatri.in", "in.gov.railtel.railone", "in.gov.railtel.RailOne",
                "com.android.camera", "com.google.android.GoogleCamera", "com.android.camera2"
            )
            val dockApps = mutableListOf<String>()
            for (candidate in dockCandidates) {
                if (apps.any { it.packageName == candidate } && dockApps.size < 5) {
                    dockApps.add(candidate)
                }
            }
            for (a in apps) {
                if (dockApps.size >= 5) break
                if (a.packageName !in dockApps) dockApps.add(a.packageName)
            }
            val entities = dockApps.take(5).mapIndexed { i, pkg -> DockAppEntity(i, pkg) }
            repository.insertDockApps(entities)
        }
    }

    fun uninstallLauncher() {
        val intent = Intent(Intent.ACTION_DELETE).apply {
            data = android.net.Uri.parse("package:${app.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        app.startActivity(intent)
    }

    fun openDefaultLauncherSettings() {
        val intent = Intent(android.provider.Settings.ACTION_HOME_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        app.startActivity(intent)
    }

    private fun handlePackageAdded(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = app.packageManager
            val appInfo = try {
                pm.getApplicationInfo(packageName, 0)
            } catch (_: PackageManager.NameNotFoundException) { return@launch }

            val mainIntent = pm.getLaunchIntentForPackage(packageName) ?: return@launch
            val resolveInfo = pm.resolveActivity(mainIntent, 0) ?: return@launch

            val label = resolveInfo.loadLabel(pm).toString()
            val category = appInfo.category
            val isSystemApp = appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
            val installTime = try {
                pm.getPackageInfo(packageName, 0).firstInstallTime
            } catch (_: Exception) { 0L }

            val newAppInfo = AppInfo(
                packageName = packageName,
                label = label,
                icon = resolveInfo.loadIcon(pm),
                category = category,
                isSystemApp = isSystemApp,
                installTime = installTime
            )

            // Update all apps and fetch bitmap
            var currentApps = _allApps.value.toMutableList()
            // Remove if already exists (for replace case)
            currentApps.removeAll { it.packageName == packageName }
            currentApps.add(newAppInfo)
            _allApps.value = currentApps

            val currentBitmaps = _iconBitmaps.value.toMutableMap()
            currentBitmaps[packageName] = iconPackManager.getIconForPackage(packageName, appInfo)
            _iconBitmaps.value = currentBitmaps

            // Add to next available grid position if it's completely new (not in DB)
            // Do not automatically add to grid as user wants an empty home screen by default
            // The app will simply appear in the App Drawer.
        }
    }

    private fun handlePackageRemoved(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Remove from room db
            repository.deleteIconByPackageName(packageName)
            repository.deleteDockApp(DockAppEntity(0, packageName))
            repository.deleteDockApp(DockAppEntity(1, packageName))
            repository.deleteDockApp(DockAppEntity(2, packageName))
            repository.deleteDockApp(DockAppEntity(3, packageName))
            repository.deleteDockApp(DockAppEntity(4, packageName))
            
            val currentApps = _allApps.value.toMutableList()
            currentApps.removeAll { it.packageName == packageName }
            _allApps.value = currentApps
            
            val currentBitmaps = _iconBitmaps.value.toMutableMap()
            currentBitmaps.remove(packageName)
            _iconBitmaps.value = currentBitmaps
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            app.unregisterReceiver(packageReceiver)
        } catch (_: Exception) {}
    }
}
