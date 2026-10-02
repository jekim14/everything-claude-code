package com.ssukssuk.playground.ui.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.content.Emotion
import com.ssukssuk.playground.content.Lines
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.logic.EmotionQuiz
import com.ssukssuk.playground.ui.components.AnswerCard
import com.ssukssuk.playground.ui.components.AnswerState
import com.ssukssuk.playground.ui.components.GameScaffold
import com.ssukssuk.playground.ui.components.PillButton
import com.ssukssuk.playground.ui.components.ShakeState
import com.ssukssuk.playground.ui.components.bouncyClick
import com.ssukssuk.playground.ui.components.rememberBob
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

/**
 * 기분 친구: 상황 이야기를 듣고 친구의 기분을 표정으로 고릅니다.
 * 감정에는 정답만 있는 것이 아니므로 다른 선택도 존중하는 말로 되묻고,
 * 정답 뒤에는 감정을 표현하고 조절하는 방법을 들려줍니다.
 */
@Composable
fun EmotionGame(env: GameEnv) {
    val difficulty = env.difficulty
    val questions = remember {
        EmotionQuiz.generate(env.random, difficulty.questionsPerRound, difficulty.emotionChoices, difficulty.stage)
    }
    var index by remember { mutableIntStateOf(0) }
    var firstTry by remember { mutableIntStateOf(0) }
    var missed by remember { mutableStateOf(false) }
    var solved by remember { mutableStateOf(false) }
    var disabled by remember { mutableStateOf(emptySet<Emotion>()) }
    /** "그럴 수도 있어"로 받아 준 다른 감정 */
    var accepted by remember { mutableStateOf<Emotion?>(null) }
    var breathing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val question = questions[index]
    val situation = question.situation
    val shakes = remember(index) { question.choices.associateWith { ShakeState() } }

    fun prompt() = env.say(Lines.emotionPrompt(situation))

    LaunchedEffect(index) {
        delay(if (index == 0) 800 else 300)
        prompt()
    }

    fun choose(emotion: Emotion) {
        if (solved || emotion in disabled) return
        when (emotion) {
            situation.emotion -> {
                solved = true
                if (!missed) firstTry++
                env.play(Sfx.CORRECT)
                env.say(Lines.emotionCorrect(env.praise(afterMiss = missed), emotion, situation))
            }
            in situation.alsoOk -> {
                // 감정에는 정답이 하나만 있지 않습니다. 그럴 법한 마음은 맞힌 것으로 인정합니다.
                solved = true
                accepted = emotion
                if (!missed) firstTry++
                env.play(Sfx.CORRECT)
                env.say(Lines.emotionAlsoOk(emotion, situation))
            }
            else -> {
                missed = true
                disabled = disabled + emotion
                env.play(Sfx.WRONG)
                env.say(Lines.emotionWrong(emotion))
                scope.launch { shakes[emotion]?.shake() }
            }
        }
    }

    fun next() {
        if (index + 1 < questions.size) {
            index++
            solved = false
            missed = false
            accepted = null
            disabled = emptySet()
        } else {
            env.complete(firstTry, questions.size)
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
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 상황 카드
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .shadow(8.dp, RoundedCornerShape(28.dp))
                    .background(Color.White, RoundedCornerShape(28.dp))
                    .bouncyClick { prompt() }
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                val bob = rememberBob(periodMillis = 1800)
                Text(
                    text = situation.emoji,
                    fontSize = if (solved) 48.sp else 76.sp,
                    modifier = Modifier.graphicsLayer {
                        translationY = bob.value * 6.dp.toPx()
                        rotationZ = bob.value * 4f
                    },
                )
                Text(
                    text = situation.text,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = KidsColors.Ink,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp,
                )
                AnimatedVisibility(visible = solved, enter = fadeIn() + scaleIn(initialScale = 0.8f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = situation.tip,
                            fontSize = 15.sp,
                            color = KidsColors.InkSoft,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp,
                            modifier = Modifier
                                .background(Color(0xFFFFF8E1), RoundedCornerShape(16.dp))
                                .padding(10.dp),
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (situation.emotion == Emotion.ANGRY || situation.emotion == Emotion.SCARED) {
                                PillButton(
                                    text = "거북이 숨쉬기",
                                    icon = "🐢",
                                    fontSize = 17.sp,
                                    color = Color(0xFFD6F3EF),
                                    shadow = Color(0xFF9FD9D1),
                                    contentColor = KidsColors.Ink,
                                    onClick = { breathing = true },
                                )
                            }
                            PillButton(
                                text = if (index + 1 < questions.size) "다음" else "끝!",
                                fontSize = 18.sp,
                                onClick = ::next,
                            )
                        }
                    }
                }
            }
            // 표정 고르기
            Column(
                modifier = Modifier.weight(1.3f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                question.choices.chunked(2).forEach { rowChoices ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowChoices.forEach { emotion ->
                            val state = when {
                                solved && emotion == (accepted ?: situation.emotion) -> AnswerState.Correct
                                solved && accepted != null && emotion == situation.emotion -> AnswerState.Hint
                                emotion in disabled || solved -> AnswerState.Dimmed
                                else -> AnswerState.Normal
                            }
                            AnswerCard(
                                state = state,
                                shakeState = shakes.getValue(emotion),
                                onClick = { choose(emotion) },
                                contentDescription = emotion.label,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(if (question.choices.size > 2) 124.dp else 170.dp),
                            ) {
                                EmotionFace(emotion, Modifier.size(if (question.choices.size > 2) 70.dp else 110.dp))
                                Text(emotion.label, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = KidsColors.Ink)
                            }
                        }
                        if (rowChoices.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
        if (breathing) TurtleBreathing(env) { breathing = false }
    }
}

/**
 * 거북이 숨쉬기: 원이 커질 때 들이마시고, 작아질 때 내쉽니다(4초씩 세 번).
 * 화나거나 무서운 마음을 가라앉히는 방법을 몸으로 연습합니다.
 */
@Composable
private fun TurtleBreathing(env: GameEnv, onDone: () -> Unit) {
    val breath = remember { Animatable(0.45f) }
    var inhale by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        env.say(Lines.BREATHE_INVITE)
        delay(2200)
        repeat(3) {
            inhale = true
            env.say(Lines.BREATHE_IN)
            breath.animateTo(1f, tween(4000, easing = FastOutSlowInEasing))
            inhale = false
            env.say(Lines.BREATHE_OUT)
            breath.animateTo(0.45f, tween(4000, easing = FastOutSlowInEasing))
        }
        env.say(Lines.BREATHE_DONE)
        delay(1800)
        onDone()
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xE6FFF8EC))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(260.dp)
                .graphicsLayer {
                    scaleX = breath.value
                    scaleY = breath.value
                }
                .background(Color(0xFF9FD9D1), CircleShape),
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🐢", fontSize = 64.sp)
            Text(if (inhale) "들이마시고…" else "후~ 내쉬어요", fontSize = 28.sp, color = KidsColors.Ink)
        }
    }
}

private val FaceYellow = Color(0xFFFFD966)
private val FaceEdge = Color(0xFFF2B84B)
private val FaceInk = Color(0xFF4E342E)

/** 움직이는 표정 얼굴: 기쁨은 통통, 슬픔은 눈물, 화남은 붉어지며 부르르, 놀람은 콩닥, 무서움은 덜덜 */
@Composable
fun EmotionFace(emotion: Emotion, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "face")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing)),
        label = "faceTime",
    )
    Canvas(modifier) {
        drawEmotionFace(emotion, t)
    }
}

private fun DrawScope.drawEmotionFace(emotion: Emotion, t: Float) {
    val wave = sin(t * 2f * PI.toFloat())
    val r = size.minDimension * 0.44f
    var cx = size.width / 2f
    var cy = size.height / 2f
    var scale = 1f
    when (emotion) {
        Emotion.HAPPY -> cy -= abs(wave) * r * 0.08f
        Emotion.ANGRY -> cx += sin(t * 2f * PI.toFloat() * 8f) * r * 0.03f
        Emotion.SCARED -> cx += sin(t * 2f * PI.toFloat() * 12f) * r * 0.02f
        Emotion.SURPRISED -> scale = 1f + abs(wave) * 0.05f
        Emotion.SAD -> cy += abs(wave) * r * 0.02f
    }
    val radius = r * scale
    val faceColor = when (emotion) {
        Emotion.ANGRY -> lerp(FaceYellow, Color(0xFFFF8A65), 0.55f + 0.25f * abs(wave))
        Emotion.SCARED -> Color(0xFFD9E3FF)
        else -> FaceYellow
    }
    drawCircle(faceColor, radius = radius, center = Offset(cx, cy))
    drawCircle(FaceEdge, radius = radius, center = Offset(cx, cy), style = Stroke(width = radius * 0.06f))

    val eyeY = cy - radius * 0.12f
    val eyeDx = radius * 0.36f
    val line = radius * 0.09f
    val stroke = Stroke(width = line, cap = StrokeCap.Round)

    // 볼
    if (emotion == Emotion.HAPPY) {
        listOf(-1f, 1f).forEach { side ->
            drawOval(
                Color(0xFFFF8A80).copy(alpha = 0.55f),
                topLeft = Offset(cx + side * radius * 0.55f - radius * 0.14f, cy + radius * 0.12f),
                size = Size(radius * 0.28f, radius * 0.16f),
            )
        }
    }

    // 눈과 눈썹
    listOf(-1f, 1f).forEach { side ->
        val ex = cx + side * eyeDx
        when (emotion) {
            Emotion.HAPPY -> drawArc(
                FaceInk, 180f, 180f, false,
                topLeft = Offset(ex - radius * 0.14f, eyeY - radius * 0.08f),
                size = Size(radius * 0.28f, radius * 0.22f),
                style = stroke,
            )
            Emotion.SAD -> {
                drawCircle(FaceInk, radius = radius * 0.08f, center = Offset(ex, eyeY + radius * 0.04f))
                // 안쪽이 올라간 눈썹
                drawLine(
                    FaceInk,
                    start = Offset(ex - side * radius * 0.16f, eyeY - radius * 0.26f),
                    end = Offset(ex + side * radius * 0.14f, eyeY - radius * 0.16f),
                    strokeWidth = line * 0.8f,
                    cap = StrokeCap.Round,
                )
            }
            Emotion.ANGRY -> {
                drawCircle(FaceInk, radius = radius * 0.08f, center = Offset(ex, eyeY + radius * 0.04f))
                // 안쪽이 내려간 눈썹
                drawLine(
                    FaceInk,
                    start = Offset(ex - side * radius * 0.17f, eyeY - radius * 0.1f),
                    end = Offset(ex + side * radius * 0.15f, eyeY - radius * 0.26f),
                    strokeWidth = line,
                    cap = StrokeCap.Round,
                )
            }
            Emotion.SURPRISED, Emotion.SCARED -> {
                drawCircle(Color.White, radius = radius * 0.15f, center = Offset(ex, eyeY))
                drawCircle(FaceInk, radius = radius * 0.15f, center = Offset(ex, eyeY), style = Stroke(width = line * 0.5f))
                val pupil = if (emotion == Emotion.SCARED) 0.05f else 0.07f
                drawCircle(FaceInk, radius = radius * pupil, center = Offset(ex, eyeY))
                drawArc(
                    FaceInk, 200f, 140f, false,
                    topLeft = Offset(ex - radius * 0.16f, eyeY - radius * 0.38f),
                    size = Size(radius * 0.32f, radius * 0.2f),
                    style = Stroke(width = line * 0.7f, cap = StrokeCap.Round),
                )
            }
        }
    }

    // 입
    val mouthY = cy + radius * 0.3f
    when (emotion) {
        Emotion.HAPPY -> {
            drawArc(
                Color(0xFFB23A48), 0f, 180f, true,
                topLeft = Offset(cx - radius * 0.3f, mouthY - radius * 0.2f),
                size = Size(radius * 0.6f, radius * 0.44f),
            )
            drawArc(
                Color(0xFFFF8A9A), 20f, 140f, true,
                topLeft = Offset(cx - radius * 0.15f, mouthY - radius * 0.02f),
                size = Size(radius * 0.3f, radius * 0.24f),
            )
        }
        Emotion.SAD -> drawArc(
            FaceInk, 200f, 140f, false,
            topLeft = Offset(cx - radius * 0.22f, mouthY),
            size = Size(radius * 0.44f, radius * 0.3f),
            style = stroke,
        )
        Emotion.ANGRY -> {
            drawRoundRect(
                FaceInk,
                topLeft = Offset(cx - radius * 0.26f, mouthY),
                size = Size(radius * 0.52f, radius * 0.18f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius * 0.08f),
            )
            drawLine(
                Color.White,
                start = Offset(cx - radius * 0.2f, mouthY + radius * 0.09f),
                end = Offset(cx + radius * 0.2f, mouthY + radius * 0.09f),
                strokeWidth = line * 0.4f,
            )
        }
        Emotion.SURPRISED -> drawOval(
            FaceInk,
            topLeft = Offset(cx - radius * 0.12f, mouthY - radius * 0.06f),
            size = Size(radius * 0.24f, radius * 0.3f),
        )
        Emotion.SCARED -> {
            val path = Path().apply {
                moveTo(cx - radius * 0.28f, mouthY + radius * 0.08f)
                val step = radius * 0.14f
                for (i in 0 until 4) {
                    val x = cx - radius * 0.28f + step * (i + 1)
                    val y = mouthY + if (i % 2 == 0) -radius * 0.02f else radius * 0.08f
                    lineTo(x, y)
                }
            }
            drawPath(path, FaceInk, style = Stroke(width = line * 0.8f, cap = StrokeCap.Round))
        }
    }

    // 눈물, 김, 땀방울
    when (emotion) {
        Emotion.SAD -> {
            val p = t
            val tearY = eyeY + radius * 0.14f + p * radius * 0.5f
            drawOval(
                Color(0xFF64B5F6).copy(alpha = 1f - p * 0.7f),
                topLeft = Offset(cx - eyeDx - radius * 0.05f, tearY),
                size = Size(radius * 0.1f, radius * 0.15f),
            )
        }
        Emotion.ANGRY -> {
            val puff = (t * 2f) % 1f
            listOf(-1f, 1f).forEach { side ->
                drawCircle(
                    Color.White.copy(alpha = 0.8f * (1f - puff)),
                    radius = radius * (0.08f + puff * 0.08f),
                    center = Offset(cx + side * radius * 0.95f, cy - radius * (0.7f + puff * 0.3f)),
                )
            }
        }
        Emotion.SCARED -> drawOval(
            Color(0xFF81D4FA),
            topLeft = Offset(cx + radius * 0.62f, cy - radius * 0.55f + abs(wave) * radius * 0.05f),
            size = Size(radius * 0.13f, radius * 0.2f),
        )
        else -> Unit
    }
}
