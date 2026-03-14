package com.launcherx.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallpaper")
data class WallpaperEntity(
    @PrimaryKey val id: Int = 0,
    val uri: String
)
