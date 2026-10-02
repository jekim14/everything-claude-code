package com.ssukssuk.playground.ui.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput

/** 손가락으로 그린 한 획 */
class PaintStroke(
    val color: Color,
    val width: Float,
    val rainbow: Boolean = false,
) {
    val points = mutableStateListOf<Offset>()
}

fun DrawScope.drawPaintStroke(stroke: PaintStroke) {
    val points = stroke.points
    if (points.isEmpty()) return
    if (points.size == 1) {
        drawCircle(
            color = if (stroke.rainbow) Color.hsv(0f, 0.8f, 0.95f) else stroke.color,
            radius = stroke.width / 2f,
            center = points[0],
        )
        return
    }
    if (stroke.rainbow) {
        for (i in 1 until points.size) {
            drawLine(
                color = Color.hsv((i * 7f) % 360f, 0.75f, 0.97f),
                start = points[i - 1],
                end = points[i],
                strokeWidth = stroke.width,
                cap = StrokeCap.Round,
            )
        }
    } else {
        val path = Path().apply {
            moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) lineTo(points[i].x, points[i].y)
        }
        drawPath(
            path = path,
            color = stroke.color,
            style = Stroke(width = stroke.width, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

/**
 * 손가락 그리기 입력.
 * [onStart]가 새 획을 돌려주면 손가락이 움직이는 동안 점을 이어 붙입니다.
 * 도장 찍기처럼 획이 필요 없으면 null을 돌려주세요.
 * 람다가 붙잡은 값이 바뀌면(예: 새 획 목록) [key]를 바꿔 입력 처리를 다시 시작하세요.
 */
fun Modifier.paintInput(
    key: Any? = Unit,
    onStart: (Offset) -> PaintStroke?,
    onEnd: () -> Unit = {},
): Modifier =
    pointerInput(key) {
        awaitEachGesture {
            val down = awaitFirstDown()
            val stroke = onStart(down.position) ?: return@awaitEachGesture
            stroke.points.add(down.position)
            drag(down.id) { change ->
                stroke.points.add(change.position)
                change.consume()
            }
            onEnd()
        }
    }
