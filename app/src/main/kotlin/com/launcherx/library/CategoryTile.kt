package com.launcherx.library

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.launcherx.home.AppInfo
import com.launcherx.ui.theme.LauncherColors
import com.launcherx.ui.theme.LauncherTypography

@Composable
fun CategoryTile(
    categoryName: String,
    apps: List<AppInfo>,
    iconBitmaps: Map<String, Bitmap>,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(LauncherColors.widgetBackground)
            .clickable { onTap() }
            .padding(12.dp)
    ) {
        // 2×2 icon cluster
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (row in 0..1) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (col in 0..1) {
                        val index = row * 2 + col
                        val app = apps.getOrNull(index)
                        if (app != null) {
                            val bitmap = iconBitmaps[app.packageName]
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = app.label,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF007AFF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = app.label.take(1).uppercase(),
                                        color = Color.White,
                                        style = LauncherTypography.iconLabel
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x22FFFFFF))
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Category name
        Text(
            text = categoryName,
            style = LauncherTypography.categoryTitle,
            maxLines = 1
        )
    }
}
