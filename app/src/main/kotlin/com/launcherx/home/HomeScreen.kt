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
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.roundToInt
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
    weatherEnabled: Boolean,
    weatherLocation: String,
    weatherTemperature: String,
    weatherCondition: String,
    isWeatherLoading: Boolean,
    onTimeWidgetOffsetChange: (Float, Float) -> Unit,
    onTimeWidgetScaleChange: (Float) -> Unit,
    onTimeWidgetColorChange: (Long) -> Unit,
    onClockClick: () -> Unit,
    onWeatherClick: () -> Unit,
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
    var timeWidgetBounds by remember { mutableStateOf(Rect.Zero) }
    val density = LocalDensity.current
    val homeTopInset = with(density) {
        if (timeWidgetBounds != Rect.Zero) {
            timeWidgetBounds.bottom.toDp() + 20.dp
        } else {
            132.dp
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Keep the grid out of the clock widget area while using the remaining space fully.
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
                    onGridBoundsChanged = onHomeGridBoundsChanged,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = homeTopInset)
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
            weatherEnabled = weatherEnabled,
            weatherLocation = weatherLocation,
            weatherTemperature = weatherTemperature,
            weatherCondition = weatherCondition,
            isWeatherLoading = isWeatherLoading,
            onOffsetChange = onTimeWidgetOffsetChange,
            onScaleChange = onTimeWidgetScaleChange,
            onColorChange = onTimeWidgetColorChange,
            onClockClick = onClockClick,
            onWeatherClick = onWeatherClick,
            onBoundsChanged = { timeWidgetBounds = it }
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
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .onGloballyPositioned { coordinates ->
                onGridBoundsChanged(coordinates.positionInRoot().let { 
                    Rect(it, Size(coordinates.size.width.toFloat(), coordinates.size.height.toFloat()))
                })
            }
    ) {
        val cellWidth = maxWidth / 4
        val cellHeight = maxHeight / 6
        val iconSizeDp = minOf(cellWidth * 0.72f, cellHeight * 0.58f).coerceIn(40.dp, 56.dp)
        val cellContentHeight = cellHeight.coerceAtLeast(64.dp)

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            repeat(6) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    repeat(4) { col ->
                        val index = row * 4 + col
                        val app = apps.getOrNull(index)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (app != null) {
                                IconCell(
                                    app = app,
                                    iconBitmap = iconBitmaps[app.packageName],
                                    iconSize = iconSizeDp.value.roundToInt(),
                                    containerWidth = cellWidth,
                                    containerHeight = cellContentHeight,
                                    isDragging = dragState.draggedApp?.packageName == app.packageName,
                                    onLaunch = { onLaunchApp(app.packageName) },
                                    onAppInfo = { onAppInfo(app.packageName) },
                                    onRemove = { onRemoveApp(app.packageName) },
                                    onUninstall = { onUninstallApp(app.packageName) },
                                    onDragStart = { onDragStart(app, it) },
                                    onDrag = onDrag,
                                    onDragEnd = onDragEnd
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
