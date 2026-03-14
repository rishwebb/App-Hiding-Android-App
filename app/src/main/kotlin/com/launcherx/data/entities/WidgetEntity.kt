package com.launcherx.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "widgets")
data class WidgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val page: Int,
    val row: Int,
    val col: Int,
    val widgetType: String, // "CLOCK","WEATHER","BATTERY","CALENDAR"
    val widgetSize: String  // "SMALL","MEDIUM","LARGE"
)
