package com.ssukssuk.playground.ui.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.content.Lines
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.ui.components.GameScaffold
import com.ssukssuk.playground.ui.components.PaintStroke
import com.ssukssuk.playground.ui.components.PillButton
import com.ssukssuk.playground.ui.components.bouncyClick
import com.ssukssuk.playground.ui.components.drawPaintStroke
import com.ssukssuk.playground.ui.components.paintInput
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class Crayon(val name: String, val color: Color)

private val crayons = listOf(
    Crayon("빨간색", Color(0xFFF44336)),
    Crayon("주황색", Color(0xFFFF9800)),
    Crayon("노란색", Color(0xFFFFEB3B)),
    Crayon("초록색", Color(0xFF4CAF50)),
    Crayon("하늘색", Color(0xFF4FC3F7)),
    Crayon("파란색", Color(0xFF1E63D6)),
    Crayon("보라색", Color(0xFF8E24AA)),
    Crayon("분홍색", Color(0xFFFF6FAE)),
    Crayon("갈색", Color(0xFF8D6E63)),
    Crayon("검은색", Color(0xFF37323E)),
)

private val stamps = listOf("⭐", "🌸", "💖", "🐾", "🍀", "🎈")

private enum class Tool { CRAYON, RAINBOW, STAMP, ERASER }

private sealed interface DrawItem {
    class Line(val stroke: PaintStroke) : DrawItem
    class Stamp(val position: Offset, val emoji: String) : DrawItem
}

/** 그림 그리기: 크레용·무지개 붓·도장·지우개로 자유롭게 표현합니다. */
@Composable
fun DrawingGame(env: GameEnv) {
    val items = remember { mutableStateListOf<DrawItem>() }
    var tool by remember { mutableStateOf(Tool.CRAYON) }
    var crayon by remember { mutableStateOf(crayons[0]) }
    var brush by remember { mutableStateOf(16.dp) }
    var stamp by remember { mutableStateOf(stamps[0]) }
    var finishing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val measurer = rememberTextMeasurer(cacheSize = 16)

    LaunchedEffect(Unit) {
        delay(800)
        env.say(Lines.DRAW_INTRO)
    }

    fun startStroke(position: Offset): PaintStroke? {
        if (finishing) return null
        return when (tool) {
            Tool.STAMP -> {
                items += DrawItem.Stamp(position, stamp)
                env.play(Sfx.POP)
                null
            }
            Tool.ERASER -> PaintStroke(Color.White, with(density) { (brush * 2.2f).toPx() }).also { items += DrawItem.Line(it) }
            Tool.RAINBOW -> PaintStroke(Color.Red, with(density) { brush.toPx() }, rainbow = true).also { items += DrawItem.Line(it) }
            Tool.CRAYON -> PaintStroke(crayon.color, with(density) { brush.toPx() }).also { items += DrawItem.Line(it) }
        }
    }

    GameScaffold(title = env.game.title, color = Color(env.game.colorArgb), onHome = env.onHome) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // 색 팔레트
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                crayons.chunked(2).forEach { pair ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        pair.forEach { c ->
                            ColorDot(c.color, selected = tool == Tool.CRAYON && crayon == c, label = c.name) {
                                crayon = c
                                tool = Tool.CRAYON
                                env.say(c.name)
                            }
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ToolDot("🌈", selected = tool == Tool.RAINBOW, label = "무지개 붓") {
                        tool = Tool.RAINBOW
                        env.say(Lines.DRAW_RAINBOW)
                    }
                    ToolDot("⭐", selected = tool == Tool.STAMP, label = "도장") {
                        tool = Tool.STAMP
                        env.say(Lines.DRAW_STAMP)
                    }
                }
            }
            Column(Modifier.weight(1f)) {
                // 도구 막대
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (tool == Tool.STAMP) {
                        stamps.forEach { s ->
                            ToolDot(s, selected = stamp == s, label = "도장", size = 44.dp) { stamp = s }
                        }
                    } else {
                        listOf(8.dp, 16.dp, 28.dp).forEach { width ->
                            BrushDot(width, selected = brush == width) { brush = width }
                        }
                        ToolDot("⬜", selected = tool == Tool.ERASER, label = "지우개", size = 44.dp) {
                            tool = Tool.ERASER
                            env.say(Lines.DRAW_ERASER)
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    ToolDot("↩️", selected = false, label = "되돌리기", size = 44.dp) {
                        if (items.isNotEmpty()) items.removeAt(items.lastIndex)
                    }
                    ToolDot("🗑️", selected = false, label = "모두 지우기", size = 44.dp) {
                        items.clear()
                        env.play(Sfx.FLIP)
                    }
                    PillButton(
                        text = "다 그렸어요",
                        icon = "✅",
                        fontSize = 16.sp,
                        onClick = {
                            if (finishing) return@PillButton
                            if (items.isEmpty()) {
                                env.say(Lines.DRAW_EMPTY)
                            } else {
                                finishing = true
                                env.say(Lines.DRAW_DONE)
                                scope.launch {
                                    delay(2500)
                                    env.complete()
                                }
                            }
                        },
                    )
                }
                // 도화지
                Box(
                    Modifier
                        .fillMaxSize()
                        .shadow(8.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White),
                ) {
                    Canvas(
                        Modifier
                            .fillMaxSize()
                            .paintInput(onStart = ::startStroke)
                            .semantics { contentDescription = "도화지" },
                    ) {
                        val stampStyle = TextStyle(fontSize = 40.sp)
                        items.forEach { item ->
                            when (item) {
                                is DrawItem.Line -> drawPaintStroke(item.stroke)
                                is DrawItem.Stamp -> {
                                    val layout = measurer.measure(item.emoji, stampStyle)
                                    drawText(
                                        textLayoutResult = layout,
                                        topLeft = item.position - Offset(layout.size.width / 2f, layout.size.height / 2f),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorDot(color: Color, selected: Boolean, label: String, onClick: () -> Unit) {
    Box(
        Modifier
            .size(if (selected) 50.dp else 44.dp)
            .shadow(if (selected) 6.dp else 2.dp, CircleShape)
            .background(color, CircleShape)
            .border(if (selected) 4.dp else 2.dp, Color.White, CircleShape)
            .bouncyClick(onClick = onClick)
            .semantics { contentDescription = label },
    )
}

@Composable
private fun ToolDot(icon: String, selected: Boolean, label: String, size: Dp = 50.dp, onClick: () -> Unit) {
    Box(
        Modifier
            .size(size)
            .shadow(if (selected) 6.dp else 2.dp, CircleShape)
            .background(
                if (selected) Brush.linearGradient(listOf(Color(0xFFFFF59D), Color(0xFFFFCC80))) else Brush.linearGradient(listOf(Color.White, Color.White)),
                CircleShape,
            )
            .border(if (selected) 3.dp else 0.dp, if (selected) KidsColors.Warm else Color.Transparent, CircleShape)
            .bouncyClick(onClick = onClick)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        Text(icon, fontSize = (size.value * 0.45f).sp)
    }
}

@Composable
private fun BrushDot(width: Dp, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .size(44.dp)
            .shadow(if (selected) 6.dp else 2.dp, CircleShape)
            .background(if (selected) Color(0xFFFFF59D) else Color.White, CircleShape)
            .bouncyClick(onClick = onClick)
            .semantics { contentDescription = "붓 굵기" },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(width * 0.9f)
                .background(KidsColors.Ink, CircleShape),
        )
    }
}
