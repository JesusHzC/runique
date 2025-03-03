package com.jesushz.wear.run.presentation.ambient

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.withSaveLayer
import kotlin.random.Random

fun Modifier.ambientMode(
    isAmbientMode: Boolean,
    burnInActionRequired: Boolean
) = composed {
    val translationX = rememberBurnInTranslation(
        isAmbientMode = isAmbientMode,
        burnInActionRequired = burnInActionRequired
    )
    val translationY = rememberBurnInTranslation(
        isAmbientMode = isAmbientMode,
        burnInActionRequired = burnInActionRequired
    )
    this
        .graphicsLayer {
            this.translationX = translationX.value
            this.translationY = translationY.value
        }
        .ambientGray(isAmbientMode)
}

internal fun Modifier.ambientGray(isAmbientMode: Boolean): Modifier {
    return if (isAmbientMode) {
        val grayScale = Paint().apply {
            colorFilter = ColorFilter.colorMatrix(
                colorMatrix = ColorMatrix().apply {
                    setToSaturation(0f)
                }
            )
        }

        drawWithContent {
            drawIntoCanvas {
                it.withSaveLayer(size.toRect(), grayScale) {
                    drawContent()
                }
            }
        }
    } else {
        this
    }
}

@Composable
private fun rememberBurnInTranslation(
    isAmbientMode: Boolean,
    burnInActionRequired: Boolean
): State<Float> {
    val translation = remember {
        Animatable(0f)
    }

    LaunchedEffect(isAmbientMode) {
        if (isAmbientMode && burnInActionRequired) {
            translation.animateTo(
                targetValue = Random.nextInt(-10, 10).toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 60_000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            translation.snapTo(0f)
        }
    }

    return translation.asState()
}