package com.ssukssuk.playground.ui.games

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.content.Consonant
import com.ssukssuk.playground.content.HangulContent
import com.ssukssuk.playground.content.Lines
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.logic.HangulQuiz
import com.ssukssuk.playground.ui.components.AnswerCard
import com.ssukssuk.playground.ui.components.AnswerState
import com.ssukssuk.playground.ui.components.BurstEffect
import com.ssukssuk.playground.ui.components.GameScaffold
import com.ssukssuk.playground.ui.components.KidIcons
import com.ssukssuk.playground.ui.components.PaintStroke
import com.ssukssuk.playground.ui.components.PillButton
import com.ssukssuk.playground.ui.components.PraisePop
import com.ssukssuk.playground.ui.components.RoundIconButton
import com.ssukssuk.playground.ui.components.ShakeState
import com.ssukssuk.playground.ui.components.bouncyClick
import com.ssukssuk.playground.ui.components.drawPaintStroke
import com.ssukssuk.playground.ui.components.paintInput
import com.ssukssuk.playground.ui.components.rememberBob
import com.ssukssuk.playground.ui.components.rememberPulse
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class HangulMode { LEARN, QUIZ }

/** 한글 놀이: 자음 카드 보며 따라 쓰기, 첫소리가 같은 그림 찾기 */
@Composable
fun HangulGame(env: GameEnv) {
    var mode by remember { mutableStateOf<HangulMode?>(null) }
    when (mode) {
        null -> HangulModePicker(env) { mode = it }
        HangulMode.LEARN -> HangulLearn(env)
        HangulMode.QUIZ -> HangulQuizPlay(env)
    }
}

@Composable
private fun HangulModePicker(env: GameEnv, onPick: (HangulMode) -> Unit) {
    LaunchedEffect(Unit) {
        delay(700)
        env.say(Lines.HANGUL_MODE)
    }
    GameScaffold(title = env.game.title, color = Color(env.game.colorArgb), onHome = env.onHome) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(28.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ModeCard("📖", "글자 카드", "자음을 보고 따라 써요", Color(0xFFFFE0B2)) {
                env.say(Lines.HANGUL_MODE_CARDS)
                onPick(HangulMode.LEARN)
            }
            ModeCard("🎯", "글자 찾기", "첫소리가 같은 그림을 찾아요", Color(0xFFC8E6C9)) {
                env.say(Lines.HANGUL_MODE_QUIZ)
                onPick(HangulMode.QUIZ)
            }
        }
    }
}

@Composable
private fun ModeCard(icon: String, title: String, subtitle: String, color: Color, onClick: () -> Unit) {
    val pulse = rememberPulse(0.97f, 1.03f, 1300)
    Column(
        modifier = Modifier
            .graphicsLayer {
                scaleX = pulse.value
                scaleY = pulse.value
            }
            .shadow(10.dp, RoundedCornerShape(32.dp))
            .background(color, RoundedCornerShape(32.dp))
            .bouncyClick(onClick = onClick)
            .padding(horizontal = 36.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(icon, fontSize = 64.sp)
        Text(title, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = KidsColors.Ink)
        Text(subtitle, fontSize = 15.sp, color = KidsColors.InkSoft)
    }
}

/** 첫 글자를 강조한 낱말: 고양이 → [고]양이 */
private fun highlightedWord(word: String) = buildAnnotatedString {
    withStyle(SpanStyle(color = KidsColors.Accent)) { append(word.take(1)) }
    append(word.drop(1))
}

@Composable
private fun HangulLearn(env: GameEnv) {
    val letters = HangulContent.consonants
    var index by remember { mutableIntStateOf(0) }
    val consonant = letters[index]
    val strokes = remember(index) { mutableStateListOf<PaintStroke>() }
    val letterScale = remember { Animatable(0.4f) }
    val strokeWidth = with(LocalDensity.current) { 18.dp.toPx() }

    fun speakCard() = env.say(Lines.consonantCard(consonant))

    LaunchedEffect(index) {
        letterScale.snapTo(0.4f)
        launch { letterScale.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessLow)) }
        delay(if (index == 0) 700 else 250)
        if (index == 0) {
            env.say("${Lines.consonantCard(consonant)} ${Lines.HANGUL_TRACE}")
        } else {
            speakCard()
        }
    }

    GameScaffold(
        title = "글자 카드",
        color = Color(env.game.colorArgb),
        onHome = env.onHome,
        onReplayVoice = ::speakCard,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // 따라 쓰기 판
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .shadow(8.dp, RoundedCornerShape(28.dp))
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                val letterSize = with(LocalDensity.current) { (min(maxWidth, maxHeight) * 0.78f).toSp() }
                Text(
                    text = consonant.letter,
                    fontSize = letterSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFB74D).copy(alpha = 0.35f),
                    modifier = Modifier.graphicsLayer {
                        scaleX = letterScale.value
                        scaleY = letterScale.value
                    },
                )
                Canvas(
                    Modifier
                        .matchParentSize()
                        .paintInput(key = index, onStart = {
                            PaintStroke(color = KidsColors.Accent, width = strokeWidth).also { strokes.add(it) }
                        }),
                ) {
                    strokes.forEach { drawPaintStroke(it) }
                }
                Text(
                    text = "✍️ 따라 써 봐요",
                    fontSize = 16.sp,
                    color = KidsColors.InkSoft,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(14.dp),
                )
                RoundIconButton(
                    icon = KidIcons.trash(KidsColors.Ink),
                    contentDescription = "지우기",
                    onClick = { strokes.clear() },
                    size = 48.dp,
                    background = Color(0xFFFFF3E0),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                )
            }
            // 그림과 낱말
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                val bob = rememberBob(periodMillis = 1600)
                Text(
                    text = consonant.emoji,
                    fontSize = 92.sp,
                    modifier = Modifier
                        .graphicsLayer {
                            translationY = bob.value * 8.dp.toPx()
                            rotationZ = bob.value * 5f
                        }
                        .bouncyClick {
                            env.play(Sfx.STAR)
                            env.say(Lines.word(consonant.word))
                        },
                )
                Text(highlightedWord(consonant.word), fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = KidsColors.Ink)
                Text(
                    text = "${consonant.letter}  ${consonant.name}",
                    fontSize = 24.sp,
                    color = KidsColors.InkSoft,
                    modifier = Modifier.bouncyClick { env.say(Lines.consonantName(consonant)) },
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RoundIconButton(
                        icon = KidIcons.arrowLeft(KidsColors.Ink),
                        contentDescription = "앞 글자",
                        enabled = index > 0,
                        onClick = {
                            env.play(Sfx.FLIP)
                            index--
                        },
                    )
                    Text(
                        "${index + 1} / ${letters.size}",
                        fontSize = 20.sp,
                        color = KidsColors.InkSoft,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                    if (index < letters.lastIndex) {
                        RoundIconButton(
                            icon = KidIcons.arrowRight(KidsColors.Ink),
                            contentDescription = "다음 글자",
                            onClick = {
                                env.play(Sfx.FLIP)
                                index++
                            },
                        )
                    } else {
                        PillButton(
                            text = "다 봤어요!",
                            icon = "✅",
                            fontSize = 18.sp,
                            onClick = { env.complete() },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HangulQuizPlay(env: GameEnv) {
    val difficulty = env.difficulty
    val questions = remember {
        HangulQuiz.generate(
            random = env.random,
            count = difficulty.questionsPerRound,
            choiceCount = difficulty.hangulChoices,
            similarDistractors = difficulty.hangulSimilarDistractors,
        )
    }
    var index by remember { mutableIntStateOf(0) }
    var firstTry by remember { mutableIntStateOf(0) }
    var missed by remember { mutableStateOf(false) }
    var solved by remember { mutableStateOf(false) }
    var disabled by remember { mutableStateOf(emptySet<String>()) }
    var burst by remember { mutableIntStateOf(0) }
    var praise by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val question = questions[index]
    val shakes = remember(index) { question.choices.associate { it.letter to ShakeState() } }

    fun prompt() = env.say(Lines.hangulQuizPrompt(question.target))

    LaunchedEffect(index) {
        delay(if (index == 0) 800 else 300)
        prompt()
    }

    fun choose(choice: Consonant) {
        if (solved || choice.letter in disabled) return
        if (choice == question.target) {
            solved = true
            if (!missed) firstTry++
            env.play(Sfx.CORRECT)
            burst++
            val word = env.praise(afterMiss = missed)
            praise = word.substringBefore(' ')
            env.say(Lines.hangulQuizCorrect(word, question.target, choice))
            scope.launch {
                delay(2600)
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
            disabled = disabled + choice.letter
            env.play(Sfx.WRONG)
            env.say(Lines.hangulQuizWrong(choice))
            scope.launch { shakes[choice.letter]?.shake() }
        }
    }

    GameScaffold(
        title = "글자 찾기",
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            val pulse = rememberPulse(0.95f, 1.05f, 1000)
            Box(
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxHeight(0.9f)
                    .shadow(8.dp, RoundedCornerShape(32.dp))
                    .background(Color.White, RoundedCornerShape(32.dp))
                    .bouncyClick { env.say(Lines.consonantName(question.target)) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = question.target.letter,
                    fontSize = 140.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = KidsColors.Accent,
                    modifier = Modifier.graphicsLayer {
                        scaleX = pulse.value
                        scaleY = pulse.value
                    },
                )
                Text(
                    text = question.target.name,
                    fontSize = 22.sp,
                    color = KidsColors.InkSoft,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                )
                BurstEffect(trigger = burst, modifier = Modifier.matchParentSize())
            }
            Row(
                modifier = Modifier.weight(1.7f),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                question.choices.forEach { choice ->
                    val state = when {
                        solved && choice == question.target -> AnswerState.Correct
                        choice.letter in disabled || solved -> AnswerState.Dimmed
                        disabled.size >= 2 && choice == question.target -> AnswerState.Hint
                        else -> AnswerState.Normal
                    }
                    AnswerCard(
                        state = state,
                        shakeState = shakes.getValue(choice.letter),
                        onClick = { choose(choice) },
                        contentDescription = choice.word,
                        modifier = Modifier
                            .weight(1f)
                            .height(190.dp),
                    ) {
                        Text(choice.emoji, fontSize = 64.sp)
                        Spacer(Modifier.height(6.dp))
                        Text(highlightedWord(choice.word), fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = KidsColors.Ink)
                    }
                }
            }
        }
        PraisePop(text = praise, trigger = burst, modifier = Modifier.fillMaxSize())
    }
}
