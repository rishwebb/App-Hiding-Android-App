package com.launcherx

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.launcherx.onboarding.DefaultLauncherOnboarding
import com.launcherx.home.HomeScreen
import com.launcherx.lockscreen.LockScreenOverlay
import com.launcherx.settings.SettingsPanel
import com.launcherx.ui.theme.LauncherXTheme
import com.launcherx.vault.HiddenAppsVault
import com.launcherx.drawer.AppDrawer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LauncherActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            LauncherXTheme {
                LauncherScreen()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // On home screen, do nothing (iOS behavior)
        // Don't call super
    }
}

@Composable
fun LauncherScreen(
    viewModel: LauncherViewModel = hiltViewModel()
) {
    // Collect state
    val homeScreenApps by viewModel.homeScreenApps.collectAsStateWithLifecycle()
    val allApps by viewModel.allApps.collectAsStateWithLifecycle()
    val dockApps by viewModel.dockApps.collectAsStateWithLifecycle()
    val iconBitmaps by viewModel.iconBitmaps.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filteredApps by viewModel.filteredApps.collectAsStateWithLifecycle()
    val hiddenApps by viewModel.hiddenApps.collectAsStateWithLifecycle()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsStateWithLifecycle()
    val isVaultOpen by viewModel.isVaultOpen.collectAsStateWithLifecycle()
    val isAppDrawerOpen by viewModel.isAppLibraryOpen.collectAsStateWithLifecycle()
    val isLockScreenVisible by viewModel.isLockScreenVisible.collectAsStateWithLifecycle()
    val isDefaultLauncher by viewModel.isDefaultLauncher.collectAsStateWithLifecycle()
    val dragState by viewModel.dragState.collectAsStateWithLifecycle()

    // Time widget state
    val timeWidgetOffsetX by viewModel.timeWidgetOffsetX.collectAsStateWithLifecycle()
    val timeWidgetOffsetY by viewModel.timeWidgetOffsetY.collectAsStateWithLifecycle()
    val timeWidgetScale by viewModel.timeWidgetScale.collectAsStateWithLifecycle()
    val timeWidgetColor by viewModel.timeWidgetColor.collectAsStateWithLifecycle()

    // Re-check default status on resume
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.checkDefaultLauncher()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Lifecycle management...
    BackHandler(enabled = isSettingsOpen) {
        viewModel.closeSettings()
    }

    BackHandler(enabled = isAppDrawerOpen) {
        viewModel.closeAppLibrary()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                // Ensure hardware acceleration for main container
                clip = false
            }
            .pointerInput(isVaultOpen, isAppDrawerOpen, isSettingsOpen, dragState.isDragging) {
                if (isVaultOpen || isAppDrawerOpen || isSettingsOpen || dragState.isDragging) return@pointerInput
                
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                    var totalZoom = 1f
                    var totalPanY = 0f
                    do {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val zoom = event.calculateZoom()
                        val pan = event.calculatePan()
                        
                        totalZoom *= zoom
                        totalPanY += pan.y
                        
                        // Zoom out (pinch spread) opens vault
                        if (totalZoom > 1.3f) {
                            viewModel.openVault()
                            totalZoom = 1f
                        }
                        
                        // Vertical pan up opens drawer - ONLY if we aren't dragging and we didn't start the swipe from the Dock (Y > ScreenHeight - 120dp)
                        val screenHeight = with(androidx.compose.ui.platform.LocalDensity.current) { 
                            androidx.compose.ui.platform.LocalConfiguration.current.screenHeightDp.dp.toPx() 
                        }
                        val isFromDock = event.changes.first().position.y > (screenHeight - 300f)

                        if (!dragState.isDragging && !isFromDock && totalPanY < -60f && Math.abs(pan.x) < Math.abs(pan.y)) {
                            viewModel.openAppLibrary()
                            totalPanY = 0f
                        } else if (totalPanY > 60f) {
                            totalPanY = 0f
                        }
                    } while (event.changes.any { it.pressed })
                }
            }
    ) {
        // No custom wallpaper layer needed; system renders behind transparent Activity

        // Home screen (main content — single page)
        HomeScreen(
            homeScreenApps = homeScreenApps,
            dockApps = dockApps,
            iconBitmaps = iconBitmaps,
            dragState = dragState,
            timeWidgetOffsetX = timeWidgetOffsetX,
            timeWidgetOffsetY = timeWidgetOffsetY,
            timeWidgetScale = timeWidgetScale,
            timeWidgetColor = timeWidgetColor,
            onTimeWidgetOffsetChange = { x, y -> viewModel.updateTimeWidgetOffset(x, y) },
            onTimeWidgetScaleChange = { scale -> viewModel.updateTimeWidgetScale(scale) },
            onTimeWidgetColorChange = { colorLong -> viewModel.updateTimeWidgetColor(colorLong) },
            onLaunchApp = { viewModel.launchApp(it) },
            onAppInfo = { viewModel.openAppInfo(it) },
            onRemoveApp = { viewModel.removeAppFromHome(it) },
            onUninstallApp = { viewModel.uninstallApp(it) },
            onDragStart = { app, pos -> viewModel.startDrag(app, pos) },
            onDrag = { pos -> viewModel.updateDrag(pos) },
            onDragEnd = { viewModel.endDrag() },
            onHomeGridBoundsChanged = { viewModel.updateHomeGridBounds(it) },
            onDockBoundsChanged = { viewModel.updateDockBounds(it) }
        )

        // Settings panel overlay
        SettingsPanel(
            isVisible = isSettingsOpen,
            onDismiss = { viewModel.closeSettings() },
            onResetLayout = { viewModel.resetLayout() },
            onResetDock = { viewModel.resetDock() },
            onUninstallLauncher = { viewModel.uninstallLauncher() },
            onChangeDefaultLauncher = { viewModel.openDefaultLauncherSettings() }
        )

        // Hidden apps vault overlay
        HiddenAppsVault(
            allApps = allApps,
            hiddenApps = hiddenApps,
            iconBitmaps = iconBitmaps,
            isOpen = isVaultOpen,
            onClose = { viewModel.closeVault() },
            onLaunchApp = { viewModel.launchApp(it) },
            onUnhideApp = { viewModel.unhideApp(it) },
            onHideApp = { viewModel.hideApp(it) }
        )

        // App Drawer overly
        AppDrawer(
            isVisible = isAppDrawerOpen,
            allApps = allApps,
            homeScreenApps = homeScreenApps,
            filteredApps = filteredApps,
            hiddenApps = hiddenApps,
            searchQuery = searchQuery,
            iconBitmaps = iconBitmaps,
            onLaunchApp = { viewModel.launchApp(it) },
            onAppInfo = { viewModel.openAppInfo(it) },
            onSearchChange = { viewModel.setSearchQuery(it) },
            onDismiss = { viewModel.closeAppLibrary() },
            onAddToHome = { pkg -> viewModel.addAppToGrid(pkg) }
        )

        // Lock screen overlay
        LockScreenOverlay(
            isVisible = isLockScreenVisible,
            onUnlock = { }
        )
        
        // Default Launcher Onboarding (always top-most if needed)
        DefaultLauncherOnboarding(
            isVisible = !isDefaultLauncher
        )

        // Drag Drop Overlay
        if (dragState.isDragging && dragState.draggedApp != null) {
            val pxOffset = with(LocalDensity.current) { 40.dp.toPx() }.toInt()
            val pyOffset = with(LocalDensity.current) { 47.dp.toPx() }.toInt() // ~95dp / 2
            Box(
                modifier = Modifier
                    .offset { 
                        IntOffset(
                            dragState.dragPosition.x.toInt() - pxOffset,
                            dragState.dragPosition.y.toInt() - pyOffset
                        ) 
                    }
            ) {
                com.launcherx.home.IconCell(
                    app = dragState.draggedApp!!,
                    iconBitmap = iconBitmaps[dragState.draggedApp!!.packageName],
                    isDragging = false,
                    onLaunch = {}
                )
            }
        }
    }
}
