package com.launcherx.dock

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.launcherx.home.AppInfo
import com.launcherx.home.IconCell
import com.launcherx.ui.theme.LauncherColors

@Composable
fun DockController(
    dockApps: List<AppInfo?>,
    iconBitmaps: Map<String, Bitmap>,
    dragState: com.launcherx.LauncherViewModel.DragState,
    onLaunchApp: (String) -> Unit,
    onAppInfo: (String) -> Unit,
    onRemoveApp: (String) -> Unit,
    onUninstallApp: (String) -> Unit,
    onDragStart: (AppInfo, androidx.compose.ui.geometry.Offset) -> Unit,
    onDrag: (androidx.compose.ui.geometry.Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDockBoundsChanged: (androidx.compose.ui.geometry.Rect) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(105.dp)
            .padding(bottom = 8.dp)
            .onGloballyPositioned { coordinates ->
                onDockBoundsChanged(coordinates.positionInRoot().let { 
                    Rect(it, Size(coordinates.size.width.toFloat(), coordinates.size.height.toFloat()))
                })
            },
        contentAlignment = Alignment.Center
    ) {
        // Frosted blur pill background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(90.dp)
                .clip(RoundedCornerShape(28.dp))
                .then(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Modifier.blur(25.dp)
                    } else {
                        Modifier
                    }
                )
                .drawBehind {
                    drawRect(
                        color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            LauncherColors.dockBackground
                        } else {
                            LauncherColors.dockFallbackBackground
                        }
                    )
                }
        )

        // Dock icons — 4 slots, no labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(90.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            dockApps.forEach { appInfo ->
                if (appInfo != null) {
                    IconCell(
                        app = appInfo,
                        iconBitmap = iconBitmaps[appInfo.packageName],
                        showLabel = false, 
                        iconSize = 56,
                        isDragging = dragState.draggedApp?.packageName == appInfo.packageName,
                        onLaunch = { onLaunchApp(appInfo.packageName) },
                        onAppInfo = { onAppInfo(appInfo.packageName) },
                        onRemove = { onRemoveApp(appInfo.packageName) },
                        onUninstall = { onUninstallApp(appInfo.packageName) },
                        onDragStart = { onDragStart(appInfo, it) },
                        onDrag = onDrag,
                        onDragEnd = onDragEnd
                    )
                } else {
                    Spacer(modifier = Modifier.size(56.dp))
                }
            }
        }
    }
}
