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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
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

/**
 * 종이 공작처럼 바닥에 두툼한 그림자가 있는 판. 누르면 그림자 쪽으로 쏙 들어갑니다.
 * [onClick]이 없으면 누를 수 없는 판입니다.
 */
@Composable
fun ChunkyBox(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    shadow: Color = KidsColors.PaperShadow,
    radius: Dp = 26.dp,
    depth: Dp = 6.dp,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val press by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "chunkyPress",
    )
    val shape = RoundedCornerShape(radius)
    Box(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable(interactionSource = interaction, indication = null, enabled = enabled, onClick = onClick)
                } else {
                    Modifier
                },
            )
            .drawBehind {
                val d = depth.toPx()
                val r = radius.toPx()
                drawRoundRect(
                    color = shadow,
                    topLeft = Offset(0f, d),
                    size = androidx.compose.ui.geometry.Size(size.width, size.height - d),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r),
                )
            }
            .padding(bottom = depth),
        contentAlignment = contentAlignment,
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { translationY = press * depth.toPx() }
                .background(color, shape),
        )
        Box(
            modifier = Modifier
                .graphicsLayer { translationY = press * depth.toPx() },
            contentAlignment = contentAlignment,
            content = content,
        )
    }
}

@Composable
fun RoundIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 60.dp,
    background: Color = Color.White,
    shadow: Color = KidsColors.PaperShadow,
    enabled: Boolean = true,
) {
    val services = LocalServices.current
    ChunkyBox(
        modifier = modifier
            .size(size)
            .graphicsLayer { alpha = if (enabled) 1f else 0.35f }
            .semantics {
                this.contentDescription = contentDescription
                role = Role.Button
            },
        color = background,
        shadow = shadow,
        radius = size * 0.34f,
        depth = 5.dp,
        enabled = enabled,
        onClick = {
            services.sound.play(Sfx.TAP)
            onClick()
        },
    ) {
        VectorIcon(icon, size = size * 0.52f)
    }
}

/** 그림 문자를 쓰는 둥근 버튼 */
@Composable
fun RoundIconButton(
    icon: String,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 60.dp,
    background: Color = Color.White,
    shadow: Color = KidsColors.PaperShadow,
    enabled: Boolean = true,
) {
    val services = LocalServices.current
    ChunkyBox(
        modifier = modifier
            .size(size)
            .graphicsLayer { alpha = if (enabled) 1f else 0.35f }
            .semantics {
                this.contentDescription = contentDescription
                role = Role.Button
            },
        color = background,
        shadow = shadow,
        radius = size * 0.34f,
        depth = 5.dp,
        enabled = enabled,
        onClick = {
            services.sound.play(Sfx.TAP)
            onClick()
        },
    ) {
        Text(text = icon, fontSize = (size.value * 0.4f).sp, modifier = Modifier.clearAndSetSemantics { })
    }
}

/** 큼직한 입체 버튼 ("또 할래!") */
@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: String? = null,
    color: Color = KidsColors.Correct,
    shadow: Color = KidsColors.CorrectDeep,
    contentColor: Color = Color.White,
    fontSize: TextUnit = 24.sp,
    enabled: Boolean = true,
) {
    val services = LocalServices.current
    ChunkyBox(
        modifier = modifier
            .graphicsLayer { alpha = if (enabled) 1f else 0.4f }
            .semantics { role = Role.Button },
        color = color,
        shadow = shadow,
        radius = 24.dp,
        enabled = enabled,
        onClick = {
            services.sound.play(Sfx.TAP)
            onClick()
        },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (icon != null) {
                Text(icon, fontSize = fontSize, modifier = Modifier.clearAndSetSemantics { })
                Spacer(Modifier.width(10.dp))
            }
            Text(text, fontSize = fontSize, color = contentColor)
        }
    }
}

/** 놀이 진행을 동그라미로 보여 줍니다. 채워지는 순간 톡 커집니다. */
@Composable
fun ProgressDots(done: Int, total: Int, color: Color, modifier: Modifier = Modifier) {
    Row(
        modifier.semantics { contentDescription = "$total 중 $done" },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(total) { i ->
            val filled = i < done
            val scale by animateFloatAsState(
                targetValue = if (filled) 1f else 0.85f,
                animationSpec = spring(dampingRatio = 0.35f, stiffness = Spring.StiffnessMedium),
                label = "dot$i",
            )
            Box(
                Modifier
                    .size(20.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .background(if (filled) color else color.copy(alpha = 0.18f), CircleShape),
            )
        }
    }
}

/**
 * 모든 놀이 화면의 공통 틀: 평평한 종이 바탕, 집 버튼, 영역 색 제목 띠, 진행 동그라미, 다시 듣기 버튼.
 * [color]는 제목 띠 색(영역의 진한 색)입니다.
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
    background: Color = KidsColors.Paper,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier
            .fillMaxSize()
            .background(background),
    ) {
        Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RoundIconButton(icon = KidIcons.home(KidsColors.Ink), contentDescription = "처음 화면으로", onClick = onHome, size = 56.dp)
                Spacer(Modifier.width(14.dp))
                Box(
                    Modifier
                        .background(color, RoundedCornerShape(50))
                        .padding(horizontal = 22.dp, vertical = 8.dp),
                ) {
                    Text(title, fontSize = 24.sp, color = Color.White, maxLines = 1)
                }
                Spacer(Modifier.weight(1f))
                if (total > 0) ProgressDots(done = progress, total = total, color = color)
                if (onReplayVoice != null) {
                    Spacer(Modifier.width(14.dp))
                    RoundIconButton(icon = KidIcons.speaker(KidsColors.Ink), contentDescription = "다시 듣기", onClick = onReplayVoice, size = 56.dp)
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
            .background(Color.White, RoundedCornerShape(24.dp))
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

enum class AnswerState { Normal, Dimmed, Correct, Hint }

/**
 * 정답 선택 카드. 맞히면 커지며 초록 테두리, 틀린 선택지는 흐려지고 흔들립니다.
 * [AnswerState.Hint]는 두 번 틀렸을 때 정답 쪽을 노란 테두리로 살짝 알려 줍니다.
 */
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
        targetValue = if (state == AnswerState.Correct) 1.06f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMediumLow),
        label = "answerScale",
    )
    val alpha by animateFloatAsState(if (state == AnswerState.Dimmed) 0.35f else 1f, label = "answerAlpha")
    val hintPulse = rememberPulse(0.97f, 1.04f, 700)
    val shape = RoundedCornerShape(26.dp)
    val border = when (state) {
        AnswerState.Correct -> KidsColors.Correct
        AnswerState.Hint -> KidsColors.Hint
        else -> null
    }
    ChunkyBox(
        modifier = modifier
            .shake(shakeState)
            .graphicsLayer {
                val pulse = if (state == AnswerState.Hint) hintPulse.value else 1f
                scaleX = scale * pulse
                scaleY = scale * pulse
                this.alpha = alpha
            }
            .then(
                if (contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else {
                    Modifier
                },
            ),
        color = if (state == AnswerState.Correct) KidsColors.CorrectSoft else Color.White,
        shadow = if (state == AnswerState.Correct) Color(0xFFA8DDB5) else KidsColors.PaperShadow,
        enabled = state != AnswerState.Dimmed,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (border != null) Modifier.border(5.dp, border, shape) else Modifier)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            content = content,
        )
    }
}
