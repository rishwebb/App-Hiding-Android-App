package com.launcherx.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "icon_positions")
data class IconPositionEntity(
    @PrimaryKey val packageName: String,
    val page: Int,
    val row: Int,
    val col: Int
)
