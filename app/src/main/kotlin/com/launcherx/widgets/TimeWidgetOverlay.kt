package com.launcherx.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.sp
import com.launcherx.ui.theme.SFProFontFamily
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TimeWidgetOverlay(
    offsetX: Float,
    offsetY: Float,
    scale: Float,
    widgetColor: Color,
    weatherEnabled: Boolean,
    weatherLocation: String,
    weatherTemperature: String,
    weatherCondition: String,
    isWeatherLoading: Boolean,
    onOffsetChange: (Float, Float) -> Unit,
    onScaleChange: (Float) -> Unit,
    onColorChange: (Long) -> Unit,
    onClockClick: () -> Unit,
    onWeatherClick: () -> Unit,
    onBoundsChanged: (Rect) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }
    var showColorPicker by remember { mutableStateOf(false) }

    val timeFormat = remember { SimpleDateFormat("h:mm", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("EEE, MMM d", Locale.getDefault()) }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            currentTime = timeFormat.format(now)
            currentDate = dateFormat.format(now)
            delay(1000)
        }
    }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    var widgetSize by remember { mutableStateOf(IntSize.Zero) }

    Box(modifier = modifier.fillMaxSize()) {
        // Time Widget
        Box(
            modifier = Modifier
                .onGloballyPositioned {
                    widgetSize = it.size
                    val position = it.positionInRoot()
                    onBoundsChanged(
                        Rect(
                            position,
                            Size(it.size.width.toFloat(), it.size.height.toFloat())
                        )
                    )
                }
                .offset { 
                    val finalX = if (offsetX < 0f && widgetSize.width > 0) {
                        ((screenWidthPx - widgetSize.width) / 2f).toInt()
                    } else if (offsetX >= 0f) {
                        offsetX.toInt()
                    } else {
                        // fallback before size is calculated, roughly center
                        (screenWidthPx / 2f - 200f).toInt()
                    }
                    IntOffset(finalX, offsetY.toInt()) 
                }
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        onOffsetChange(offsetX + dragAmount.x, offsetY + dragAmount.y)
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            showColorPicker = !showColorPicker
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentTime,
                    color = widgetColor,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = SFProFontFamily,
                    textAlign = TextAlign.Center,
                    letterSpacing = (-2).sp,
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onClockClick() },
                            onLongPress = { showColorPicker = !showColorPicker }
                        )
                    }
                )
                Text(
                    text = currentDate,
                    color = widgetColor.copy(alpha = 0.85f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = SFProFontFamily,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = when {
                        isWeatherLoading -> "Loading weather..."
                        weatherEnabled && weatherTemperature.isNotBlank() && weatherCondition.isNotBlank() ->
                            "$weatherTemperature  $weatherCondition"
                        else -> "Tap to get weather"
                    },
                    color = widgetColor.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = SFProFontFamily,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onWeatherClick() },
                            onLongPress = { showColorPicker = !showColorPicker }
                        )
                    }
                )
                if (weatherEnabled && weatherLocation.isNotBlank()) {
                    Text(
                        text = weatherLocation,
                        color = widgetColor.copy(alpha = 0.48f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = SFProFontFamily,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Color picker popup
        AnimatedVisibility(
            visible = showColorPicker,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            ColorPickerPopup(
                currentColor = widgetColor,
                onColorSelected = { colorLong ->
                    onColorChange(colorLong)
                    showColorPicker = false
                },
                onDismiss = { showColorPicker = false }
            )
        }
    }
}

@Composable
private fun ColorPickerPopup(
    currentColor: Color,
    onColorSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = listOf(
        0xFFFFFFFF to "White",
        0xFFFF3B30 to "Red",
        0xFF007AFF to "Blue",
        0xFF34C759 to "Green",
        0xFFFFCC00 to "Yellow",
        0xFFFF9500 to "Orange",
        0xFFFF2D92 to "Pink",
        0xFF5AC8FA to "Cyan",
        0xFF000000 to "Black"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1C1C1E).copy(alpha = 0.95f))
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Widget Color",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = SFProFontFamily
            )
            Spacer(modifier = Modifier.height(12.dp))
            // Color grid: 3 columns
            for (row in colors.chunked(3)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    row.forEach { (colorLong, name) ->
                        val color = Color(colorLong)
                        val isSelected = color == currentColor
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color)
                                .then(
                                    if (isSelected) Modifier.border(3.dp, Color.White, CircleShape)
                                    else if (colorLong == 0xFFFFFFFF.toLong()) Modifier.border(1.dp, Color.Gray, CircleShape)
                                    else Modifier
                                )
                                .pointerInput(colorLong) {
                                    detectTapGestures(onTap = { onColorSelected(colorLong) })
                                }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Dismiss",
                color = Color(0xFF007AFF),
                fontSize = 14.sp,
                fontFamily = SFProFontFamily,
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(onTap = { onDismiss() })
                }
            )
        }
    }
}
