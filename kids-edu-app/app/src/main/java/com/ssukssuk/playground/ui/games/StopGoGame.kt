package com.ssukssuk.playground.ui.games

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.content.Lines
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.logic.StopGoPlan
import com.ssukssuk.playground.logic.StopGoTrial
import com.ssukssuk.playground.ui.components.BurstEffect
import com.ssukssuk.playground.ui.components.ChunkyBox
import com.ssukssuk.playground.ui.components.GameScaffold
import com.ssukssuk.playground.ui.components.PillButton
import com.ssukssuk.playground.ui.components.ShakeState
import com.ssukssuk.playground.ui.components.shake
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Meadow = Color(0xFFFFF3D6)
private val Hole = Color(0xFF8FBF6C)

/** 팔각형 (멈춤 표지) */
private val OctagonShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    moveTo(w * 0.3f, 0f)
    lineTo(w * 0.7f, 0f)
    lineTo(w, h * 0.3f)
    lineTo(w, h * 0.7f)
    lineTo(w * 0.7f, h)
    lineTo(w * 0.3f, h)
    lineTo(0f, h * 0.7f)
    lineTo(0f, h * 0.3f)
    close()
}

/**
 * 멈춰! 놀이 (Go/No-Go): 초록 친구가 나오면 톡 누르고, 빨간 친구가 나오면 누르지 않고 멈춥니다.
 * 2단계부터는 중간에 "이번엔 반대로!" 규칙이 바뀝니다.
 *
 * 유아의 실행기능(억제·작업기억·전환)은 규칙을 기억하며 하고 싶은 행동을 멈추는 놀이로 연습할 수 있고
 * (Diamond & Lee 2011), 규칙이 바뀌는 놀이는 DCCS·머리-발끝-무릎-어깨 과제와 같은 원리입니다.
 * 색만으로 구별하지 않도록 초록은 동그라미 체크, 빨강은 팔각형 표지로 모양도 다르게 했습니다.
 */
@Composable
fun StopGoGame(env: GameEnv) {
    val d = env.difficulty
    val plan = remember { StopGoPlan.generate(env.random, d.stopGoTrials, d.stopGoSwitchRule) }
    val switchAt = remember { StopGoPlan.switchIndex(plan) }
    val tapped = remember { mutableStateListOf<Boolean>() }
    var started by remember { mutableStateOf(false) }
    var current by remember { mutableIntStateOf(-1) }
    var reversed by remember { mutableStateOf(false) }
    var hit by remember { mutableIntStateOf(0) }
    var showing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val pop = remember { Animatable(0f) }
    val shakeState = remember { ShakeState() }

    LaunchedEffect(Unit) {
        delay(800)
        env.say(Lines.STOP_RULE)
    }

    LaunchedEffect(started) {
        if (!started) return@LaunchedEffect
        env.say(Lines.STOP_READY)
        delay(1500)
        for (i in plan.indices) {
            if (i == switchAt) {
                reversed = true
                env.say(Lines.STOP_RULE_REVERSED)
                delay(4200)
            }
            current = i
            tapped.add(false)
            showing = true
            pop.snapTo(0f)
            pop.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium))
            val shownAt = System.currentTimeMillis()
            while (showing && System.currentTimeMillis() - shownAt < d.stopGoWindowMillis) delay(30)
            if (showing) {
                pop.animateTo(0f, tween(160))
                showing = false
            }
            delay(650)
        }
        current = -1
        env.play(Sfx.CHEER)
        env.say(Lines.STOP_DONE)
        delay(1800)
        env.complete(StopGoPlan.score(plan, tapped), plan.size)
    }

    fun tapFriend() {
        val i = current
        if (i < 0 || !showing || tapped.getOrNull(i) != false) return
        tapped[i] = true
        val trial = plan[i]
        if (trial.shouldTap) {
            env.play(Sfx.POP)
            hit++
            scope.launch {
                pop.animateTo(0f, tween(180))
                showing = false
            }
        } else {
            env.play(Sfx.WRONG)
            env.say(if (trial.reversed) Lines.STOP_OOPS_GREEN else Lines.STOP_OOPS_RED)
            scope.launch { shakeState.shake() }
        }
    }

    GameScaffold(
        title = env.game.title,
        color = Color(env.game.colorArgb),
        onHome = env.onHome,
        progress = (current + if (showing) 0 else 1).coerceIn(0, plan.size).takeIf { started } ?: 0,
        total = plan.size,
        onReplayVoice = { env.say(if (reversed) Lines.STOP_RULE_REVERSED else Lines.STOP_RULE) },
        background = Meadow,
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val ruleHeight = min(maxHeight * 0.24f, 96.dp)
            val cellW = min((maxWidth - 48.dp - 60.dp) / 3, 220.dp)
            val cellH = min((maxHeight * 0.66f - 24.dp) / 2, 150.dp)
            Canvas(Modifier.fillMaxSize()) {
                drawRect(Color(0xFFCDEBB0), topLeft = Offset(0f, size.height * 0.28f))
            }
            Column(Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                RuleCard(reversed = reversed, height = ruleHeight)
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    for (row in 0 until 2) {
                        Row(horizontalArrangement = Arrangement.spacedBy(30.dp)) {
                            for (col in 0 until 3) {
                                val hole = row * 3 + col
                                val trial = plan.getOrNull(current)?.takeIf { it.hole == hole && current >= 0 }
                                HoleCell(
                                    width = cellW,
                                    height = cellH,
                                    trial = trial,
                                    pop = { pop.value },
                                    shakeState = shakeState,
                                    burst = if (trial != null) hit else 0,
                                    onTap = ::tapFriend,
                                )
                            }
                        }
                    }
                }
            }
            if (!started) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0x66FFF3D6))
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { },
                    contentAlignment = Alignment.Center,
                ) {
                    PillButton(text = "시작!", fontSize = 30.sp, onClick = { started = true })
                }
            }
        }
    }
}

@Composable
private fun RuleCard(reversed: Boolean, height: Dp) {
    ChunkyBox(
        modifier = Modifier.height(height),
        shadow = Color(0xFFEAD6A6),
        radius = 28.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            if (reversed) {
                Text("반대로!", fontSize = 20.sp, color = Color.White, modifier = Modifier.background(KidsColors.Ink, RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 4.dp))
            }
            val font = (height.value * 0.27f).sp
            SignBadge(green = !reversed, size = height * 0.52f)
            Text(if (reversed) "빨간 친구는 톡!" else "초록 친구는 톡!", fontSize = font, color = KidsColors.Ink)
            Box(Modifier.size(width = 3.dp, height = height * 0.5f).background(Color(0xFFF0E4C8)))
            SignBadge(green = reversed, size = height * 0.52f)
            Text(if (reversed) "초록 친구는 멈춰!" else "빨간 친구는 멈춰!", fontSize = font, color = KidsColors.Ink)
        }
    }
}

/** 초록 = 동그라미 안 체크, 빨강 = 팔각형 안 가로 막대 */
@Composable
private fun SignBadge(green: Boolean, size: Dp) {
    Box(
        Modifier
            .size(size)
            .background(if (green) KidsColors.Go else KidsColors.Stop, if (green) CircleShape else OctagonShape),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(size * 0.6f)) {
            val w = this.size.width
            val h = this.size.height
            if (green) {
                drawLine(Color.White, Offset(w * 0.12f, h * 0.52f), Offset(w * 0.4f, h * 0.8f), strokeWidth = w * 0.16f, cap = StrokeCap.Round)
                drawLine(Color.White, Offset(w * 0.4f, h * 0.8f), Offset(w * 0.9f, h * 0.2f), strokeWidth = w * 0.16f, cap = StrokeCap.Round)
            } else {
                drawLine(Color.White, Offset(w * 0.1f, h * 0.5f), Offset(w * 0.9f, h * 0.5f), strokeWidth = h * 0.24f, cap = StrokeCap.Round)
            }
        }
    }
}

@Composable
private fun HoleCell(
    width: Dp,
    height: Dp,
    trial: StopGoTrial?,
    pop: () -> Float,
    shakeState: ShakeState,
    burst: Int,
    onTap: () -> Unit,
) {
    Box(Modifier.size(width, height), contentAlignment = Alignment.BottomCenter) {
        Box(
            Modifier
                .size(width * 0.9f, height * 0.3f)
                .background(Hole, CircleShape),
        )
        if (trial != null) {
            val face = min(height * 0.82f, width * 0.62f)
            Box(
                Modifier
                    .padding(bottom = height * 0.12f)
                    .size(face)
                    .shake(shakeState)
                    .graphicsLayer {
                        val p = pop()
                        scaleX = p
                        scaleY = p
                        translationY = (1f - p) * face.toPx() * 0.4f
                    }
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onTap)
                    .semantics { contentDescription = if (trial.green) "초록 친구" else "빨간 친구" },
            ) {
                Friend(green = trial.green, modifier = Modifier.fillMaxSize())
                SignBadge(green = trial.green, size = face * 0.36f)
                BurstEffect(trigger = burst, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

/** 초록 친구(곰)와 빨간 친구(고양이). 표지와 테두리 색이 규칙을 알려 줍니다. */
@Composable
private fun Friend(green: Boolean, modifier: Modifier) {
    Box(
        modifier
            .padding(top = 6.dp, start = 6.dp)
            .background(if (green) Color(0xFFFFD08A) else Color(0xFFD9D4F2), CircleShape)
            .border(5.dp, if (green) KidsColors.Go else KidsColors.Stop, CircleShape),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val ink = KidsColors.Ink
            if (green) {
                drawCircle(Color(0xFFE9A85A), radius = w * 0.12f, center = Offset(w * 0.22f, h * 0.2f))
                drawCircle(Color(0xFFE9A85A), radius = w * 0.12f, center = Offset(w * 0.78f, h * 0.2f))
            } else {
                drawLine(Color(0xFFA79BDE), Offset(w * 0.2f, h * 0.28f), Offset(w * 0.28f, h * 0.08f), strokeWidth = w * 0.1f, cap = StrokeCap.Round)
                drawLine(Color(0xFFA79BDE), Offset(w * 0.8f, h * 0.28f), Offset(w * 0.72f, h * 0.08f), strokeWidth = w * 0.1f, cap = StrokeCap.Round)
            }
            drawCircle(ink, radius = w * 0.055f, center = Offset(w * 0.36f, h * 0.48f))
            drawCircle(ink, radius = w * 0.055f, center = Offset(w * 0.64f, h * 0.48f))
            drawLine(ink, Offset(w * 0.42f, h * 0.66f), Offset(w * 0.58f, h * 0.66f), strokeWidth = w * 0.04f, cap = StrokeCap.Round)
        }
    }
}
