package com.launcherx.data.dao

import androidx.room.*
import com.launcherx.data.entities.WallpaperEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WallpaperDao {
    @Query("SELECT * FROM wallpaper WHERE id = 0")
    fun getWallpaper(): Flow<WallpaperEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallpaper(wallpaper: WallpaperEntity)

    @Query("DELETE FROM wallpaper")
    suspend fun deleteAll()
}
