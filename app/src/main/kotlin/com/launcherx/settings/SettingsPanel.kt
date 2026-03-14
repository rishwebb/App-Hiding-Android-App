package com.launcherx.settings

import android.os.Process
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dock
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.launcherx.BuildConfig
import com.launcherx.ui.theme.LauncherColors
import com.launcherx.ui.theme.SFProFontFamily

@Composable
fun SettingsPanel(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onResetLayout: () -> Unit,
    onResetDock: () -> Unit,
    onUninstallLauncher: () -> Unit,
    onChangeDefaultLauncher: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000))
                    .clickable { onDismiss() }
            )

            // Settings sheet
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(LauncherColors.settingsBackground)
                    .padding(top = 12.dp)
            ) {
                // Handle bar
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(5.dp)
                            .background(Color(0xFFDDDDDD), RoundedCornerShape(2.5.dp))
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = "Launcher Settings",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SFProFontFamily,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    // Layout section
                    item {
                        SectionHeader("Layout")
                    }
                    item {
                        SettingsRow(
                            icon = Icons.Filled.GridView,
                            title = "Reset Home Screen Layout",
                            onClick = { showResetConfirmDialog = true }
                        )
                    }
                    item {
                        SettingsRow(
                            icon = Icons.Filled.Dock,
                            title = "Reset Dock",
                            onClick = onResetDock
                        )
                    }

                    // Emergency Exit section
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        SectionHeader("Emergency Exit")
                    }
                    item {
                        SettingsRow(
                            icon = Icons.Filled.DeleteForever,
                            title = "Uninstall LauncherX",
                            subtitle = "Remove this app completely",
                            isDestructive = true,
                            onClick = onUninstallLauncher
                        )
                    }
                    item {
                        SettingsRow(
                            icon = Icons.Filled.Home,
                            title = "Change Default Launcher",
                            subtitle = "Switch back to your system launcher",
                            onClick = onChangeDefaultLauncher
                        )
                    }

                    // About section
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        SectionHeader("About")
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = "LauncherX v${BuildConfig.VERSION_NAME}",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontFamily = SFProFontFamily
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }

    // Confirm dialogs
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Reset Layout?") },
            text = { Text("This will reset all icon positions to their defaults.") },
            confirmButton = {
                TextButton(onClick = {
                    onResetLayout()
                    showResetConfirmDialog = false
                }) {
                    Text("Reset", color = Color(0xFFFF3B30))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }


}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.Gray,
        fontFamily = SFProFontFamily,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF2F2F7))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isDestructive) Color(0xFFFF3B30) else Color(0xFF007AFF),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = if (isDestructive) Color(0xFFFF3B30) else Color.Black,
                fontFamily = SFProFontFamily
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontFamily = SFProFontFamily
                )
            }
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFC7C7CC),
            modifier = Modifier.size(18.dp)
        )
    }
    Spacer(modifier = Modifier.height(2.dp))
}
