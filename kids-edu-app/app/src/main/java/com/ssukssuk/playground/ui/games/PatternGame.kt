package com.ssukssuk.playground.ui.games

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.content.PatternItem
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.logic.PatternQuiz
import com.ssukssuk.playground.ui.components.AnswerCard
import com.ssukssuk.playground.ui.components.AnswerState
import com.ssukssuk.playground.ui.components.GameScaffold
import com.ssukssuk.playground.ui.components.PraisePop
import com.ssukssuk.playground.ui.components.ShakeState
import com.ssukssuk.playground.ui.components.rememberPulse
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 규칙 찾기: 구슬처럼 꿰어진 그림들의 반복 규칙(AB, AAB, ABB, ABC)을 찾아 다음 그림을 고릅니다.
 * 그림이 차례로 폴짝 뛰며 이름을 읽어 주어 리듬으로 규칙을 느끼게 합니다.
 */
@Composable
fun PatternGame(env: GameEnv) {
    val difficulty = env.difficulty
    val questions = remember { PatternQuiz.generate(env.random, difficulty.questionsPerRound, difficulty.patternKinds) }
    var index by remember { mutableIntStateOf(0) }
    var firstTry by remember { mutableIntStateOf(0) }
    var missed by remember { mutableStateOf(false) }
    var solved by remember { mutableStateOf(false) }
    var disabled by remember { mutableStateOf(emptySet<PatternItem>()) }
    var hop by remember { mutableIntStateOf(-1) }
    var celebrate by remember { mutableIntStateOf(0) }
    var praise by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    var readJob by remember { mutableStateOf<Job?>(null) }
    val question = questions[index]
    val shakes = remember(index) { question.choices.associateWith { ShakeState() } }

    /** 그림 이름을 읽으며 하나씩 폴짝 뛰게 합니다. */
    fun readAloud(includeAnswer: Boolean, lead: String = "") {
        readJob?.cancel()
        val items = if (includeAnswer) question.shown + question.answer else question.shown
        val names = items.joinToString(", ") { it.name }
        env.say(if (includeAnswer) "$lead $names!" else "$lead $names, 다음은 뭘까요?")
        readJob = scope.launch {
            delay(if (lead.isEmpty()) 250 else 900)
            items.indices.forEach { i ->
                hop = i
                delay(560)
            }
            hop = if (includeAnswer) -1 else items.size
            delay(700)
            hop = -1
        }
    }

    LaunchedEffect(index) {
        delay(if (index == 0) 900 else 350)
        readAloud(includeAnswer = false)
    }

    fun choose(choice: PatternItem) {
        if (solved || choice in disabled) return
        if (choice == question.answer) {
            solved = true
            if (!missed) firstTry++
            env.play(Sfx.CORRECT)
            celebrate++
            val word = env.praise()
            praise = word
            readAloud(includeAnswer = true, lead = word)
            scope.launch {
                delay(1600L + (question.shown.size + 1) * 560L)
                if (index + 1 < questions.size) {
                    index++
                    solved = false
                    missed = false
                    disabled = emptySet()
                } else {
                    env.complete(firstTry, questions.size)
                }
            }
        } else {
            missed = true
            disabled = disabled + choice
            env.play(Sfx.WRONG)
            scope.launch { shakes[choice]?.shake() }
            readAloud(includeAnswer = false, lead = "음~ 다시 같이 읽어 볼까요?")
        }
    }

    GameScaffold(
        title = env.game.title,
        color = Color(env.game.colorArgb),
        onHome = env.onHome,
        progress = index + if (solved) 1 else 0,
        total = questions.size,
        onReplayVoice = { readAloud(includeAnswer = false) },
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                val cells = question.shown.size + 1
                val cell = min(maxWidth / (cells + 0.6f), 100.dp)
                // 구슬을 꿴 줄
                Canvas(
                    Modifier
                        .fillMaxWidth()
                        .height(cell),
                ) {
                    val y = size.height / 2f
                    val span = cell.toPx() * cells + 8.dp.toPx() * (cells - 1)
                    val start = (size.width - span) / 2f
                    drawLine(
                        color = Color(0xFF8D6E63),
                        start = Offset(start, y),
                        end = Offset(start + span, y),
                        strokeWidth = 5.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    question.shown.forEachIndexed { i, item ->
                        key(index, i) {
                            Bead(item.emoji, cell, hopping = hop == i)
                        }
                    }
                    key(index, "slot") {
                        MysterySlot(
                            size = cell,
                            answer = if (solved) question.answer.emoji else null,
                            hopping = hop == question.shown.size,
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                question.choices.forEach { choice ->
                    val state = when {
                        solved && choice == question.answer -> AnswerState.Correct
                        choice in disabled || solved -> AnswerState.Dimmed
                        else -> AnswerState.Normal
                    }
                    AnswerCard(
                        state = state,
                        shakeState = shakes.getValue(choice),
                        onClick = { choose(choice) },
                        contentDescription = choice.name,
                        modifier = Modifier.size(118.dp),
                    ) {
                        Text(choice.emoji, fontSize = 58.sp)
                    }
                }
            }
        }
        PraisePop(text = praise, trigger = celebrate, modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun Bead(emoji: String, size: Dp, hopping: Boolean) {
    val lift by animateFloatAsState(
        targetValue = if (hopping) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium),
        label = "beadHop",
    )
    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                translationY = -lift * size.toPx() * 0.3f
                val s = 1f + lift * 0.12f
                scaleX = s
                scaleY = s
            }
            .shadow(4.dp, CircleShape)
            .background(Color.White, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(emoji, fontSize = (size.value * 0.55f).sp)
    }
}

@Composable
private fun MysterySlot(size: Dp, answer: String?, hopping: Boolean) {
    val pulse = rememberPulse(0.92f, 1.08f, 800)
    val appear = remember { Animatable(0f) }
    LaunchedEffect(answer) {
        if (answer != null) {
            appear.snapTo(0.3f)
            appear.animateTo(1f, spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMediumLow))
        }
    }
    val lift by animateFloatAsState(if (hopping) 1f else 0f, label = "slotHop")
    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                val s = if (answer == null) pulse.value else appear.value
                scaleX = s
                scaleY = s
                translationY = -lift * size.toPx() * 0.3f
            },
        contentAlignment = Alignment.Center,
    ) {
        if (answer == null) {
            Canvas(Modifier.fillMaxSize()) {
                drawCircle(Color(0xFFFFF8E1))
                drawCircle(
                    color = KidsColors.Warm,
                    style = Stroke(width = 4.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f))),
                )
            }
            Text("?", fontSize = (size.value * 0.5f).sp, fontWeight = FontWeight.ExtraBold, color = KidsColors.Warm)
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .shadow(4.dp, CircleShape)
                    .background(Color(0xFFE8F5E9), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(answer, fontSize = (size.value * 0.55f).sp)
            }
        }
    }
}
