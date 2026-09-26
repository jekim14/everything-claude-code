package com.ssukssuk.playground.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import com.ssukssuk.playground.content.ShapeKind
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** [size] 크기의 상자 안에 들어가는 도형 경로 */
fun shapePath(kind: ShapeKind, size: Size, origin: Offset = Offset.Zero): Path {
    val w = size.width
    val h = size.height
    val x = origin.x
    val y = origin.y
    return when (kind) {
        ShapeKind.CIRCLE -> Path().apply { addOval(Rect(x, y, x + w, y + h)) }
        ShapeKind.SQUARE -> Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(x + w * 0.04f, y + h * 0.04f, x + w * 0.96f, y + h * 0.96f),
                    cornerRadius = CornerRadius(w * 0.12f, h * 0.12f),
                ),
            )
        }
        ShapeKind.TRIANGLE -> Path().apply {
            moveTo(x + w * 0.5f, y + h * 0.04f)
            lineTo(x + w * 0.97f, y + h * 0.92f)
            lineTo(x + w * 0.03f, y + h * 0.92f)
            close()
        }
        ShapeKind.STAR -> starPath(
            center = Offset(x + w / 2f, y + h * 0.53f),
            outerRadius = w * 0.5f,
            innerRadius = w * 0.21f,
        )
        ShapeKind.HEART -> Path().apply {
            moveTo(x + w * 0.5f, y + h * 0.28f)
            cubicTo(x + w * 0.5f, y + h * 0.02f, x + w * 0.02f, y + h * 0.02f, x + w * 0.02f, y + h * 0.36f)
            cubicTo(x + w * 0.02f, y + h * 0.62f, x + w * 0.32f, y + h * 0.78f, x + w * 0.5f, y + h * 0.96f)
            cubicTo(x + w * 0.68f, y + h * 0.78f, x + w * 0.98f, y + h * 0.62f, x + w * 0.98f, y + h * 0.36f)
            cubicTo(x + w * 0.98f, y + h * 0.02f, x + w * 0.5f, y + h * 0.02f, x + w * 0.5f, y + h * 0.28f)
            close()
        }
        ShapeKind.DIAMOND -> Path().apply {
            moveTo(x + w * 0.5f, y + h * 0.02f)
            lineTo(x + w * 0.94f, y + h * 0.5f)
            lineTo(x + w * 0.5f, y + h * 0.98f)
            lineTo(x + w * 0.06f, y + h * 0.5f)
            close()
        }
    }
}

/** 다섯 꼭짓점 별 */
fun starPath(center: Offset, outerRadius: Float, innerRadius: Float, points: Int = 5): Path = Path().apply {
    val step = PI / points
    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) outerRadius else innerRadius
        val angle = -PI / 2 + i * step
        val px = center.x + (cos(angle) * r).toFloat()
        val py = center.y + (sin(angle) * r).toFloat()
        if (i == 0) moveTo(px, py) else lineTo(px, py)
    }
    close()
}
