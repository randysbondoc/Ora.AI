package com.rbtech.henyotalk.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row // Added missing import
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rbtech.henyotalk.ui.theme.HenyoTalkTheme

@Composable
fun GlowingOrb(
    modifier: Modifier = Modifier,
    color: Color,
    isSpeaking: Boolean = false,
    isListening: Boolean = false
) {
    // Animation for the overall size (scale) of the orb
    val scale = remember { Animatable(1f) }
    LaunchedEffect(isSpeaking, isListening) {
        scale.animateTo(
            targetValue = when {
                isSpeaking -> 1.2f
                isListening -> 0.8f
                else -> 1f
            },
            animationSpec = tween(durationMillis = 500)
        )
    }

    // Continuous pulsing animation for the glow when speaking
    val infiniteTransition = rememberInfiniteTransition(label = "orb-pulse")
    val pulse = if (isSpeaking) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ), label = "pulse"
        ).value
    } else {
        1f
    }

    Canvas(
        modifier = modifier
            .size(120.dp)
            .scale(scale.value)
    ) {
        val radius = (size.minDimension / 2) * pulse

        // The glow effect
        val glowBrush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = 0.5f), Color.Transparent),
            center = center,
            radius = radius
        )
        drawCircle(brush = glowBrush)

        // The solid core of the orb
        drawCircle(
            color = color,
            radius = radius / 2
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GlowingOrbStatesPreview() {
    // Wrapped the Row in the theme to fix the Composable context error
    HenyoTalkTheme {
        Row {
            GlowingOrb(color = Color.Blue, isSpeaking = true)
            GlowingOrb(color = Color.Red, isListening = true)
            GlowingOrb(color = Color.Green)
        }
    }
}