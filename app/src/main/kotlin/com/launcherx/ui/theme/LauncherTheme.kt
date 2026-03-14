package com.launcherx.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// SF Pro approximation — falls back to system sans-serif
// Drop in actual sf_pro_display.ttf / sf_pro_text.ttf into res/font/ to use real SF Pro
val SFProFontFamily = FontFamily.SansSerif

object LauncherTypography {
    val iconLabel = TextStyle(
        fontFamily = SFProFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        color = Color.White
    )

    val dockLabel = TextStyle(
        fontFamily = SFProFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        color = Color.Transparent // Dock labels are hidden
    )

    val lockScreenTime = TextStyle(
        fontFamily = SFProFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 70.sp,
        color = Color.White
    )

    val lockScreenDate = TextStyle(
        fontFamily = SFProFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        color = Color.White.copy(alpha = 0.85f)
    )

    val searchPlaceholder = TextStyle(
        fontFamily = SFProFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = Color.White.copy(alpha = 0.6f)
    )

    val categoryTitle = TextStyle(
        fontFamily = SFProFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        color = Color.White
    )

    val settingsTitle = TextStyle(
        fontFamily = SFProFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        color = Color.Black
    )

    val settingsItem = TextStyle(
        fontFamily = SFProFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = Color.Black
    )
}

object LauncherColors {
    val dockBackground = Color(0x44FFFFFF)
    val dockFallbackBackground = Color(0x99FFFFFF)
    val folderScrim = Color(0x88000000)
    val appLibrarySearchBar = Color(0x33FFFFFF)
    val deleteRed = Color(0xFFFF3B30)
    val iosBlue = Color(0xFF007AFF)
    val homeIndicator = Color(0xCCFFFFFF)
    val pageIndicatorActive = Color.White
    val pageIndicatorInactive = Color.White.copy(alpha = 0.4f)
    val iconShadow = Color(0x33000000)
    val widgetBackground = Color(0x33FFFFFF)
    val pinButtonBackground = Color(0x33FFFFFF)
    val settingsBackground = Color.White
    val notificationBackground = Color(0x55FFFFFF)
}

private val DarkScheme = darkColorScheme(
    primary = LauncherColors.iosBlue,
    onPrimary = Color.White,
    background = Color.Transparent,
    surface = Color.Transparent,
    onSurface = Color.White
)

@Composable
fun LauncherXTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkScheme,
        content = content
    )
}
