package com.launcherx.library

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.launcherx.home.AppInfo
import com.launcherx.home.IconCell
import com.launcherx.ui.theme.LauncherColors
import com.launcherx.ui.theme.LauncherTypography
import com.launcherx.ui.theme.SFProFontFamily
import androidx.compose.foundation.Image

@Composable
fun AppLibraryScreen(
    allApps: List<AppInfo>,
    categorizedApps: Map<String, List<AppInfo>>,
    iconBitmaps: Map<String, Bitmap>,
    searchQuery: String,
    filteredApps: List<AppInfo>,
    onSearchQueryChanged: (String) -> Unit,
    onLaunchApp: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSearchActive by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Search bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(LauncherColors.appLibrarySearchBar)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Box {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "App Library",
                            style = LauncherTypography.searchPlaceholder
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = {
                            onSearchQueryChanged(it)
                            isSearchActive = it.isNotEmpty()
                        },
                        singleLine = true,
                        textStyle = LauncherTypography.searchPlaceholder.copy(
                            color = Color.White
                        ),
                        cursorBrush = SolidColor(Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isSearchActive && searchQuery.isNotEmpty()) {
            // Search results — flat alphabetical list
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = filteredApps.sortedBy { it.label.lowercase() },
                    key = { it.packageName }
                ) { app ->
                    AppSearchResultRow(
                        app = app,
                        iconBitmap = iconBitmaps[app.packageName],
                        onLaunch = { onLaunchApp(app.packageName) }
                    )
                }
            }
        } else {
            // Category grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Suggestions
                item {
                    CategoryTile(
                        categoryName = "Suggestions",
                        apps = allApps.take(4),
                        iconBitmaps = iconBitmaps,
                        onTap = { }
                    )
                }
                // Recently Added
                item {
                    CategoryTile(
                        categoryName = "Recently Added",
                        apps = allApps.sortedByDescending { it.installTime }.take(4),
                        iconBitmaps = iconBitmaps,
                        onTap = { }
                    )
                }
                // Dynamic categories
                items(
                    items = categorizedApps.entries.toList(),
                    key = { it.key }
                ) { (category, apps) ->
                    CategoryTile(
                        categoryName = category,
                        apps = apps.take(4),
                        iconBitmaps = iconBitmaps,
                        onTap = { }
                    )
                }
            }
        }
    }
}

@Composable
fun AppSearchResultRow(
    app: AppInfo,
    iconBitmap: Bitmap?,
    onLaunch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onLaunch() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconBitmap != null) {
            androidx.compose.foundation.Image(
                bitmap = iconBitmap.asImageBitmap(),
                contentDescription = app.label,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(27))
            )
        } else {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(27))
                    .background(Color(0xFF007AFF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = app.label.take(1).uppercase(),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = app.label,
            color = Color.White,
            fontSize = 16.sp,
            fontFamily = SFProFontFamily
        )
    }
}
