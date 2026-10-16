package com.frogsudoku.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.frogsudoku.app.ui.theme.BlushPink
import com.frogsudoku.app.ui.theme.DeepFrogGreen
import com.frogsudoku.app.ui.theme.SageGreen

@Composable
fun FrogLifeView(isAlive: Boolean, size: Dp = 44.dp) {
    val shakeAnim = remember { Animatable(0f) }
    LaunchedEffect(isAlive) {
        if (!isAlive) {
            shakeAnim.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 450
                    -18f at 80; 18f at 160; -12f at 240; 12f at 320; 0f at 450
                }
            )
        }
    }

    val bodyColor by animateColorAsState(if (isAlive) SageGreen else Color(0xFFD3D8D4), label = "")
    val bellyColor by animateColorAsState(if (isAlive) Color(0xFFE9F2E6) else Color(0xFFE6EAE7), label = "")
    val outlineColor by animateColorAsState(if (isAlive) DeepFrogGreen else Color(0xFF8C968E), label = "")

    Canvas(
        modifier = Modifier.size(size).graphicsLayer {
            rotationZ = shakeAnim.value
            alpha = if (isAlive) 1f else 0.65f
        }
    ) {
        val w = this.size.width
        val h = this.size.height
        val eyeRadius = w * 0.17f
        val leftEye = Offset(w * 0.28f, h * 0.26f)
        val rightEye = Offset(w * 0.72f, h * 0.26f)

        drawCircle(bodyColor, eyeRadius, leftEye)
        drawCircle(outlineColor, eyeRadius, leftEye, style = Stroke(3.5f))
        drawCircle(bodyColor, eyeRadius, rightEye)
        drawCircle(outlineColor, eyeRadius, rightEye, style = Stroke(3.5f))

        drawOval(bodyColor, Offset(w * 0.08f, h * 0.22f), Size(w * 0.84f, h * 0.70f))
        drawOval(outlineColor, Offset(w * 0.08f, h * 0.22f), Size(w * 0.84f, h * 0.70f), style = Stroke(3.5f))
        drawOval(bellyColor, Offset(w * 0.22f, h * 0.48f), Size(w * 0.56f, h * 0.38f))

        if (isAlive) {
            drawCircle(Color(0xFF1F2821), eyeRadius * 0.52f, leftEye)
            drawCircle(Color.White, eyeRadius * 0.20f, leftEye - Offset(2f, 2f))
            drawCircle(Color(0xFF1F2821), eyeRadius * 0.52f, rightEye)
            drawCircle(Color.White, eyeRadius * 0.20f, rightEye - Offset(2f, 2f))

            drawOval(BlushPink.copy(alpha = 0.75f), Offset(w * 0.14f, h * 0.42f), Size(w * 0.16f, h * 0.10f))
            drawOval(BlushPink.copy(alpha = 0.75f), Offset(w * 0.70f, h * 0.42f), Size(w * 0.16f, h * 0.10f))

            val smile = Path().apply {
                moveTo(w * 0.38f, h * 0.44f)
                quadraticBezierTo(w * 0.50f, h * 0.54f, w * 0.62f, h * 0.44f)
            }
            drawPath(smile, outlineColor, style = Stroke(3.5f, cap = StrokeCap.Round))
        } else {
            val d = eyeRadius * 0.42f
            listOf(leftEye, rightEye).forEach { c ->
                drawLine(outlineColor, c - Offset(d, d), c + Offset(d, d), 3.5f, StrokeCap.Round)
                drawLine(outlineColor, Offset(c.x + d, c.y - d), Offset(c.x - d, c.y + d), 3.5f, StrokeCap.Round)
            }
            val sad = Path().apply {
                moveTo(w * 0.40f, h * 0.50f)
                quadraticBezierTo(w * 0.50f, h * 0.43f, w * 0.60f, h * 0.50f)
            }
            drawPath(sad, outlineColor, style = Stroke(3.5f, cap = StrokeCap.Round))
        }
    }
}
