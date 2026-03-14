package com.launcherx.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hidden_apps")
data class HiddenAppEntity(
    @PrimaryKey val packageName: String
)
