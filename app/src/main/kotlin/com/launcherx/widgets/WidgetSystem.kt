package com.launcherx.widgets

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.launcherx.ui.theme.LauncherColors
import com.launcherx.ui.theme.SFProFontFamily
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

enum class WidgetSize(val cols: Int, val rows: Int) {
    SMALL(2, 2),
    MEDIUM(4, 2),
    LARGE(4, 4)
}

@Composable
fun ClockWidget(
    size: WidgetSize = WidgetSize.MEDIUM,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }
    val timeFormat = remember { SimpleDateFormat("h:mm", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()) }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            currentTime = timeFormat.format(now)
            currentDate = dateFormat.format(now)
            delay(1000)
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(LauncherColors.widgetBackground)
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(
                text = currentTime,
                color = Color.White,
                fontSize = if (size == WidgetSize.LARGE) 54.sp else 38.sp,
                fontWeight = FontWeight.Light,
                fontFamily = SFProFontFamily
            )
            Text(
                text = currentDate,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = SFProFontFamily
            )
        }
    }
}

@Composable
fun WeatherWidget(
    size: WidgetSize = WidgetSize.SMALL,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(LauncherColors.widgetBackground)
            .padding(16.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Column {
            Text(
                text = "My Location",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontFamily = SFProFontFamily
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "72°",
                color = Color.White,
                fontSize = if (size == WidgetSize.SMALL) 36.sp else 48.sp,
                fontWeight = FontWeight.Light,
                fontFamily = SFProFontFamily
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "☀️ Sunny",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                fontFamily = SFProFontFamily
            )
        }
    }
}

@Composable
fun BatteryWidget(
    size: WidgetSize = WidgetSize.SMALL,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val batteryLevel = remember {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        if (scale > 0) (level * 100 / scale) else 100
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(LauncherColors.widgetBackground)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(if (size == WidgetSize.SMALL) 60.dp else 80.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 8f
                    val sweepAngle = 360f * batteryLevel / 100f
                    val arcSize = this.size.minDimension - strokeWidth

                    // Background ring
                    drawArc(
                        color = Color.White.copy(alpha = 0.2f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    // Battery level ring
                    val ringColor = when {
                        batteryLevel > 50 -> Color(0xFF34C759)
                        batteryLevel > 20 -> Color(0xFFFF9500)
                        else -> Color(0xFFFF3B30)
                    }
                    drawArc(
                        color = ringColor,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = "$batteryLevel%",
                    color = Color.White,
                    fontSize = if (size == WidgetSize.SMALL) 16.sp else 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = SFProFontFamily
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Battery",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontFamily = SFProFontFamily
            )
        }
    }
}

@Composable
fun CalendarWidget(
    size: WidgetSize = WidgetSize.SMALL,
    modifier: Modifier = Modifier
) {
    val calendar = remember { Calendar.getInstance() }
    val today = remember { calendar.get(Calendar.DAY_OF_MONTH) }
    val monthName = remember { SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time) }
    val daysInMonth = remember { calendar.getActualMaximum(Calendar.DAY_OF_MONTH) }

    val firstDayOfMonth = remember {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.get(Calendar.DAY_OF_WEEK) - 1
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(LauncherColors.widgetBackground)
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = monthName.uppercase(),
                color = Color(0xFFFF3B30),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = SFProFontFamily
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Day names row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                    Text(
                        text = day,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 8.sp,
                        fontFamily = SFProFontFamily,
                        modifier = Modifier.width(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Calendar grid
            var dayCounter = 1
            for (week in 0..5) {
                if (dayCounter > daysInMonth) break
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (dayOfWeek in 0..6) {
                        if ((week == 0 && dayOfWeek < firstDayOfMonth) || dayCounter > daysInMonth) {
                            Box(modifier = Modifier.size(14.dp))
                        } else {
                            val isToday = dayCounter == today
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .then(
                                        if (isToday)
                                            Modifier.background(Color(0xFFFF3B30), RoundedCornerShape(50))
                                        else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayCounter.toString(),
                                    color = if (isToday) Color.White else Color.White.copy(alpha = 0.8f),
                                    fontSize = 8.sp,
                                    fontFamily = SFProFontFamily
                                )
                            }
                            dayCounter++
                        }
                    }
                }
            }
        }
    }
}
