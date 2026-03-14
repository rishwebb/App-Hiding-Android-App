package com.launcherx.animations

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

object AnimationSystem {

    // Icon press spring
    val iconPressSpec: AnimationSpec<Float> = spring(
        stiffness = 300f,
        dampingRatio = 0.7f
    )

    // Page swipe spring
    val pageSwipeSpec: AnimationSpec<Float> = spring(
        stiffness = 200f,
        dampingRatio = 0.9f
    )

    // App launch zoom
    val appLaunchSpec: AnimationSpec<Float> = spring(
        stiffness = 250f,
        dampingRatio = 0.8f
    )

    // Folder open spring
    val folderOpenSpec: AnimationSpec<Float> = spring(
        stiffness = 250f,
        dampingRatio = 0.75f
    )

    // Sheet presentation spring
    val sheetPresentSpec: AnimationSpec<Float> = spring(
        stiffness = 400f,
        dampingRatio = 0.85f
    )

    // Lock screen dismiss spring
    val lockScreenDismissSpec: AnimationSpec<Float> = spring(
        stiffness = 350f,
        dampingRatio = 0.8f
    )

    // Jiggle animation
    val jiggleDurationMs = 300
    val jiggleAngle = 2.5f

    @Composable
    fun rememberJiggleRotation(
        isJiggling: Boolean,
        phaseOffsetMs: Int = 0
    ): State<Float> {
        val infiniteTransition = rememberInfiniteTransition(label = "jiggle")
        return infiniteTransition.animateFloat(
            initialValue = -jiggleAngle,
            targetValue = jiggleAngle,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = jiggleDurationMs,
                    easing = LinearEasing,
                    delayMillis = phaseOffsetMs
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "jiggleRotation"
        )
    }

    fun Modifier.pressScale(pressed: Boolean, scale: Float): Modifier {
        return this.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    }
}
