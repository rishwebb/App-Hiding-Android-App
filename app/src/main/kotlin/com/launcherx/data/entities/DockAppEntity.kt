package com.launcherx.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dock_apps")
data class DockAppEntity(
    @PrimaryKey val slot: Int, // 0-3
    val packageName: String
)
