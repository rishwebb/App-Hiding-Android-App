package com.launcherx.data.repository

import com.launcherx.data.dao.*
import com.launcherx.data.entities.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LauncherRepository @Inject constructor(
    private val iconPositionDao: IconPositionDao,
    private val dockAppDao: DockAppDao,
    private val widgetDao: WidgetDao,
    private val hiddenAppDao: HiddenAppDao,
    private val wallpaperDao: WallpaperDao
) {
    // Icon Positions
    fun getAllIconPositions(): Flow<List<IconPositionEntity>> = iconPositionDao.getAllPositions()
    fun getIconPositionsForPage(page: Int): Flow<List<IconPositionEntity>> = iconPositionDao.getPositionsForPage(page)
    suspend fun getMaxPage(): Int = iconPositionDao.getMaxPage() ?: 0
    suspend fun insertIconPosition(position: IconPositionEntity) = iconPositionDao.insertPosition(position)
    suspend fun insertIconPositions(positions: List<IconPositionEntity>) = iconPositionDao.insertPositions(positions)
    suspend fun deleteIconPosition(position: IconPositionEntity) = iconPositionDao.deletePosition(position)
    suspend fun deleteIconByPackageName(packageName: String) = iconPositionDao.deleteByPackageName(packageName)
    suspend fun deleteAllIconPositions() = iconPositionDao.deleteAll()
    suspend fun getIconCountForPage(page: Int): Int = iconPositionDao.getCountForPage(page)

    // Dock Apps
    fun getAllDockApps(): Flow<List<DockAppEntity>> = dockAppDao.getAllDockApps()
    suspend fun insertDockApp(dockApp: DockAppEntity) = dockAppDao.insertDockApp(dockApp)
    suspend fun insertDockApps(dockApps: List<DockAppEntity>) = dockAppDao.insertDockApps(dockApps)
    suspend fun deleteDockApp(dockApp: DockAppEntity) = dockAppDao.deleteDockApp(dockApp)
    suspend fun deleteDockAppsByPackageName(packageName: String) = dockAppDao.deleteByPackageName(packageName)
    suspend fun deleteAllDockApps() = dockAppDao.deleteAll()

    // Widgets
    fun getAllWidgets(): Flow<List<WidgetEntity>> = widgetDao.getAllWidgets()
    fun getWidgetsForPage(page: Int): Flow<List<WidgetEntity>> = widgetDao.getWidgetsForPage(page)
    suspend fun insertWidget(widget: WidgetEntity) = widgetDao.insertWidget(widget)
    suspend fun deleteWidget(widget: WidgetEntity) = widgetDao.deleteWidget(widget)
    suspend fun updateWidget(widget: WidgetEntity) = widgetDao.updateWidget(widget)
    suspend fun deleteAllWidgets() = widgetDao.deleteAll()

    // Hidden Apps
    fun getAllHiddenApps(): Flow<List<HiddenAppEntity>> = hiddenAppDao.getAllHiddenApps()
    suspend fun getHiddenPackageNames(): List<String> = hiddenAppDao.getHiddenPackageNames()
    suspend fun hideApp(packageName: String) = hiddenAppDao.insertHiddenApp(HiddenAppEntity(packageName))
    suspend fun unhideApp(packageName: String) = hiddenAppDao.deleteByPackageName(packageName)
    suspend fun deleteAllHiddenApps() = hiddenAppDao.deleteAll()

    // Wallpaper
    fun getWallpaper(): Flow<WallpaperEntity?> = wallpaperDao.getWallpaper()
    suspend fun setWallpaper(uri: String) = wallpaperDao.insertWallpaper(WallpaperEntity(id = 0, uri = uri))
    suspend fun deleteWallpaper() = wallpaperDao.deleteAll()
}
