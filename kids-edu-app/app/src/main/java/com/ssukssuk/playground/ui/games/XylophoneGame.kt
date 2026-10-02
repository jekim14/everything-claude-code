package com.ssukssuk.playground.ui.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.ssukssuk.playground.content.Lines
import com.ssukssuk.playground.content.Songs
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.ui.components.GameScaffold
import com.ssukssuk.playground.ui.components.PillButton
import com.ssukssuk.playground.ui.components.bouncyClick
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sin

private val barColors = listOf(
    Color(0xFFF44336), Color(0xFFFF9800), Color(0xFFFFEB3B), Color(0xFF66BB6A),
    Color(0xFF26C6DA), Color(0xFF42A5F5), Color(0xFF7E57C2), Color(0xFFEC407A),
)

private class FloatingNote(val bar: Int, val start: Float, val glyph: String)

/** 막대 위치 계산 (그리기와 터치 판정에 같이 씁니다) */
private fun barRects(size: Size): List<Rect> {
    val slot = size.width / 8f
    val barWidth = slot * 0.74f
    val centerY = size.height * 0.56f
    return List(8) { i ->
        val height = size.height * (0.8f - i * 0.045f)
        val left = slot * i + (slot - barWidth) / 2f
        Rect(left, centerY - height / 2f, left + barWidth, centerY + height / 2f)
    }
}

/**
 * 실로폰: 여덟 음 막대를 두드려 소리를 만들고, 노래를 고르면 반짝이는 막대를 따라 칩니다.
 * 여러 손가락으로 동시에 치거나 미끄러지듯 쓸어도(글리산도) 소리가 납니다.
 */
@Composable
fun XylophoneGame(env: GameEnv) {
    var songIndex by remember { mutableIntStateOf(-1) }
    var position by remember { mutableIntStateOf(0) }
    var time by remember { mutableFloatStateOf(0f) }
    var finished by remember { mutableStateOf(false) }
    val hitTimes = remember { FloatArray(8) { -10f } }
    val notes = remember { ArrayList<FloatingNote>() }
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer(cacheSize = 16)
    val labelStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
    val noteStyle = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = KidsColors.Ink)

    /** 만 4세는 노래 앞부분만 따라 칩니다. */
    fun notesFor(index: Int): List<Int>? =
        Songs.all.getOrNull(index)?.let { if (env.difficulty.isYounger) it.notes.take(it.shortLength) else it.notes }

    val songNotes = notesFor(songIndex)

    LaunchedEffect(Unit) {
        delay(800)
        env.say(Lines.XYLO_INTRO)
    }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (isActive) {
            withFrameNanos { now -> time = (now - start) / 1_000_000_000f }
        }
    }

    fun hit(bar: Int) {
        if (finished) return
        env.sound.playNote(bar)
        hitTimes[bar] = time
        notes.removeAll { time - it.start > 1.2f }
        notes += FloatingNote(bar, time, if (notes.size % 2 == 0) "♪" else "♫")
        // 터치 처리 안에서 불리므로 현재 고른 노래를 상태에서 다시 읽습니다.
        val current = notesFor(songIndex) ?: return
        if (position < current.size && bar == current[position]) {
            position++
            if (position == current.size) {
                finished = true
                val song = Songs.all[songIndex]
                scope.launch {
                    delay(700)
                    env.play(Sfx.CHEER)
                    env.say(Lines.xyloSongDone(song))
                    delay(1800)
                    env.complete()
                }
            }
        }
    }

    fun pickSong(index: Int) {
        if (finished) return
        songIndex = index
        position = 0
        if (index < 0) {
            env.say(Lines.XYLO_FREE)
        } else {
            env.say(Lines.xyloSong(Songs.all[index]))
        }
    }

    GameScaffold(title = env.game.title, color = Color(env.game.colorArgb), onHome = env.onHome) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SongChip("🎶 자유 연주", selected = songIndex == -1) { pickSong(-1) }
                Songs.all.forEachIndexed { i, s -> SongChip("🎵 ${s.title}", selected = songIndex == i) { pickSong(i) } }
                if (songIndex == -1) {
                    PillButton(text = "다 했어요", icon = "✅", fontSize = 16.sp, onClick = {
                        if (!finished) {
                            finished = true
                            env.say(Lines.XYLO_DONE)
                            scope.launch {
                                delay(1200)
                                env.complete()
                            }
                        }
                    })
                }
            }
            if (songNotes != null) {
                SongProgress(done = position, total = songNotes.size)
            }
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 6.dp)
                    .semantics { contentDescription = "실로폰 막대 여덟 개" }
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            val lastBar = HashMap<PointerId, Int>()
                            while (true) {
                                val event = awaitPointerEvent()
                                val rects = barRects(size.toSize())
                                event.changes.forEach { change ->
                                    if (change.pressed) {
                                        val bar = rects.indexOfFirst { it.contains(change.position) }
                                        if (bar >= 0 && lastBar[change.id] != bar) {
                                            lastBar[change.id] = bar
                                            hit(bar)
                                        } else if (bar < 0) {
                                            lastBar.remove(change.id)
                                        }
                                        change.consume()
                                    } else {
                                        lastBar.remove(change.id)
                                    }
                                }
                            }
                        }
                    },
            ) {
                val now = time
                val rects = barRects(size)
                // 받침대
                val railColor = Color(0xFF8D6E63)
                val first = rects.first()
                val last = rects.last()
                val railHeight = size.height * 0.035f
                listOf(0.22f, 0.78f).forEach { f ->
                    val topY = first.top + first.height * f
                    val bottomY = last.top + last.height * f
                    val rail = Path().apply {
                        moveTo(first.left - 10f, topY - railHeight / 2f)
                        lineTo(last.right + 10f, bottomY - railHeight / 2f)
                        lineTo(last.right + 10f, bottomY + railHeight / 2f)
                        lineTo(first.left - 10f, topY + railHeight / 2f)
                        close()
                    }
                    drawPath(rail, railColor)
                }
                val next = songNotes?.getOrNull(position)
                rects.forEachIndexed { i, rect ->
                    val since = now - hitTimes[i]
                    val bounce = if (since in 0f..0.6f) sin(since * 30f) * exp(-since * 8f) * rect.height * 0.03f else 0f
                    val flash = if (since in 0f..0.35f) 1f - since / 0.35f else 0f
                    val topLeft = Offset(rect.left, rect.top + bounce)
                    val corner = CornerRadius(rect.width * 0.25f)
                    if (next == i && !finished) {
                        val glow = 0.5f + 0.5f * abs(sin(now * PI.toFloat() * 2f))
                        drawRoundRect(
                            Color(0xFFFFF59D).copy(alpha = 0.5f + glow * 0.5f),
                            topLeft = topLeft - Offset(8f, 8f),
                            size = Size(rect.width + 16f, rect.height + 16f),
                            cornerRadius = CornerRadius(rect.width * 0.3f),
                        )
                        // 콩콩 뛰는 화살표
                        val arrowY = rect.top - 24f - glow * 14f
                        val arrow = Path().apply {
                            moveTo(rect.center.x - 18f, arrowY - 18f)
                            lineTo(rect.center.x + 18f, arrowY - 18f)
                            lineTo(rect.center.x, arrowY + 6f)
                            close()
                        }
                        drawPath(arrow, KidsColors.Accent)
                    }
                    drawRoundRect(barColors[i], topLeft = topLeft, size = rect.size, cornerRadius = corner)
                    drawRoundRect(
                        Color.Black.copy(alpha = 0.12f),
                        topLeft = topLeft,
                        size = rect.size,
                        cornerRadius = corner,
                        style = Stroke(width = 3f),
                    )
                    if (flash > 0f) {
                        drawRoundRect(Color.White.copy(alpha = flash * 0.6f), topLeft = topLeft, size = rect.size, cornerRadius = corner)
                    }
                    // 나사
                    listOf(0.22f, 0.78f).forEach { f ->
                        drawCircle(Color.White.copy(alpha = 0.8f), radius = rect.width * 0.08f, center = Offset(rect.center.x, topLeft.y + rect.height * f))
                    }
                    val label = measurer.measure(Songs.noteNames[i], labelStyle)
                    drawText(
                        textLayoutResult = label,
                        topLeft = Offset(rect.center.x - label.size.width / 2f, topLeft.y + rect.height * 0.45f - label.size.height / 2f),
                    )
                }
                // 떠오르는 음표
                notes.forEach { note ->
                    val p = (now - note.start) / 1.2f
                    if (p in 0f..1f) {
                        val rect = rects[note.bar]
                        val layout = measurer.measure(note.glyph, noteStyle)
                        drawText(
                            textLayoutResult = layout,
                            topLeft = Offset(
                                rect.center.x - layout.size.width / 2f + sin(p * 8f) * 12f,
                                rect.top - layout.size.height - p * size.height * 0.25f,
                            ),
                            alpha = 1f - p,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SongChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(if (selected) KidsColors.Leaf else Color.White, RoundedCornerShape(50))
            .border(2.dp, if (selected) KidsColors.Leaf else Color(0xFFE0E0E0), RoundedCornerShape(50))
            .bouncyClick(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(label, fontSize = 17.sp, color = if (selected) Color.White else KidsColors.Ink)
    }
}

@Composable
private fun SongProgress(done: Int, total: Int) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
            .height(14.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterHorizontally),
    ) {
        repeat(total) { i ->
            Box(
                Modifier
                    .size(if (total > 30) 7.dp else 10.dp)
                    .background(if (i < done) KidsColors.Accent else Color(0x33000000), CircleShape),
            )
        }
    }
}
