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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlin.math.PI
import kotlin.math.abs
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

enum class SkyStyle { DAY, EVENING, NIGHT }

/**
 * 하늘과 언덕 배경. 놀이에 방해가 되지 않도록 색은 평평하게, 움직임은 구름이 천천히 흐르는 정도로만 둡니다.
 * [SkyStyle.EVENING]은 해가 언덕 뒤로 지는 저녁(오늘 놀이 끝), [SkyStyle.NIGHT]는 별이 반짝이는 밤입니다.
 */
@Composable
fun SkyBackground(modifier: Modifier = Modifier, style: SkyStyle = SkyStyle.DAY) {
    val transition = rememberInfiniteTransition(label = "sky")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(120_000, easing = LinearEasing)),
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
        val night = style == SkyStyle.NIGHT
        drawRect(
            when (style) {
                SkyStyle.DAY -> KidsColors.Sky
                SkyStyle.EVENING -> KidsColors.Evening
                SkyStyle.NIGHT -> KidsColors.NightTop
            },
        )

        when (style) {
            SkyStyle.NIGHT -> {
                twinkles.forEach { star ->
                    val alpha = 0.35f + 0.65f * abs(sin(t * 2f * PI.toFloat() * star.speed + star.phase))
                    drawCircle(Color.White.copy(alpha = alpha), radius = star.size * density, center = Offset(star.x * w, star.y * h))
                }
                val moonCenter = Offset(w * 0.84f, h * 0.18f)
                val moonRadius = size.minDimension * 0.09f
                drawCircle(Color(0xFFFFF3B0), radius = moonRadius, center = moonCenter)
                drawCircle(KidsColors.NightTop, radius = moonRadius * 0.86f, center = moonCenter + Offset(moonRadius * 0.42f, -moonRadius * 0.2f))
            }
            SkyStyle.EVENING -> drawCircle(KidsColors.SettingSun, radius = size.minDimension * 0.15f, center = Offset(w * 0.8f, h * 0.5f))
            SkyStyle.DAY -> Unit
        }

        clouds.forEach { cloud ->
            val x = ((cloud.base + t * cloud.speed * 1.3f) % 1.3f - 0.15f) * w
            val alpha = if (night) 0.18f else 0.9f
            drawCloud(Offset(x, cloud.y * h), size.minDimension * 0.1f * cloud.scale, Color.White.copy(alpha = alpha))
        }

        val back = Path().apply {
            moveTo(0f, h * 0.8f)
            cubicTo(w * 0.2f, h * 0.72f, w * 0.42f, h * 0.72f, w * 0.62f, h * 0.79f)
            cubicTo(w * 0.78f, h * 0.85f, w * 0.9f, h * 0.76f, w, h * 0.78f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            back,
            when (style) {
                SkyStyle.DAY -> KidsColors.HillBack
                SkyStyle.EVENING -> Color(0xFFB7D98F)
                SkyStyle.NIGHT -> Color(0xFF2E5A4A)
            },
        )
        val front = Path().apply {
            moveTo(0f, h * 0.9f)
            cubicTo(w * 0.3f, h * 0.84f, w * 0.6f, h * 0.86f, w, h * 0.89f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            front,
            when (style) {
                SkyStyle.DAY -> KidsColors.Hill
                SkyStyle.EVENING -> Color(0xFF98C574)
                SkyStyle.NIGHT -> Color(0xFF3B6E57)
            },
        )
    }
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
