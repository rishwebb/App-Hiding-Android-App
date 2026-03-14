package com.launcherx.lockscreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.launcherx.ui.theme.SFProFontFamily
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LockScreenOverlay(
    isVisible: Boolean,
    onUnlock: () -> Unit,
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

    // Slide-up dismiss animation
    val screenHeightPx = with(LocalDensity.current) {
        LocalConfiguration.current.screenHeightDp.dp.toPx()
    }

    val offsetY by animateFloatAsState(
        targetValue = if (isVisible) 0f else -screenHeightPx,
        animationSpec = spring(stiffness = 350f, dampingRatio = 0.8f),
        label = "lockScreenDismiss",
        finishedListener = {
            if (!isVisible) onUnlock()
        }
    )

    var isDismissing by remember { mutableStateOf(false) }
    var swipeProgress by remember { mutableFloatStateOf(0f) }

    // Pulse animation for "Swipe up to unlock"
    val infiniteAlpha = remember { mutableFloatStateOf(1f) }
    LaunchedEffect(Unit) {
        while (true) {
            infiniteAlpha.floatValue = 0.4f
            delay(1200)
            infiniteAlpha.floatValue = 1f
            delay(1200)
        }
    }

    if (isVisible || offsetY > -screenHeightPx) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .graphicsLayer { translationY = offsetY }
                .background(Color(0xFF1C1C1E))
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            if (swipeProgress < -200f) {
                                isDismissing = true
                                onUnlock()
                            }
                            swipeProgress = 0f
                        },
                        onVerticalDrag = { _, dragAmount ->
                            swipeProgress += dragAmount
                        }
                    )
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                // Date
                Text(
                    text = currentDate,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = SFProFontFamily,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Time — large
                Text(
                    text = currentTime,
                    color = Color.White,
                    fontSize = 70.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = SFProFontFamily,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.weight(1f))

                // "Swipe up to unlock"
                Text(
                    text = "Swipe up to unlock",
                    color = Color.White.copy(alpha = infiniteAlpha.floatValue * 0.7f),
                    fontSize = 15.sp,
                    fontFamily = SFProFontFamily,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Home indicator
                Box(
                    modifier = Modifier
                        .width(134.dp)
                        .height(5.dp)
                        .background(
                            Color(0xCCFFFFFF),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(2.5.dp)
                        )
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
