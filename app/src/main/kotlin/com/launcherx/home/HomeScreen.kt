package com.launcherx.home

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import android.content.Intent
import android.provider.Settings
import com.launcherx.LauncherViewModel
import com.launcherx.dock.DockController
import com.launcherx.widgets.TimeWidgetOverlay

@Composable
fun HomeScreen(
    homeScreenApps: Map<Int, List<AppInfo?>>,
    dockApps: List<AppInfo?>,
    iconBitmaps: Map<String, Bitmap>,
    dragState: LauncherViewModel.DragState,
    timeWidgetOffsetX: Float,
    timeWidgetOffsetY: Float,
    timeWidgetScale: Float,
    timeWidgetColor: Long,
    onTimeWidgetOffsetChange: (Float, Float) -> Unit,
    onTimeWidgetScaleChange: (Float) -> Unit,
    onTimeWidgetColorChange: (Long) -> Unit,
    onLaunchApp: (String) -> Unit,
    onAppInfo: (String) -> Unit,
    onRemoveApp: (String) -> Unit,
    onUninstallApp: (String) -> Unit,
    onDragStart: (AppInfo, androidx.compose.ui.geometry.Offset) -> Unit,
    onDrag: (androidx.compose.ui.geometry.Offset) -> Unit,
    onDragEnd: () -> Unit,
    onHomeGridBoundsChanged: (Rect) -> Unit,
    onDockBoundsChanged: (Rect) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        try {
                            val intent = Intent(Settings.ACTION_DISPLAY_SETTINGS)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                            Toast.makeText(context, "Adjust Screen Timeout here to sleep faster", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Cannot open display settings", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top spacing for time widget area
            Spacer(modifier = Modifier.height(260.dp))

            // Main content area — single page only (page 0)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                HomeScreenPage(
                    apps = homeScreenApps[0] ?: emptyList(),
                    iconBitmaps = iconBitmaps,
                    dragState = dragState,
                    onLaunchApp = onLaunchApp,
                    onAppInfo = onAppInfo,
                    onRemoveApp = onRemoveApp,
                    onUninstallApp = onUninstallApp,
                    onDragStart = onDragStart,
                    onDrag = onDrag,
                    onDragEnd = onDragEnd,
                    onGridBoundsChanged = onHomeGridBoundsChanged
                )
            }

            // Dock
            DockController(
                dockApps = dockApps,
                iconBitmaps = iconBitmaps,
                dragState = dragState,
                onLaunchApp = onLaunchApp,
                onAppInfo = onAppInfo,
                onRemoveApp = onRemoveApp,
                onUninstallApp = onUninstallApp,
                onDragStart = onDragStart,
                onDrag = onDrag,
                onDragEnd = onDragEnd,
                onDockBoundsChanged = onDockBoundsChanged
            )
        }

        // Time widget overlay (positioned above everything, draggable)
        TimeWidgetOverlay(
            offsetX = timeWidgetOffsetX,
            offsetY = timeWidgetOffsetY,
            scale = timeWidgetScale,
            widgetColor = Color(timeWidgetColor),
            onOffsetChange = onTimeWidgetOffsetChange,
            onScaleChange = onTimeWidgetScaleChange,
            onColorChange = onTimeWidgetColorChange
        )
    }
}

@Composable
fun HomeScreenPage(
    apps: List<AppInfo?>,
    iconBitmaps: Map<String, Bitmap>,
    dragState: LauncherViewModel.DragState,
    onLaunchApp: (String) -> Unit,
    onAppInfo: (String) -> Unit,
    onRemoveApp: (String) -> Unit,
    onUninstallApp: (String) -> Unit,
    onDragStart: (AppInfo, androidx.compose.ui.geometry.Offset) -> Unit,
    onDrag: (androidx.compose.ui.geometry.Offset) -> Unit,
    onDragEnd: () -> Unit,
    onGridBoundsChanged: (Rect) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .onGloballyPositioned { coordinates ->
                onGridBoundsChanged(coordinates.positionInRoot().let { 
                    Rect(it, Size(coordinates.size.width.toFloat(), coordinates.size.height.toFloat()))
                })
            }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            userScrollEnabled = false
        ) {
            items(
                count = 24, // Fixed 4×6 grid
                key = { it }
            ) { index ->
                val app = apps.getOrNull(index)
                if (app != null) {
                    IconCell(
                        app = app,
                        iconBitmap = iconBitmaps[app.packageName],
                        isDragging = dragState.draggedApp?.packageName == app.packageName,
                        onLaunch = { onLaunchApp(app.packageName) },
                        onAppInfo = { onAppInfo(app.packageName) },
                        onRemove = { onRemoveApp(app.packageName) },
                        onUninstall = { onUninstallApp(app.packageName) },
                        onDragStart = { onDragStart(app, it) },
                        onDrag = onDrag,
                        onDragEnd = onDragEnd
                    )
                } else {
                    // Empty slot placeholder to maintain absolute coordinates
                    Box(modifier = Modifier.size(64.dp))
                }
            }
        }
    }
}
