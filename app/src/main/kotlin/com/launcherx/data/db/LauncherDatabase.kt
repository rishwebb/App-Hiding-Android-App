package com.launcherx.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.launcherx.data.dao.*
import com.launcherx.data.entities.*

@Database(
    entities = [
        IconPositionEntity::class,
        DockAppEntity::class,
        WidgetEntity::class,
        HiddenAppEntity::class,
        WallpaperEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class LauncherDatabase : RoomDatabase() {
    abstract fun iconPositionDao(): IconPositionDao
    abstract fun dockAppDao(): DockAppDao
    abstract fun widgetDao(): WidgetDao
    abstract fun hiddenAppDao(): HiddenAppDao
    abstract fun wallpaperDao(): WallpaperDao
}
