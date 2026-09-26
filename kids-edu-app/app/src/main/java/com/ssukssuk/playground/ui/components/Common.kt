package com.ssukssuk.playground.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.core.Services
import com.ssukssuk.playground.core.SilentSoundPlayer
import com.ssukssuk.playground.core.SilentSpeaker
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

val LocalServices = staticCompositionLocalOf { Services(SilentSpeaker, SilentSoundPlayer) }

/** 누르면 말랑하게 작아졌다가 튕겨 돌아오는 클릭 효과 */
fun Modifier.bouncyClick(enabled: Boolean = true, onClick: () -> Unit): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMediumLow),
        label = "bouncyClick",
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interaction,
            indication = null,
            enabled = enabled,
            onClick = onClick,
        )
}

/** 오답일 때 좌우로 도리도리 흔드는 효과 */
@Stable
class ShakeState {
    internal val offset = Animatable(0f)

    suspend fun shake() {
        offset.snapTo(0f)
        offset.animateTo(
            targetValue = 0f,
            animationSpec = keyframes {
                durationMillis = 420
                -16f at 50
                16f at 110
                -12f at 170
                12f at 230
                -6f at 290
                6f at 350
            },
        )
    }
}

@Composable
fun rememberShakeState(): ShakeState = remember { ShakeState() }

fun Modifier.shake(state: ShakeState): Modifier = graphicsLayer {
    translationX = state.offset.value * density
}

/**
 * 둥실둥실 떠 있는 값 (-1..1). [phase]로 여러 개가 서로 다르게 움직이게 합니다.
 * 매 프레임 다시 구성하지 않도록 graphicsLayer/그리기 단계에서 `.value`를 읽으세요.
 */
@Composable
fun rememberBob(periodMillis: Int = 1800, phase: Float = 0f): State<Float> {
    val transition = rememberInfiniteTransition(label = "bob")
    val t = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(periodMillis, easing = LinearEasing)),
        label = "bobValue",
    )
    return remember(t, phase) { derivedStateOf { sin((t.value + phase) * 2f * PI.toFloat()) } }
}

/** 콩닥콩닥 커졌다 작아지는 배율. graphicsLayer 안에서 `.value`를 읽으세요. */
@Composable
fun rememberPulse(min: Float = 0.94f, max: Float = 1.06f, periodMillis: Int = 900): State<Float> {
    val transition = rememberInfiniteTransition(label = "pulse")
    return transition.animateFloat(
        initialValue = min,
        targetValue = max,
        animationSpec = infiniteRepeatable(tween(periodMillis, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulseValue",
    )
}

@Composable
fun RoundIconButton(
    icon: String,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    background: Color = Color.White,
    enabled: Boolean = true,
) {
    val services = LocalServices.current
    Box(
        modifier = modifier
            .size(size)
            .shadow(4.dp, CircleShape)
            .background(background, CircleShape)
            .bouncyClick(enabled) {
                services.sound.play(Sfx.TAP)
                onClick()
            }
            .semantics {
                this.contentDescription = contentDescription
                role = Role.Button
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = icon,
            fontSize = (size.value * 0.44f).sp,
            modifier = Modifier.clearAndSetSemantics { },
        )
    }
}

/** 큼직한 알약 모양 버튼 */
@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: String? = null,
    color: Color = KidsColors.Leaf,
    contentColor: Color = Color.White,
    fontSize: TextUnit = 22.sp,
    enabled: Boolean = true,
) {
    val services = LocalServices.current
    Row(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(50))
            .background(if (enabled) color else color.copy(alpha = 0.4f), RoundedCornerShape(50))
            .bouncyClick(enabled) {
                services.sound.play(Sfx.TAP)
                onClick()
            }
            .padding(horizontal = 26.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Text(icon, fontSize = fontSize, modifier = Modifier.clearAndSetSemantics { })
            Spacer(Modifier.width(10.dp))
        }
        Text(text, fontSize = fontSize, color = contentColor, fontWeight = FontWeight.ExtraBold)
    }
}

/** 놀이 진행을 별로 보여 줍니다. 채워지는 순간 톡 커집니다. */
@Composable
fun ProgressStars(done: Int, total: Int, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(total) { i ->
            val filled = i < done
            val scale by animateFloatAsState(
                targetValue = if (filled) 1f else 0.72f,
                animationSpec = spring(dampingRatio = 0.35f, stiffness = Spring.StiffnessMedium),
                label = "star$i",
            )
            Text(
                text = "⭐",
                fontSize = 24.sp,
                modifier = Modifier.graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    alpha = if (filled) 1f else 0.28f
                },
            )
        }
    }
}

/**
 * 모든 놀이 화면의 공통 틀: 부드러운 배경, 집 버튼, 제목, 진행 별, 다시 듣기 버튼.
 */
@Composable
fun GameScaffold(
    title: String,
    color: Color,
    onHome: () -> Unit,
    modifier: Modifier = Modifier,
    progress: Int = 0,
    total: Int = 0,
    onReplayVoice: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val top = color.copy(alpha = 0.45f).compositeOver(Color.White)
    Box(
        modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(top, KidsColors.Cream))),
    ) {
        Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RoundIconButton(icon = "🏠", contentDescription = "처음 화면으로", onClick = onHome, size = 52.dp)
                Spacer(Modifier.width(12.dp))
                Text(title, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = KidsColors.Ink)
                Spacer(Modifier.weight(1f))
                if (total > 0) ProgressStars(done = progress, total = total)
                if (onReplayVoice != null) {
                    Spacer(Modifier.width(10.dp))
                    RoundIconButton(icon = "🔊", contentDescription = "다시 듣기", onClick = onReplayVoice, size = 52.dp)
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                content = content,
            )
        }
    }
}

/** 말풍선. 글자가 바뀌면 살짝 커지며 나타납니다. */
@Composable
fun SpeechBubble(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 20.sp,
    tail: BubbleTail = BubbleTail.Left,
) {
    val tailColor = Color.White
    Box(
        modifier = modifier
            .drawBehind {
                val path = Path()
                val t = 14.dp.toPx()
                when (tail) {
                    BubbleTail.Left -> {
                        path.moveTo(2f, size.height * 0.45f)
                        path.lineTo(-t, size.height * 0.62f)
                        path.lineTo(2f, size.height * 0.75f)
                    }
                    BubbleTail.Bottom -> {
                        path.moveTo(size.width * 0.3f, size.height - 2f)
                        path.lineTo(size.width * 0.24f, size.height + t)
                        path.lineTo(size.width * 0.44f, size.height - 2f)
                    }
                    BubbleTail.None -> Unit
                }
                path.close()
                drawPath(path, tailColor)
            }
            .shadow(3.dp, RoundedCornerShape(22.dp))
            .background(Color.White, RoundedCornerShape(22.dp))
            .border(2.dp, KidsColors.Ink.copy(alpha = 0.08f), RoundedCornerShape(22.dp))
            .padding(horizontal = 18.dp, vertical = 12.dp),
    ) {
        AnimatedContent(
            targetState = text,
            transitionSpec = { (fadeIn(tween(220)) + scaleIn(initialScale = 0.9f)) togetherWith fadeOut(tween(120)) },
            label = "bubbleText",
        ) { value ->
            Text(value, fontSize = fontSize, color = KidsColors.Ink, textAlign = TextAlign.Start, lineHeight = fontSize * 1.3f)
        }
    }
}

enum class BubbleTail { Left, Bottom, None }

/** [trigger]가 바뀔 때마다 별이 사방으로 톡 퍼집니다. */
@Composable
fun BurstEffect(
    trigger: Int,
    modifier: Modifier = Modifier,
    color: Color = KidsColors.Sun,
    particleCount: Int = 10,
) {
    val progress = remember { Animatable(1f) }
    LaunchedEffect(trigger) {
        if (trigger > 0) {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
        }
    }
    Canvas(modifier) {
        val p = progress.value
        if (p >= 1f) return@Canvas
        val maxRadius = size.minDimension * 0.62f
        val starRadius = size.minDimension * 0.07f * (1f - p * 0.5f)
        repeat(particleCount) { i ->
            val angle = i * 2f * PI.toFloat() / particleCount + p * 0.6f
            val distance = maxRadius * (0.25f + 0.75f * p)
            val pos = center + Offset(cos(angle) * distance, sin(angle) * distance)
            drawPath(
                path = starPath(pos, starRadius, starRadius * 0.45f),
                color = (if (i % 2 == 0) color else Color.White).copy(alpha = 1f - p),
            )
        }
    }
}

enum class AnswerState { Normal, Dimmed, Correct }

/** 정답 선택 카드. 맞히면 커지며 초록 테두리, 틀린 선택지는 흐려지고 흔들립니다. */
@Composable
fun AnswerCard(
    state: AnswerState,
    shakeState: ShakeState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (state == AnswerState.Correct) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMediumLow),
        label = "answerScale",
    )
    val alpha by animateFloatAsState(if (state == AnswerState.Dimmed) 0.4f else 1f, label = "answerAlpha")
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier = modifier
            .shake(shakeState)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .shadow(6.dp, shape)
            .background(if (state == AnswerState.Correct) Color(0xFFE8F5E9) else Color.White, shape)
            .then(if (state == AnswerState.Correct) Modifier.border(4.dp, KidsColors.Correct, shape) else Modifier)
            .bouncyClick(enabled = state != AnswerState.Dimmed, onClick = onClick)
            .then(
                if (contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else {
                    Modifier
                },
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = content,
    )
}
