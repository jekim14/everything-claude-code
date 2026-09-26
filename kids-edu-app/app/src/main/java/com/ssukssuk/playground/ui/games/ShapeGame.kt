package com.ssukssuk.playground.ui.games

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.round
import androidx.compose.ui.zIndex
import com.ssukssuk.playground.content.Lines
import com.ssukssuk.playground.content.Palette
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.ui.components.BurstEffect
import com.ssukssuk.playground.ui.components.GameScaffold
import com.ssukssuk.playground.ui.components.PraisePop
import com.ssukssuk.playground.content.ShapeKind
import com.ssukssuk.playground.ui.components.rememberPulse
import com.ssukssuk.playground.ui.components.shapePath
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class ShapeRound(val slots: List<ShapeKind>, val pieces: List<ShapeKind>, val colors: Map<ShapeKind, Color>)

/** 모양 맞추기: 도형 조각을 손가락으로 끌어 같은 모양 자리에 넣습니다(소근육·도형 인식). */
@Composable
fun ShapeGame(env: GameEnv) {
    val difficulty = env.difficulty
    val rounds = remember {
        List(difficulty.shapeRounds) {
            val kinds = ShapeKind.entries.shuffled(env.random).take(difficulty.shapeCount)
            val palette = Palette.balloonColors.shuffled(env.random)
            ShapeRound(
                slots = kinds,
                pieces = kinds.shuffled(env.random),
                colors = kinds.mapIndexed { i, kind -> kind to Color(palette[i % palette.size].argb) }.toMap(),
            )
        }
    }
    var roundIndex by remember { mutableIntStateOf(0) }
    var firstTry by remember { mutableIntStateOf(0) }
    var celebrate by remember { mutableIntStateOf(0) }
    var praise by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val round = rounds[roundIndex]
    val placed = remember(roundIndex) { mutableStateListOf<ShapeKind>() }
    val missed = remember(roundIndex) { mutableStateListOf<ShapeKind>() }
    val slotCenters = remember(roundIndex) { mutableStateMapOf<ShapeKind, Offset>() }
    var slotRadius by remember { mutableStateOf(1f) }
    val roundDone = placed.size == round.slots.size

    fun prompt() = env.say(Lines.SHAPE_PROMPT)

    LaunchedEffect(roundIndex) {
        delay(if (roundIndex == 0) 800 else 300)
        prompt()
    }

    /** 조각을 놓았을 때: 맞는 자리면 true */
    fun drop(kind: ShapeKind, center: Offset): Boolean {
        val threshold = slotRadius * 1.1f
        val slot = slotCenters[kind]
        if (slot != null && (center - slot).getDistance() < threshold) {
            placed += kind
            if (kind !in missed) firstTry++
            env.play(Sfx.CORRECT)
            if (placed.size == round.slots.size) {
                celebrate++
                val word = env.praise(afterMiss = missed.isNotEmpty())
                praise = word.substringBefore(' ')
                env.say(Lines.shapeAllDone(word))
                scope.launch {
                    delay(2200)
                    if (roundIndex + 1 < rounds.size) {
                        roundIndex++
                    } else {
                        env.complete(firstTry, rounds.sumOf { it.slots.size })
                    }
                }
            } else {
                env.say(Lines.shapeFit(kind))
            }
            return true
        }
        val wrongSlot = slotCenters.entries.firstOrNull { (other, c) ->
            other != kind && other !in placed && (center - c).getDistance() < threshold
        }
        if (wrongSlot != null) {
            if (kind !in missed) missed += kind
            env.play(Sfx.WRONG)
            env.say(Lines.shapeWrong(kind))
        }
        return false
    }

    GameScaffold(
        title = env.game.title,
        color = Color(env.game.colorArgb),
        onHome = env.onHome,
        progress = roundIndex + if (roundDone) 1 else 0,
        total = rounds.size,
        onReplayVoice = ::prompt,
    ) {
        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            val count = round.slots.size
            val itemSize: Dp = min(min(maxWidth / (count + 0.8f), maxHeight / 2.5f), 128.dp)
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                // 모양 자리
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    round.slots.forEach { kind ->
                        key(roundIndex, kind) {
                            ShapeSlot(
                                kind = kind,
                                size = itemSize,
                                filled = kind in placed,
                                onPositioned = { center, radius ->
                                    slotCenters[kind] = center
                                    slotRadius = radius
                                },
                            )
                        }
                    }
                }
                // 모양 조각
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    round.pieces.forEach { kind ->
                        key(roundIndex, kind) {
                            ShapePiece(
                                kind = kind,
                                color = round.colors.getValue(kind),
                                size = itemSize,
                                placed = kind in placed,
                                slotCenter = slotCenters[kind],
                                onPick = {
                                    env.play(Sfx.TAP)
                                    env.say(Lines.shapeName(kind))
                                },
                                onDrop = { center -> drop(kind, center) },
                            )
                        }
                    }
                }
            }
            BurstEffect(trigger = celebrate, modifier = Modifier.fillMaxSize())
        }
        PraisePop(text = praise, trigger = celebrate, modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun ShapeSlot(kind: ShapeKind, size: Dp, filled: Boolean, onPositioned: (Offset, Float) -> Unit) {
    val pulse = rememberPulse(0.96f, 1.04f, 1200)
    Canvas(
        modifier = Modifier
            .size(size)
            .onGloballyPositioned { coordinates ->
                val bounds = coordinates.boundsInRoot()
                onPositioned(bounds.center, bounds.width / 2f)
            }
            .graphicsLayer {
                val s = if (filled) 1f else pulse.value
                scaleX = s
                scaleY = s
            }
            .semantics { contentDescription = "${kind.label} 자리" },
    ) {
        val path = shapePath(kind, this.size)
        drawPath(path, Color.Black.copy(alpha = if (filled) 0.02f else 0.08f))
        drawPath(
            path = path,
            color = KidsColors.Ink.copy(alpha = 0.4f),
            style = Stroke(
                width = 3.dp.toPx(),
                join = StrokeJoin.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f)),
            ),
        )
    }
}

@Composable
private fun ShapePiece(
    kind: ShapeKind,
    color: Color,
    size: Dp,
    placed: Boolean,
    slotCenter: Offset?,
    onPick: () -> Unit,
    onDrop: (Offset) -> Boolean,
) {
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    var home by remember { mutableStateOf(Offset.Zero) }
    var dragging by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val currentPlaced by rememberUpdatedState(placed)
    val currentOnDrop by rememberUpdatedState(onDrop)
    val currentOnPick by rememberUpdatedState(onPick)

    LaunchedEffect(placed, slotCenter) {
        if (placed && slotCenter != null) {
            offset.animateTo(slotCenter - home, spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow))
        }
    }

    Box(
        modifier = Modifier
            .size(size)
            .zIndex(if (dragging) 10f else 1f)
            .onGloballyPositioned { home = it.boundsInRoot().center },
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .offset { offset.value.round() }
                .graphicsLayer {
                    val s = if (dragging) 1.12f else 1f
                    scaleX = s
                    scaleY = s
                }
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { if (!currentPlaced) currentOnPick() })
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            if (!currentPlaced) {
                                dragging = true
                                currentOnPick()
                            }
                        },
                        onDragEnd = {
                            if (dragging) {
                                dragging = false
                                val accepted = currentOnDrop(home + offset.value)
                                if (!accepted) {
                                    scope.launch { offset.animateTo(Offset.Zero, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow)) }
                                }
                            }
                        },
                        onDragCancel = {
                            dragging = false
                            scope.launch { offset.animateTo(Offset.Zero) }
                        },
                        onDrag = { change, amount ->
                            if (dragging) {
                                change.consume()
                                scope.launch { offset.snapTo(offset.value + amount) }
                            }
                        },
                    )
                }
                .semantics { contentDescription = "${kind.label} 조각" },
        ) {
            val path = shapePath(kind, this.size)
            drawPath(path, color)
            drawPath(path, Color.White.copy(alpha = 0.35f), style = Stroke(width = 4.dp.toPx(), join = StrokeJoin.Round))
        }
    }
}
