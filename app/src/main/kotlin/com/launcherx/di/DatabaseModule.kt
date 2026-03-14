package com.launcherx.di

import android.content.Context
import androidx.room.Room
import com.launcherx.data.dao.*
import com.launcherx.data.db.LauncherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LauncherDatabase {
        return Room.databaseBuilder(
            context,
            LauncherDatabase::class.java,
            "launcherx_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideIconPositionDao(db: LauncherDatabase): IconPositionDao = db.iconPositionDao()

    @Provides
    fun provideDockAppDao(db: LauncherDatabase): DockAppDao = db.dockAppDao()

    @Provides
    fun provideWidgetDao(db: LauncherDatabase): WidgetDao = db.widgetDao()

    @Provides
    fun provideHiddenAppDao(db: LauncherDatabase): HiddenAppDao = db.hiddenAppDao()

    @Provides
    fun provideWallpaperDao(db: LauncherDatabase): WallpaperDao = db.wallpaperDao()
}
