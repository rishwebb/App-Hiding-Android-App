package com.launcherx.vault

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.launcherx.ui.theme.LauncherColors
import com.launcherx.ui.theme.SFProFontFamily

@Composable
fun PinScreen(
    title: String = "Enter Passcode",
    onPinEntered: (String) -> Unit,
    onCancel: () -> Unit,
    onForgotPin: (() -> Unit)? = null,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    var pin by remember { mutableStateOf("") }

    // Shake animation for wrong PIN
    val shakeOffset by animateFloatAsState(
        targetValue = if (isError) 15f else 0f,
        animationSpec = spring(stiffness = 2000f, dampingRatio = 0.3f),
        label = "shake"
    )

    LaunchedEffect(isError) {
        if (isError) {
            kotlinx.coroutines.delay(500)
            pin = ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xCC000000))
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Light,
            fontFamily = SFProFontFamily
        )

        Spacer(modifier = Modifier.height(24.dp))

        // PIN dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.graphicsLayer { translationX = shakeOffset }
        ) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(
                            if (index < pin.length) Color.White
                            else Color.White.copy(alpha = 0.3f)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Numeric keypad — iOS style
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("", "0", "⌫")
        )

        keys.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                row.forEach { key ->
                    if (key.isEmpty()) {
                        Spacer(modifier = Modifier.size(70.dp))
                    } else {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(LauncherColors.pinButtonBackground)
                                .clickable {
                                    when (key) {
                                        "⌫" -> {
                                            if (pin.isNotEmpty()) pin = pin.dropLast(1)
                                        }
                                        else -> {
                                            if (pin.length < 4) {
                                                pin += key
                                                if (pin.length == 4) {
                                                    onPinEntered(pin)
                                                }
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = key,
                                color = Color.White,
                                fontSize = if (key == "⌫") 20.sp else 28.sp,
                                fontWeight = FontWeight.Light,
                                textAlign = TextAlign.Center,
                                fontFamily = SFProFontFamily
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Spacer(modifier = Modifier.height(24.dp))

        if (onForgotPin != null) {
            Text(
                text = "Forgot Passcode?",
                color = LauncherColors.deleteRed,
                fontSize = 16.sp,
                fontFamily = SFProFontFamily,
                modifier = Modifier.clickable { onForgotPin() }.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            text = "Cancel",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 16.sp,
            fontFamily = SFProFontFamily,
            modifier = Modifier.clickable { onCancel() }.padding(vertical = 8.dp)
        )
    }
}
