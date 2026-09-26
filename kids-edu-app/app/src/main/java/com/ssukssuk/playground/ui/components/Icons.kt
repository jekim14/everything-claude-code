package com.ssukssuk.playground.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.core.Game

/**
 * 선으로 그린 단순한 아이콘. 디자인 시안(56×56 격자, 3.5 굵기 선)을 그대로 옮겼습니다.
 * 그림 문자(이모지)는 기기마다 모양이 달라, 홈 타일과 버튼은 이 아이콘을 씁니다.
 */
object KidIcons {
    private fun circle(cx: Float, cy: Float, r: Float) =
        "M${cx - r},$cy a$r,$r 0 1,0 ${2 * r},0 a$r,$r 0 1,0 ${-2 * r},0 Z"

    private fun ellipse(cx: Float, cy: Float, rx: Float, ry: Float) =
        "M${cx - rx},$cy a$rx,$ry 0 1,0 ${2 * rx},0 a$rx,$ry 0 1,0 ${-2 * rx},0 Z"

    private fun roundRect(x: Float, y: Float, w: Float, h: Float, r: Float) =
        "M${x + r},$y h${w - 2 * r} a$r,$r 0 0 1 $r,$r v${h - 2 * r} a$r,$r 0 0 1 ${-r},$r " +
            "h${-(w - 2 * r)} a$r,$r 0 0 1 ${-r},${-r} v${-(h - 2 * r)} a$r,$r 0 0 1 $r,${-r} Z"

    /** 한 아이콘을 이루는 선 하나. [filled]면 안을 칠하고, [rotate]는 [pivotX],[pivotY] 기준 회전 */
    private class Part(
        val d: String,
        val filled: Boolean = false,
        val rotate: Float = 0f,
        val pivotX: Float = 0f,
        val pivotY: Float = 0f,
        val width: Float = 3.5f,
    )

    private fun build(name: String, viewport: Float, color: Color, parts: List<Part>): ImageVector {
        val builder = ImageVector.Builder(
            name = name,
            defaultWidth = viewport.dp,
            defaultHeight = viewport.dp,
            viewportWidth = viewport,
            viewportHeight = viewport,
        )
        parts.forEach { part ->
            if (part.rotate != 0f) builder.addGroup(rotate = part.rotate, pivotX = part.pivotX, pivotY = part.pivotY)
            builder.addPath(
                pathData = PathParser().parsePathString(part.d).toNodes(),
                fill = if (part.filled) SolidColor(color) else null,
                stroke = SolidColor(color),
                strokeLineWidth = part.width,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
            if (part.rotate != 0f) builder.clearGroup()
        }
        return builder.build()
    }

    private fun parts(game: Game): List<Part>? = when (game) {
        Game.HANGUL, Game.SYLLABLE -> null
        Game.COUNTING -> listOf(
            Part("M28 18c-4-4-14-4-16 6-2 12 6 24 12 24 2 0 3-1 4-1s2 1 4 1c6 0 14-12 12-24-2-10-12-10-16-6z"),
            Part("M28 18c0-5 2-8 6-10"),
        )
        Game.NUMBER_PATH -> listOf(
            Part(ellipse(8f, 42f, 7f, 4.5f)),
            Part(ellipse(28f, 42f, 7f, 4.5f)),
            Part(ellipse(48f, 42f, 7f, 4.5f)),
            Part("M8 30 Q18 12 28 30 Q38 12 48 30", width = 2.6f),
        )
        Game.SHAPES -> listOf(
            Part("M14 6 L24 24 H4 Z"),
            Part(circle(42f, 15f, 9f)),
            Part(roundRect(18f, 32f, 20f, 18f, 3f)),
        )
        Game.PATTERN -> listOf(
            Part(circle(9f, 26f, 6f)),
            Part(roundRect(22f, 20f, 12f, 12f, 2f)),
            Part(circle(47f, 26f, 6f)),
            Part("M6 44 H10 M20 44 H24 M34 44 H38 M48 44 H52"),
        )
        Game.MEMORY -> listOf(
            Part(roundRect(6f, 12f, 24f, 34f, 5f), rotate = -8f, pivotX = 18f, pivotY = 29f),
            Part(roundRect(26f, 10f, 24f, 34f, 5f), rotate = 8f, pivotX = 38f, pivotY = 27f),
        )
        Game.EMOTION -> listOf(
            Part(circle(28f, 28f, 22f)),
            Part(circle(20f, 23f, 1.6f), filled = true),
            Part(circle(36f, 23f, 1.6f), filled = true),
            Part("M18 33 Q28 42 38 33"),
        )
        Game.STOP_GO -> listOf(
            Part(roundRect(16f, 4f, 24f, 48f, 12f)),
            Part(circle(28f, 17f, 6f)),
            Part(circle(28f, 39f, 6f), filled = true),
        )
        Game.MOVEMENT -> listOf(
            Part(circle(28f, 10f, 6f)),
            Part("M28 17 V34 M28 22 L14 12 M28 22 L42 12 M28 34 L18 50 M28 34 L38 50"),
        )
        Game.BALLOON -> listOf(
            Part("M28 6c-9 0-15 7-15 15 0 10 9 17 15 19 6-2 15-9 15-19 0-8-6-15-15-15z"),
            Part("M28 40 l-3 4 h6 z"),
            Part("M28 44 c-4 4 4 6 0 10"),
        )
        Game.DRAWING -> listOf(
            Part("M38 6 L50 18 L24 44 L10 46 L12 32 Z"),
            Part("M32 12 L44 24"),
            Part("M6 52 Q14 48 22 52"),
        )
        Game.XYLOPHONE -> listOf(
            Part(roundRect(4f, 10f, 9f, 38f, 3f)),
            Part(roundRect(17f, 14f, 9f, 30f, 3f)),
            Part(roundRect(30f, 18f, 9f, 22f, 3f)),
            Part(roundRect(43f, 22f, 9f, 14f, 3f)),
        )
    }

    fun game(game: Game, color: Color): ImageVector? =
        parts(game)?.let { build("game_${game.id}", 56f, color, it) }

    // ── 버튼 아이콘 (34×34 격자) ─────────────────────────────
    fun home(color: Color) = build(
        "home", 34f, color,
        listOf(Part("M5 16 L17 5 L29 16", width = 3f), Part("M9 14 V29 H25 V14", width = 3f), Part("M14 29 V21 H20 V29", width = 3f)),
    )

    fun speaker(color: Color) = build(
        "speaker", 34f, color,
        listOf(
            Part("M5 13 H11 L18 7 V27 L11 21 H5 Z", width = 3f),
            Part("M23 12 C26 15 26 19 23 22", width = 3f),
            Part("M27 8 C32 13 32 21 27 26", width = 3f),
        ),
    )

    fun book(color: Color) = build(
        "book", 36f, color,
        listOf(Part("M6 6h18a4 4 0 0 1 4 4v20H10a4 4 0 0 1-4-4z", width = 2.8f), Part("M6 26a4 4 0 0 1 4-4h18", width = 2.8f)),
    )

    fun star(color: Color) = build(
        "star", 36f, color,
        listOf(Part("M18 5l3.8 7.8 8.6 1.2-6.2 6 1.5 8.5L18 24.5l-7.7 4 1.5-8.5-6.2-6 8.6-1.2z", filled = true, width = 2f)),
    )

    fun lock(color: Color) = build(
        "lock", 18f, color,
        listOf(Part(roundRect(3f, 8f, 12f, 8f, 2f), width = 2f), Part("M6 8V5.5a3 3 0 0 1 6 0V8", width = 2f)),
    )

    fun gift(color: Color) = build(
        "gift", 52f, color,
        listOf(
            Part(roundRect(6f, 20f, 40f, 26f, 4f), width = 3f),
            Part(roundRect(3f, 13f, 46f, 9f, 3f), width = 3f),
            Part("M26 13 V46", width = 3f),
            Part("M26 13 C20 3 10 6 14 13 M26 13 C32 3 42 6 38 13", width = 3f),
        ),
    )

    fun talk(color: Color) = build(
        "talk", 34f, color,
        listOf(
            Part("M4 8 a4 4 0 0 1 4 -4 H22 a4 4 0 0 1 4 4 V16 a4 4 0 0 1 -4 4 H13 L7 25 V20 a4 4 0 0 1 -3 -4 Z", width = 2.6f),
            Part("M26 12 H27 a3 3 0 0 1 3 3 V22 a3 3 0 0 1 -3 3 V29 L22 25 H17", width = 2.6f),
        ),
    )

    fun check(color: Color) = build("check", 26f, color, listOf(Part("M6 13 L11 18 L20 8", width = 4f)))

    fun trash(color: Color) = build(
        "trash", 34f, color,
        listOf(Part("M7 10 H27 M13 10 V6 H21 V10", width = 3f), Part("M9 10 L11 29 H23 L25 10", width = 3f)),
    )

    fun arrowLeft(color: Color) = build("left", 34f, color, listOf(Part("M21 7 L11 17 L21 27", width = 4f)))
    fun arrowRight(color: Color) = build("right", 34f, color, listOf(Part("M13 7 L23 17 L13 27", width = 4f)))

    fun sun(color: Color) = build(
        "sun", 30f, color,
        listOf(
            Part(circle(15f, 15f, 6f), filled = true, width = 2.5f),
            Part("M15 2v4M15 24v4M2 15h4M24 15h4M5.8 5.8l2.8 2.8M21.4 21.4l2.8 2.8M5.8 24.2l2.8-2.8M21.4 8.6l2.8-2.8", width = 2.5f),
        ),
    )

    fun moon(color: Color) = build("moon", 26f, color, listOf(Part("M17 3a10 10 0 1 0 6 16A8 8 0 0 1 17 3z", width = 2.5f)))
}

@Composable
fun VectorIcon(vector: ImageVector, modifier: Modifier = Modifier, size: Dp = 32.dp) {
    Image(
        painter = rememberVectorPainter(vector),
        contentDescription = null,
        modifier = modifier.size(size).clearAndSetSemantics { },
    )
}

/** 놀이 아이콘. 한글 놀이는 글자로 보여 줍니다. */
@Composable
fun GameIcon(game: Game, color: Color, size: Dp, modifier: Modifier = Modifier) {
    val vector = remember(game, color) { KidIcons.game(game, color) }
    if (vector != null) {
        VectorIcon(vector, modifier, size)
    } else {
        Box(modifier.size(size).clearAndSetSemantics { }, contentAlignment = Alignment.Center) {
            val label = if (game == Game.HANGUL) "가" else "ㄱ+ㅏ"
            Text(label, fontSize = (size.value * if (game == Game.HANGUL) 0.8f else 0.5f).sp, color = color, softWrap = false)
        }
    }
}
