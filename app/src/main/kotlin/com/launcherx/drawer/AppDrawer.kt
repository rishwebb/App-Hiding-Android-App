package com.launcherx.drawer

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.launcherx.home.AppInfo
import com.launcherx.home.IconCell
import com.launcherx.ui.theme.SFProFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
    isVisible: Boolean,
    allApps: List<AppInfo>,
    homeScreenApps: Map<Int, List<AppInfo?>>,
    filteredApps: List<AppInfo>,
    hiddenApps: List<AppInfo>,
    searchQuery: String,
    iconBitmaps: Map<String, Bitmap>,
    onLaunchApp: (String) -> Unit,
    onAppInfo: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onAddToHome: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = { onDismiss() },
            sheetState = sheetState,
            containerColor = Color(0xFF141414), // Solid color avoids any background leak
            scrimColor = Color.Black.copy(alpha = 0.5f),
            dragHandle = { BottomSheetDefaults.DragHandle() },
            windowInsets = WindowInsets(0, 0, 0, 0),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            modifier = Modifier.fillMaxSize().widthIn(max = 2000.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search Bar
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp)
                            .background(Color(0xFF2C2C2E), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search",
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontSize = 15.sp,
                                    fontFamily = SFProFontFamily
                                )
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = onSearchChange,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontFamily = SFProFontFamily
                                ),
                                cursorBrush = SolidColor(Color.White),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Settings Button
                    androidx.compose.material3.IconButton(
                        onClick = { onLaunchApp("com.launcherx.settings") },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Launcher Settings",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // App Grid — filter out hidden apps and ghost settings app
                val hiddenPackages = hiddenApps.map { it.packageName }.toSet()
                val visibleAllApps = allApps.filter { it.packageName !in hiddenPackages && it.packageName != "com.launcherx.settings" }
                val visibleFilteredApps = filteredApps.filter { it.packageName !in hiddenPackages && it.packageName != "com.launcherx.settings" }
                val displayApps = if (searchQuery.isEmpty()) visibleAllApps else visibleFilteredApps
                val sortedApps = displayApps.sortedBy { it.label.lowercase() }
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    items(
                        items = sortedApps,
                        key = { it.packageName }
                    ) { app ->
                        // Check if this app is already on ANY page of the home screen grid
                        val isAlreadyOnHome = homeScreenApps.values.flatten().filterNotNull().any { it.packageName == app.packageName }
                        
                        IconCell(
                            app = app,
                            iconBitmap = iconBitmaps[app.packageName],
                            onLaunch = { 
                                onDismiss()
                                onLaunchApp(app.packageName) 
                            },
                            onAppInfo = {
                                onDismiss()
                                onAppInfo(app.packageName)
                            },
                            isDragging = false,
                            onDragStart = { },
                            onDrag = { },
                            onDragEnd = { },
                            onAddToHome = {
                                onDismiss()
                                onAddToHome(app.packageName)
                            },
                            isAppDrawer = true,
                            isAlreadyOnHome = isAlreadyOnHome
                        )
                    }
                }
            }
        }
    }
}
