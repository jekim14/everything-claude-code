package com.ssukssuk.playground.ui.games

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.content.Lines
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.logic.CountingQuiz
import com.ssukssuk.playground.ui.components.AnswerCard
import com.ssukssuk.playground.ui.components.AnswerState
import com.ssukssuk.playground.ui.components.GameScaffold
import com.ssukssuk.playground.ui.components.PraisePop
import com.ssukssuk.playground.ui.components.ShakeState
import com.ssukssuk.playground.ui.components.bouncyClick
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 숫자 세기: 물건을 하나씩 눌러 세고(일대일 대응), 마지막에 센 수가 전체 수라는 것(기수 원리)을
 * 숫자와 연결합니다. 10까지는 다섯 칸씩 두 줄(10칸 틀)로 놓아 한눈에 수량을 파악하도록 돕습니다.
 */
@Composable
fun CountingGame(env: GameEnv) {
    val difficulty = env.difficulty
    val questions = remember {
        CountingQuiz.generate(env.random, difficulty.questionsPerRound, difficulty.countMax, difficulty.countChoices)
    }
    var index by remember { mutableIntStateOf(0) }
    var firstTry by remember { mutableIntStateOf(0) }
    var missed by remember { mutableStateOf(false) }
    var misses by remember { mutableIntStateOf(0) }
    var demoJob by remember { mutableStateOf<Job?>(null) }
    var solved by remember { mutableStateOf(false) }
    var disabled by remember { mutableStateOf(emptySet<Int>()) }
    var celebrate by remember { mutableIntStateOf(0) }
    var praise by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val question = questions[index]
    val item = question.item
    /** 눌러서 센 물건 번호 → 몇 번째로 셌는지 */
    val counted = remember(index) { mutableStateListOf<Int>() }
    val shakes = remember(index) { question.choices.associateWith { ShakeState() } }

    fun prompt() = env.say(Lines.countingPrompt(item))

    LaunchedEffect(index) {
        delay(if (index == 0) 800 else 300)
        prompt()
    }

    fun tapItem(i: Int) {
        if (solved) return
        if (i in counted) {
            env.say(Lines.count(counted.indexOf(i) + 1))
            return
        }
        counted.add(i)
        env.play(Sfx.TAP)
        if (counted.size == question.answer) {
            env.say(Lines.countingAllCounted(counted.size, item))
        } else {
            env.say(Lines.count(counted.size))
        }
    }

    fun choose(number: Int) {
        if (solved || number in disabled) return
        if (number == question.answer) {
            solved = true
            demoJob?.cancel()
            if (!missed) firstTry++
            env.play(Sfx.CORRECT)
            celebrate++
            val word = env.praise(afterMiss = missed)
            praise = word.substringBefore(' ')
            // 기수 원리: 하나씩 센 뒤 "모두 세 개!"로 마지막 수가 전체라는 것을 짚어 줍니다.
            counted.clear()
            counted.addAll(0 until question.answer)
            env.say(Lines.countingCorrect(word, number, item))
            scope.launch {
                delay(2600)
                if (index + 1 < questions.size) {
                    index++
                    solved = false
                    missed = false
                    misses = 0
                    disabled = emptySet()
                } else {
                    env.complete(firstTry, questions.size)
                }
            }
        } else {
            missed = true
            misses++
            disabled = disabled + number
            counted.clear()
            env.play(Sfx.WRONG)
            scope.launch { shakes[number]?.shake() }
            if (misses == 1) {
                env.say(Lines.COUNTING_RETRY)
            } else {
                // 두 번째부터는 같이 세어 보여 줍니다(시범).
                env.say(Lines.countingDemo(question.answer, item))
                demoJob?.cancel()
                demoJob = scope.launch {
                    delay(900)
                    for (i in 0 until question.answer) {
                        if (solved) break
                        if (i !in counted) counted.add(i)
                        delay(620)
                    }
                }
            }
        }
    }

    GameScaffold(
        title = env.game.title,
        color = Color(env.game.colorArgb),
        onHome = env.onHome,
        progress = index + if (solved) 1 else 0,
        total = questions.size,
        onReplayVoice = ::prompt,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .shadow(8.dp, RoundedCornerShape(32.dp))
                    .background(Color(0xFFFFFDF5), RoundedCornerShape(32.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center,
            ) {
                val rows = if (question.answer > 5) 2 else 1
                val cell = min(maxWidth / 5.3f, maxHeight / (rows + 0.4f))
                Column(verticalArrangement = Arrangement.spacedBy(cell * 0.08f)) {
                    (0 until question.answer).chunked(5).forEach { rowItems ->
                        Row(horizontalArrangement = Arrangement.spacedBy(cell * 0.06f)) {
                            rowItems.forEach { i ->
                                val order = counted.indexOf(i)
                                key(index, i) {
                                    CountObject(
                                        emoji = item.emoji,
                                        size = cell,
                                        order = if (order >= 0) order + 1 else null,
                                        appearDelay = i * 110L,
                                        celebrate = celebrate,
                                        wavePhase = i,
                                        onTap = { tapItem(i) },
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                question.choices.forEach { number ->
                    val state = when {
                        solved && number == question.answer -> AnswerState.Correct
                        number in disabled || solved -> AnswerState.Dimmed
                        misses >= 2 && number == question.answer -> AnswerState.Hint
                        else -> AnswerState.Normal
                    }
                    AnswerCard(
                        state = state,
                        shakeState = shakes.getValue(number),
                        onClick = { choose(number) },
                        contentDescription = "$number",
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .size(width = 150.dp, height = 84.dp),
                    ) {
                        Text("$number", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = KidsColors.Ink)
                    }
                }
            }
        }
        PraisePop(text = praise, trigger = celebrate, modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun CountObject(
    emoji: String,
    size: Dp,
    order: Int?,
    appearDelay: Long,
    celebrate: Int,
    wavePhase: Int,
    onTap: () -> Unit,
) {
    val appear = remember { Animatable(0f) }
    val bounce = remember { Animatable(1f) }
    val hop = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(appearDelay)
        appear.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMediumLow))
    }
    LaunchedEffect(order) {
        if (order != null) {
            bounce.snapTo(1.35f)
            bounce.animateTo(1f, spring(dampingRatio = 0.35f, stiffness = Spring.StiffnessMedium))
        }
    }
    LaunchedEffect(celebrate) {
        if (celebrate > 0) {
            delay(wavePhase * 90L)
            hop.animateTo(1f, tween(160))
            hop.animateTo(0f, spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMediumLow))
        }
    }
    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                val s = appear.value * bounce.value
                scaleX = s
                scaleY = s
                translationY = -hop.value * size.toPx() * 0.3f
            }
            .bouncyClick(onClick = onTap)
            .semantics { contentDescription = if (order != null) "$order" else "세지 않은 물건" },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = emoji,
            fontSize = (size.value * 0.62f).sp,
            modifier = Modifier.graphicsLayer { alpha = if (order != null) 1f else 0.92f },
        )
        if (order != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(size * 0.36f)
                    .background(KidsColors.Accent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text("$order", fontSize = (size.value * 0.2f).sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
        }
    }
}
