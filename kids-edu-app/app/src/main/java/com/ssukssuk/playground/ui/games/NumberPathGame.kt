package com.ssukssuk.playground.ui.games

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.content.Lines
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.logic.HopResult
import com.ssukssuk.playground.logic.NumberPath
import com.ssukssuk.playground.ui.components.AnswerCard
import com.ssukssuk.playground.ui.components.AnswerState
import com.ssukssuk.playground.ui.components.ChunkyBox
import com.ssukssuk.playground.ui.components.GameScaffold
import com.ssukssuk.playground.ui.components.Mascot
import com.ssukssuk.playground.ui.components.MascotAction
import com.ssukssuk.playground.ui.components.MascotMood
import com.ssukssuk.playground.ui.components.PraisePop
import com.ssukssuk.playground.ui.components.ShakeState
import com.ssukssuk.playground.ui.components.rememberPulse
import com.ssukssuk.playground.ui.components.shake
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Grass = Color(0xFFE6F5D8)
private val River = Color(0xFFBFE3F7)
private val Stone = Color(0xFFD8D2C4)
private val StoneShadow = Color(0xFFA89F8C)

/**
 * 숫자 징검다리: 1부터 10까지 같은 간격으로 한 줄에 놓인 돌을 주사위만큼 한 칸씩 건너며,
 * 내려앉은 돌의 수를 말합니다("넷, 다섯!").
 *
 * 이런 일직선 수판 놀이를 몇 번만 해도 유아의 수 크기 비교·수직선 어림·세기가 좋아졌습니다
 * (Siegler & Ramani 2008, 2009; Ramani & Siegler 2008). 2단계부터는 뛰고 나서 "몇에 도착했을까?"를 묻습니다.
 */
@Composable
fun NumberPathGame(env: GameEnv) {
    val d = env.difficulty
    val path = remember { NumberPath(10) }
    var position by remember { mutableIntStateOf(0) }
    var nextStone by remember { mutableStateOf<Int?>(null) }
    var die by remember { mutableIntStateOf(0) }
    var rolling by remember { mutableStateOf(false) }
    var asking by remember { mutableStateOf<List<Int>?>(null) }
    var disabledChoices by remember { mutableStateOf(emptySet<Int>()) }
    var rolls by remember { mutableIntStateOf(0) }
    var cleanRolls by remember { mutableIntStateOf(0) }
    var rollHadMistake by remember { mutableStateOf(false) }
    var celebrate by remember { mutableIntStateOf(0) }
    var praise by remember { mutableStateOf<String?>(null) }
    var finished by remember { mutableStateOf(false) }
    val hopX = remember { Animatable(0f) }
    val hopY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val stoneShakes = remember { List(11) { ShakeState() } }
    val arrivalShakes = remember { List(11) { ShakeState() } }

    LaunchedEffect(Unit) {
        delay(800)
        env.say(Lines.PATH_START)
    }

    fun finishRoll() {
        rolls++
        if (!rollHadMistake) cleanRolls++
        rollHadMistake = false
        asking = null
        disabledChoices = emptySet()
        if (path.isFinished) {
            finished = true
            env.play(Sfx.CHEER)
            env.say(Lines.PATH_FINISH)
            scope.launch {
                delay(2600)
                env.complete(cleanRolls, rolls)
            }
        } else {
            scope.launch {
                delay(1600)
                env.say(Lines.PATH_ROLL)
            }
        }
    }

    fun roll() {
        if (!path.canRoll || rolling || asking != null || finished) return
        rolling = true
        env.play(Sfx.FLIP)
        scope.launch {
            repeat(6) {
                die = NumberPath.rollDie(env.random, d.pathDieMax)
                delay(90)
            }
            val value = NumberPath.rollDie(env.random, d.pathDieMax)
            val steps = path.roll(value)
            die = steps
            rolling = false
            nextStone = path.nextStone
            env.say(Lines.pathRolled(steps))
        }
    }

    fun tapStone(stone: Int) {
        if (rolling || finished) return
        when (val result = path.tap(stone)) {
            HopResult.NotNow -> if (asking == null) env.say(Lines.PATH_ROLL)
            HopResult.Wrong -> {
                rollHadMistake = true
                env.play(Sfx.WRONG)
                env.say(Lines.PATH_WRONG_STONE)
                scope.launch { stoneShakes[stone].shake() }
            }
            is HopResult.Hopped, is HopResult.Landed -> {
                val landed = if (result is HopResult.Hopped) result.stone else (result as HopResult.Landed).stone
                env.play(Sfx.POP)
                env.say(Lines.pathStone(landed))
                position = landed
                nextStone = path.nextStone
                scope.launch {
                    launch { hopX.animateTo(landed.toFloat(), tween(380, easing = FastOutSlowInEasing)) }
                    hopY.animateTo(1f, tween(190))
                    hopY.animateTo(0f, tween(190))
                }
                if (result is HopResult.Landed) {
                    if (d.pathAskArrival && !path.isFinished) {
                        scope.launch {
                            delay(900)
                            asking = path.arrivalChoices(env.random)
                            env.say(Lines.PATH_ASK)
                        }
                    } else {
                        finishRoll()
                    }
                }
            }
        }
    }

    fun answer(n: Int) {
        if (n in disabledChoices) return
        if (n == path.position) {
            env.play(Sfx.CORRECT)
            celebrate++
            val word = env.praise(afterMiss = rollHadMistake)
            praise = word.substringBefore(' ')
            env.say(Lines.pathArrived(word, n))
            finishRoll()
        } else {
            rollHadMistake = true
            disabledChoices = disabledChoices + n
            env.play(Sfx.WRONG)
            env.say(Lines.pathArrivalRetry(path.rollStart, path.lastRoll))
            scope.launch { arrivalShakes[n].shake() }
        }
    }

    GameScaffold(
        title = env.game.title,
        color = Color(env.game.colorArgb),
        onHome = env.onHome,
        onReplayVoice = { env.say(if (asking != null) Lines.PATH_ASK else if (path.canRoll) Lines.PATH_ROLL else Lines.pathRolled(path.stepsLeft.coerceAtLeast(1))) },
        background = Grass,
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val gap = maxWidth / 11f
            val stoneW = min(gap * 0.86f, 90.dp)
            val stoneH = stoneW * 0.66f
            val riverTop = maxHeight * 0.30f
            val riverHeight = maxHeight * 0.34f
            val stoneY = riverTop + riverHeight * 0.5f - stoneH * 0.5f

            Canvas(Modifier.fillMaxSize()) {
                val top = riverTop.toPx()
                val bottom = top + riverHeight.toPx()
                val w = size.width
                val river = Path().apply {
                    moveTo(0f, top + 10f)
                    cubicTo(w * 0.3f, top - 8f, w * 0.6f, top + 18f, w, top)
                    lineTo(w, bottom)
                    cubicTo(w * 0.7f, bottom + 14f, w * 0.35f, bottom - 10f, 0f, bottom + 6f)
                    close()
                }
                drawPath(river, River)
                repeat(3) { i ->
                    val y = top + riverHeight.toPx() * (0.25f + i * 0.28f)
                    val x = w * (0.12f + i * 0.33f)
                    drawLine(Color.White.copy(alpha = 0.7f), Offset(x, y), Offset(x + 60f, y), strokeWidth = 5f)
                }
            }

            // 출발점 + 돌 1~10
            for (n in 0..10) {
                val x = gap * (n + 0.5f) - stoneW / 2
                val isNext = n == nextStone
                val done = n in 1..position
                Box(
                    Modifier
                        .offset(x = x, y = stoneY)
                        .size(stoneW, stoneH),
                ) {
                    if (n == 0) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color(0xFFB5DD95), RoundedCornerShape(50)),
                            contentAlignment = Alignment.Center,
                        ) { Text("출발", fontSize = (stoneH.value * 0.3f).sp, color = Color(0xFF3E6B38)) }
                    } else {
                        StoneView(
                            number = n,
                            width = stoneW,
                            height = stoneH,
                            isNext = isNext,
                            done = done,
                            goal = n == 10,
                            shakeState = stoneShakes[n],
                            onClick = { tapStone(n) },
                        )
                    }
                }
            }

            // 쑥쑥이
            val mascotW = stoneW * 0.9f
            Mascot(
                modifier = Modifier
                    .offset(x = gap * 0.5f - mascotW / 2, y = stoneY - mascotW * 1.02f)
                    .graphicsLayer {
                        translationX = hopX.value * gap.toPx()
                        translationY = -hopY.value * stoneH.toPx() * 0.9f
                    }
                    .size(mascotW, mascotW * 1.1f),
                mood = if (finished) MascotMood.EXCITED else MascotMood.HAPPY,
                action = if (finished) MascotAction.CHEER else MascotAction.IDLE,
            )

            // 주사위
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp, bottom = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DieCard(value = die, canRoll = path.canRoll && !rolling && asking == null && !finished, size = min(maxHeight * 0.28f, 120.dp), onClick = ::roll)
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        text = when {
                            finished -> "다 건넜어!"
                            path.canRoll && asking == null -> "주사위를 톡!"
                            asking != null -> "몇에 도착했을까?"
                            else -> "${path.lastRoll} 칸 가요"
                        },
                        fontSize = 26.sp,
                        color = KidsColors.Ink,
                    )
                    if (!path.canRoll && asking == null && !finished) {
                        Text("${path.rollStart} → ${path.rollStart + path.lastRoll}", fontSize = 18.sp, color = KidsColors.InkSoft)
                    }
                }
            }

            // 도착 질문
            asking?.let { choices ->
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 24.dp, bottom = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    choices.forEach { n ->
                        AnswerCard(
                            state = if (n in disabledChoices) AnswerState.Dimmed else AnswerState.Normal,
                            shakeState = arrivalShakes[n],
                            onClick = { answer(n) },
                            contentDescription = "$n",
                            modifier = Modifier.size(min(maxHeight * 0.26f, 96.dp)),
                        ) {
                            Text("$n", fontSize = 38.sp, color = KidsColors.Ink)
                        }
                    }
                }
            }
        }
        PraisePop(text = praise, trigger = celebrate, modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun StoneView(
    number: Int,
    width: Dp,
    height: Dp,
    isNext: Boolean,
    done: Boolean,
    goal: Boolean,
    shakeState: ShakeState,
    onClick: () -> Unit,
) {
    val pulse = rememberPulse(0.94f, 1.08f, 650)
    val (fill, shadow) = when {
        done -> Color(0xFF9FD9D1) to Color(0xFF5DB3A8)
        goal -> Color(0xFFFFE08A) to Color(0xFFD9A92E)
        isNext -> Color.White to StoneShadow
        else -> Stone to StoneShadow
    }
    Box(
        Modifier
            .fillMaxSize()
            .shake(shakeState)
            .graphicsLayer {
                val s = if (isNext) pulse.value else 1f
                scaleX = s
                scaleY = s
            },
    ) {
        if (isNext) {
            Box(
                Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        scaleX = 1.22f
                        scaleY = 1.3f
                    }
                    .background(KidsColors.Accent.copy(alpha = 0.25f), CircleShape),
            )
        }
        ChunkyBox(
            modifier = Modifier
                .size(width, height)
                .semantics { contentDescription = "$number 번 돌" },
            color = fill,
            shadow = shadow,
            radius = height / 2,
            depth = 5.dp,
            onClick = onClick,
        ) {
            Text("$number", fontSize = (height.value * 0.5f).sp, color = if (isNext) KidsColors.Accent else KidsColors.Ink)
        }
    }
}

@Composable
private fun DieCard(value: Int, canRoll: Boolean, size: Dp, onClick: () -> Unit) {
    val pulse = rememberPulse(0.96f, 1.05f, 800)
    ChunkyBox(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                val s = if (canRoll) pulse.value else 1f
                scaleX = s
                scaleY = s
            }
            .semantics { contentDescription = if (value == 0) "주사위" else "주사위 $value" },
        shadow = Color(0xFFC9DDB8),
        radius = size * 0.24f,
        onClick = onClick,
    ) {
        Canvas(Modifier.size(size * 0.62f)) {
            val r = this.size.minDimension
            drawRoundRect(
                KidsColors.Paper,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.22f, r * 0.22f),
            )
            drawRoundRect(
                KidsColors.Ink,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.22f, r * 0.22f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = r * 0.06f),
            )
            val dot = r * 0.1f
            val spots = when (value) {
                1 -> listOf(0.5f to 0.5f)
                2 -> listOf(0.28f to 0.28f, 0.72f to 0.72f)
                3 -> listOf(0.25f to 0.25f, 0.5f to 0.5f, 0.75f to 0.75f)
                else -> emptyList()
            }
            spots.forEach { (fx, fy) -> drawCircle(KidsColors.Ink, dot, Offset(r * fx, r * fy)) }
            if (value == 0) drawCircle(KidsColors.InkSoft.copy(alpha = 0.3f), dot, Offset(r * 0.5f, r * 0.5f))
        }
    }
}
