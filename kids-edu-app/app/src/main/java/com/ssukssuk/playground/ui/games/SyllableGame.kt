package com.ssukssuk.playground.ui.games

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.content.Consonant
import com.ssukssuk.playground.content.Lines
import com.ssukssuk.playground.content.Vowel
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.logic.SyllableQuiz
import com.ssukssuk.playground.logic.SyllableResult
import com.ssukssuk.playground.ui.components.BurstEffect
import com.ssukssuk.playground.ui.components.ChunkyBox
import com.ssukssuk.playground.ui.components.GameScaffold
import com.ssukssuk.playground.ui.components.Mascot
import com.ssukssuk.playground.ui.components.MascotAction
import com.ssukssuk.playground.ui.components.MascotMood
import com.ssukssuk.playground.ui.components.PraisePop
import com.ssukssuk.playground.ui.components.ShakeState
import com.ssukssuk.playground.ui.components.SpeechBubble
import com.ssukssuk.playground.ui.components.rememberPulse
import com.ssukssuk.playground.ui.components.shake
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 글자 만들기: 자음 조각과 모음 조각을 눌러 글자 블록에 넣으면 소리를 이어 읽어 줍니다("느, 아, 나!").
 *
 * 한글을 처음 읽는 아이에게는 낱자보다 '자음+모음' 음절이 먼저 잡히고, 음절 지식이 이후 낱자 소리 인식을
 * 이끈다는 연구(Cho 2009)에 따라 만들었습니다. 틀린 조각을 넣어도 만들어진 글자를 그대로 읽어 주고,
 * 무엇이 필요한지 소리로 알려 줍니다. 1단계는 모음 ㅏ 하나로 자음 소리에만 집중합니다.
 */
@Composable
fun SyllableGame(env: GameEnv) {
    val d = env.difficulty
    val questions = remember {
        SyllableQuiz.generate(
            env.random, d.questionsPerRound, d.syllableVowels, d.syllableConsonantChoices,
            d.syllableVowelChoices, d.hangulSimilarDistractors,
        )
    }
    var index by remember { mutableIntStateOf(0) }
    var firstTry by remember { mutableIntStateOf(0) }
    var missed by remember { mutableStateOf(false) }
    var solved by remember { mutableStateOf(false) }
    var consonant by remember { mutableStateOf<Consonant?>(null) }
    var vowel by remember { mutableStateOf<Vowel?>(null) }
    var busy by remember { mutableStateOf(false) }
    var burst by remember { mutableIntStateOf(0) }
    var praise by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val question = questions[index]
    val target = question.target
    val shakes = remember(index) { ShakeState() }
    val made = consonant?.let { c -> vowel?.let { v -> SyllableQuiz.made(c, v) } }

    fun prompt() = env.say(Lines.syllablePrompt(target))

    LaunchedEffect(index) {
        delay(if (index == 0) 800 else 300)
        prompt()
    }

    fun check() {
        val c = consonant ?: return
        val v = vowel ?: return
        busy = true
        val result = SyllableQuiz.check(target, c, v)
        if (result == SyllableResult.CORRECT) {
            solved = true
            if (!missed) firstTry++
            env.play(Sfx.CORRECT)
            burst++
            val word = env.praise(afterMiss = missed)
            praise = word.substringBefore(' ')
            env.say("${Lines.syllableBlend(c, v)} ${Lines.syllableCorrect(word, target)}")
            scope.launch {
                delay(3600)
                if (index + 1 < questions.size) {
                    index++
                    solved = false
                    missed = false
                    consonant = null
                    vowel = null
                } else {
                    env.complete(firstTry, questions.size)
                }
                busy = false
            }
        } else {
            missed = true
            env.play(Sfx.WRONG)
            val need = if (result == SyllableResult.WRONG_VOWEL) Lines.syllableNeedVowel(target) else Lines.syllableNeedConsonant(target)
            val madeLine = made?.let { Lines.syllableMade(it) } ?: ""
            env.say("${Lines.syllableBlend(c, v)} $madeLine $need")
            scope.launch {
                shakes.shake()
                delay(2600)
                // 틀린 조각만 빼고 맞는 조각은 남겨 둡니다.
                if (result != SyllableResult.WRONG_VOWEL) consonant = null
                if (result != SyllableResult.WRONG_CONSONANT) vowel = null
                busy = false
            }
        }
    }

    fun pickConsonant(c: Consonant) {
        if (solved || busy) return
        env.play(Sfx.TAP)
        consonant = c
        if (vowel == null) env.say(Lines.syllableTapConsonant(c)) else check()
    }

    fun pickVowel(v: Vowel) {
        if (solved || busy) return
        env.play(Sfx.TAP)
        vowel = v
        if (consonant == null) env.say(Lines.syllableTapVowel(v)) else check()
    }

    val color = Color(env.game.domain.colorArgb)
    val deep = Color(env.game.domain.deepArgb)
    val soft = Color(env.game.domain.softArgb)
    val shadow = Color(env.game.domain.shadowArgb)

    GameScaffold(
        title = env.game.title,
        color = deep,
        onHome = env.onHome,
        progress = index + if (solved) 1 else 0,
        total = questions.size,
        onReplayVoice = ::prompt,
    ) {
        BoxWithConstraints(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 8.dp)) {
            val unit = min(maxHeight / 230f, maxWidth / 520f)
            Column(Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxWidth().weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1.5f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Mascot(
                                modifier = Modifier.size(width = unit * 48, height = unit * 54),
                                mood = if (solved) MascotMood.EXCITED else MascotMood.HAPPY,
                                action = if (solved) MascotAction.CHEER else MascotAction.IDLE,
                            )
                            Spacer(Modifier.width(10.dp))
                            SpeechBubble(
                                text = if (missed && !solved) {
                                    "${target.word}의 '${target.syllable}'\n${Lines.syllableConsonantHint(target.initial)}"
                                } else {
                                    "${target.word}의 '${target.syllable}'를 만들어 볼까?"
                                },
                                fontSize = (unit.value * 11f).coerceIn(16f, 24f).sp,
                            )
                        }
                        Spacer(Modifier.height(unit * 8))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ChunkyBox(
                                modifier = Modifier.shake(shakes),
                                shadow = shadow,
                                radius = unit * 16,
                                depth = 7.dp,
                            ) {
                                Row(
                                    modifier = Modifier.padding(unit * 8),
                                    horizontalArrangement = Arrangement.spacedBy(unit * 6),
                                ) {
                                    Slot(consonant?.letter, unit, soft, deep, "여기에 쏙!")
                                    Slot(vowel?.letter?.toString(), unit, soft, deep, "여기에 쏙!")
                                }
                            }
                            Text("→", fontSize = (unit.value * 20).sp, color = KidsColors.PaperShadow, modifier = Modifier.padding(horizontal = unit * 6))
                            ResultTile(made, solved, unit, deep)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    TargetCard(target.emoji, target.word, unit, deep, burst, Modifier.weight(0.8f).fillMaxHeight(0.82f))
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    question.consonants.forEach { c ->
                        Piece(c.letter, used = consonant == c, unit = unit, onClick = { pickConsonant(c) }, label = c.name)
                    }
                    Box(Modifier.size(width = 3.dp, height = unit * 36).background(KidsColors.PaperShadow, RoundedCornerShape(2.dp)))
                    question.vowels.forEach { v ->
                        Piece(
                            v.letter.toString(),
                            used = vowel == v,
                            unit = unit,
                            onClick = { pickVowel(v) },
                            label = v.sound,
                            accent = color,
                            accentShadow = deep,
                        )
                    }
                }
            }
        }
        PraisePop(text = praise, trigger = burst, modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun Slot(letter: String?, unit: Dp, soft: Color, deep: Color, hint: String) {
    val shape = RoundedCornerShape(unit * 12)
    Box(
        modifier = Modifier
            .size(width = unit * 54, height = unit * 70)
            .then(
                if (letter != null) {
                    Modifier.background(soft, shape)
                } else {
                    // 비어 있는 칸은 점선으로 "여기에 넣어요"를 보여 줍니다.
                    Modifier.drawBehind {
                        val r = (unit * 12).toPx()
                        drawRoundRect(
                            color = deep.copy(alpha = 0.35f),
                            cornerRadius = CornerRadius(r, r),
                            style = Stroke(width = 3.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f))),
                        )
                    }
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (letter != null) {
            Text(letter, fontSize = (unit.value * 48).sp, color = deep)
        } else {
            Text(hint, fontSize = (unit.value * 8).coerceAtLeast(12f).sp, color = deep.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun ResultTile(made: Char?, solved: Boolean, unit: Dp, deep: Color) {
    val pop = remember { Animatable(1f) }
    LaunchedEffect(made) {
        if (made != null) {
            pop.snapTo(0.5f)
            pop.animateTo(1f, spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMediumLow))
        }
    }
    ChunkyBox(
        modifier = Modifier
            .size(unit * 72)
            .graphicsLayer {
                scaleX = pop.value
                scaleY = pop.value
            }
            .semantics { contentDescription = made?.toString() ?: "아직 만들지 않은 글자" },
        color = if (solved) KidsColors.CorrectSoft else Color.White,
        shadow = if (solved) Color(0xFFA8DDB5) else KidsColors.PaperShadow,
        radius = unit * 18,
    ) {
        Text(made?.toString() ?: "?", fontSize = (unit.value * 44).sp, color = if (made == null) KidsColors.PaperShadow else deep)
    }
}

@Composable
private fun TargetCard(emoji: String, word: String, unit: Dp, deep: Color, burst: Int, modifier: Modifier) {
    ChunkyBox(modifier = modifier, radius = unit * 18) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = (unit.value * 44).sp)
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = deep)) { append(word.take(1)) }
                    withStyle(SpanStyle(color = KidsColors.Ink)) { append(word.drop(1)) }
                },
                fontSize = (unit.value * 26).sp,
            )
        }
        BurstEffect(trigger = burst, modifier = Modifier.matchParentSize())
    }
}

@Composable
private fun Piece(
    letter: String,
    used: Boolean,
    unit: Dp,
    onClick: () -> Unit,
    label: String,
    accent: Color? = null,
    accentShadow: Color? = null,
) {
    val pulse = rememberPulse(0.97f, 1.03f, 1200)
    ChunkyBox(
        modifier = Modifier
            .size(unit * 50)
            .graphicsLayer {
                val s = if (used) 0.92f else pulse.value
                scaleX = s
                scaleY = s
                alpha = if (used) 0.45f else 1f
            }
            .semantics { contentDescription = label },
        color = accent ?: Color.White,
        shadow = accentShadow ?: KidsColors.PaperShadow,
        radius = unit * 14,
        onClick = onClick,
    ) {
        Text(letter, fontSize = (unit.value * 32).sp, color = if (accent != null) Color.White else KidsColors.Ink)
    }
}
