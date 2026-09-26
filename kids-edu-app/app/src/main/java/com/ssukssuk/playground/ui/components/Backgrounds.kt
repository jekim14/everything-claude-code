package com.ssukssuk.playground.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private class Twinkle(val x: Float, val y: Float, val size: Float, val phase: Float, val speed: Int)

private data class CloudSpec(val base: Float, val y: Float, val scale: Float, val speed: Int)

private val clouds = listOf(
    CloudSpec(base = 0.05f, y = 0.12f, scale = 1.1f, speed = 1),
    CloudSpec(base = 0.55f, y = 0.24f, scale = 0.8f, speed = 2),
    CloudSpec(base = 0.95f, y = 0.08f, scale = 0.65f, speed = 1),
    CloudSpec(base = 0.35f, y = 0.34f, scale = 0.55f, speed = 2),
)

/** 해가 빙글 돌고 구름이 흘러가는 하늘과 언덕 배경. [night]이면 달과 반짝이는 별이 나옵니다. */
@Composable
fun SkyBackground(modifier: Modifier = Modifier, night: Boolean = false) {
    val transition = rememberInfiniteTransition(label = "sky")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(90_000, easing = LinearEasing)),
        label = "skyTime",
    )
    val twinkles = remember {
        val random = Random(42)
        List(46) {
            Twinkle(
                x = random.nextFloat(),
                y = random.nextFloat() * 0.62f,
                size = 1.5f + random.nextFloat() * 2.5f,
                phase = random.nextFloat() * 6.28f,
                speed = 20 + random.nextInt(40),
            )
        }
    }
    Canvas(modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawRect(
            Brush.verticalGradient(
                if (night) listOf(KidsColors.NightTop, KidsColors.NightBottom) else listOf(KidsColors.SkyTop, KidsColors.SkyBottom),
            ),
        )

        if (night) {
            twinkles.forEach { star ->
                val alpha = 0.35f + 0.65f * abs(sin(t * 2f * PI.toFloat() * star.speed + star.phase))
                drawCircle(Color.White.copy(alpha = alpha), radius = star.size * density, center = Offset(star.x * w, star.y * h))
            }
            val moonCenter = Offset(w * 0.84f, h * 0.18f)
            val moonRadius = size.minDimension * 0.09f
            drawCircle(Color(0xFFFFF3B0), radius = moonRadius, center = moonCenter)
            drawCircle(KidsColors.NightTop, radius = moonRadius * 0.86f, center = moonCenter + Offset(moonRadius * 0.42f, -moonRadius * 0.2f))
        } else {
            drawSun(Offset(w * 0.87f, h * 0.17f), size.minDimension * 0.085f, t * 360f * 4f)
        }

        clouds.forEach { cloud ->
            val x = ((cloud.base + t * cloud.speed * 1.3f) % 1.3f - 0.15f) * w
            val alpha = if (night) 0.18f else 0.92f
            drawCloud(Offset(x, cloud.y * h), size.minDimension * 0.12f * cloud.scale, Color.White.copy(alpha = alpha))
        }

        val back = Path().apply {
            moveTo(0f, h * 0.8f)
            cubicTo(w * 0.2f, h * 0.68f, w * 0.42f, h * 0.68f, w * 0.62f, h * 0.77f)
            cubicTo(w * 0.78f, h * 0.84f, w * 0.9f, h * 0.76f, w, h * 0.72f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(back, if (night) Color(0xFF2E5A4A) else KidsColors.HillDark)
        val front = Path().apply {
            moveTo(0f, h * 0.88f)
            cubicTo(w * 0.3f, h * 0.78f, w * 0.6f, h * 0.8f, w, h * 0.9f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(front, if (night) Color(0xFF3B6E57) else KidsColors.Hill)

        if (!night) {
            val sway = sin(t * 2f * PI.toFloat() * 30f) * 8f
            listOf(0.08f, 0.2f, 0.33f, 0.71f, 0.83f, 0.95f).forEachIndexed { i, fx ->
                val colors = listOf(Color(0xFFFF8A80), Color(0xFFFFD54F), Color(0xFFCE93D8))
                drawFlower(Offset(fx * w, h * (0.9f + (i % 2) * 0.03f)), size.minDimension * 0.022f, colors[i % colors.size], sway * if (i % 2 == 0) 1f else -1f)
            }
        }
    }
}

private fun DrawScope.drawSun(center: Offset, radius: Float, rotation: Float) {
    rotate(rotation, pivot = center) {
        repeat(12) { i ->
            val angle = i * PI.toFloat() / 6f
            val start = center + Offset(cos(angle), sin(angle)) * (radius * 1.25f)
            val end = center + Offset(cos(angle), sin(angle)) * (radius * 1.65f)
            drawLine(KidsColors.Sun, start, end, strokeWidth = radius * 0.16f, cap = StrokeCap.Round)
        }
    }
    drawCircle(Color(0xFFFFE082), radius = radius * 1.12f, center = center)
    drawCircle(KidsColors.Sun, radius = radius, center = center)
}

fun DrawScope.drawCloud(center: Offset, unit: Float, color: Color) {
    drawCircle(color, radius = unit, center = center)
    drawCircle(color, radius = unit * 0.75f, center = center + Offset(-unit * 0.95f, unit * 0.25f))
    drawCircle(color, radius = unit * 0.8f, center = center + Offset(unit * 0.95f, unit * 0.2f))
    drawRoundRect(
        color,
        topLeft = Offset(center.x - unit * 1.6f, center.y + unit * 0.1f),
        size = Size(unit * 3.2f, unit * 0.9f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(unit * 0.45f),
    )
}

private fun DrawScope.drawFlower(base: Offset, unit: Float, color: Color, sway: Float) {
    rotate(sway, pivot = base) {
        val head = base + Offset(0f, -unit * 3.2f)
        drawLine(KidsColors.Leaf, base, head, strokeWidth = unit * 0.45f, cap = StrokeCap.Round)
        repeat(5) { i ->
            val angle = i * 2f * PI.toFloat() / 5f
            drawCircle(color, radius = unit * 0.75f, center = head + Offset(cos(angle), sin(angle)) * unit)
        }
        drawCircle(Color(0xFFFFF59D), radius = unit * 0.6f, center = head)
    }
}
