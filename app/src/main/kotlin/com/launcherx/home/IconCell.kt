package com.launcherx.home

import android.graphics.Bitmap
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.launcherx.animations.AnimationSystem
import com.launcherx.ui.theme.LauncherColors
import com.launcherx.ui.theme.LauncherTypography

@Composable
fun IconCell(
    app: AppInfo,
    iconBitmap: Bitmap?,
    showLabel: Boolean = true,
    iconSize: Int = 60,
    isDragging: Boolean = false,
    onLaunch: () -> Unit,
    onRemove: () -> Unit = {},
    onUninstall: () -> Unit = {},
    onAppInfo: () -> Unit = {},
    onDragStart: (Offset) -> Unit = {},
    onDrag: (Offset) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onAddToHome: () -> Unit = {},
    isAppDrawer: Boolean = false,
    isAlreadyOnHome: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var showMenu by remember { mutableStateOf(false) }
    var globalPosition by remember { mutableStateOf(Offset.Zero) }

    // Press scale animation
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = AnimationSystem.iconPressSpec,
        label = "iconPress"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .height(if (showLabel) 95.dp else 75.dp)
            .onGloballyPositioned { coordinates ->
                globalPosition = coordinates.positionInRoot()
            }
            .alpha(if (isDragging) 0f else 1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Box(
            contentAlignment = Alignment.TopStart,
            modifier = Modifier.size(iconSize.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(iconSize.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(27),
                        ambientColor = LauncherColors.iconShadow,
                        spotColor = LauncherColors.iconShadow
                    )
                    .clip(RoundedCornerShape(27))
                    .pointerInput(app.packageName) {
                        detectTapGestures(
                            onTap = { onLaunch() }
                        )
                    }
                    .pointerInput(app.packageName) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { _ ->
                                showMenu = true
                            },
                            onDrag = { change, dragAmount ->
                                val distance = kotlin.math.sqrt(dragAmount.x * dragAmount.x + dragAmount.y * dragAmount.y)
                                if (showMenu && distance > 20f) {
                                    if (isAppDrawer) return@detectDragGesturesAfterLongPress
                                    
                                    // User is dragging significantly — close menu and start drag
                                    showMenu = false
                                    onDragStart(globalPosition + change.position)
                                }
                                if (!showMenu) {
                                    onDrag(globalPosition + change.position)
                                }
                                change.consume()
                            },
                            onDragEnd = {
                                if (!showMenu) onDragEnd()
                            },
                            onDragCancel = {
                                if (!showMenu) onDragEnd()
                            }
                        )
                    }
            ) {
                if (iconBitmap != null) {
                    Image(
                        bitmap = iconBitmap.asImageBitmap(),
                        contentDescription = app.label,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF007AFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = app.label.take(1).uppercase(),
                            color = Color.White,
                            style = LauncherTypography.lockScreenDate
                        )
                    }
                }
            }

            // Context Menu
            androidx.compose.material3.DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(Color.White)
            ) {
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text("App Info", color = Color.Black) },
                    onClick = { 
                        showMenu = false
                        onAppInfo() 
                    }
                )
                if (!isAppDrawer) {
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text("Remove", color = Color.Black) },
                        onClick = { 
                            showMenu = false
                            onRemove() 
                        }
                    )
                } else if (!isAlreadyOnHome) {
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text("Add to Home Screen", color = Color.Black) },
                        onClick = { 
                            showMenu = false
                            onAddToHome() 
                        }
                    )
                }
                if (!app.isSystemApp) {
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text("Uninstall", color = LauncherColors.deleteRed) },
                        onClick = { 
                            showMenu = false
                            onUninstall() 
                        }
                    )
                }
            }
        }

        // Label
        if (showLabel) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = app.label,
                style = LauncherTypography.iconLabel,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 76.dp)
            )
        }
    }
}
