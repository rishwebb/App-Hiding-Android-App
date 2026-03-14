package com.launcherx.home

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val label: String,
    val icon: Drawable?,
    val category: Int = -1,
    val isSystemApp: Boolean = false,
    val installTime: Long = 0L,
    val launchCount: Int = 0
)
