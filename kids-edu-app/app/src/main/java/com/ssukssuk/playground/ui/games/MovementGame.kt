package com.ssukssuk.playground.ui.games

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.ui.components.GameScaffold
import com.ssukssuk.playground.ui.components.Mascot
import com.ssukssuk.playground.ui.components.MascotAction
import com.ssukssuk.playground.ui.components.MascotCanvas
import com.ssukssuk.playground.ui.components.MascotMood
import com.ssukssuk.playground.ui.components.MascotPose
import com.ssukssuk.playground.ui.components.PillButton
import com.ssukssuk.playground.ui.components.SpeechBubble
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

private class Move(
    val title: String,
    val icon: String,
    val instruction: String,
    val seconds: Float,
    val pose: (Float) -> MascotPose,
)

private const val TWO_PI = (2 * PI).toFloat()

private val allMoves = listOf(
    Move("만세!", "🙌", "두 팔을 하늘 높이 쭉 뻗어요. 만세!", 7f) { t ->
        val s = sin(t * TWO_PI * 0.5f)
        MascotPose(leftArm = 165f + s * 10f, rightArm = 165f + s * 10f, squash = -0.06f - abs(s) * 0.03f, leafSway = s * 10f)
    },
    Move("박수 짝짝", "👏", "박수를 짝짝짝 쳐요!", 7f) { t ->
        val c = abs(sin(t * TWO_PI))
        MascotPose(leftArm = 110f + c * 62f, rightArm = 110f + c * 62f, squash = c * 0.03f, leafSway = c * 8f)
    },
    Move("콩콩 뛰기", "🐰", "토끼처럼 제자리에서 콩콩 뛰어요!", 7f) { t ->
        val h = abs(sin(t * TWO_PI * 0.8f))
        MascotPose(
            leftArm = 40f + h * 50f,
            rightArm = 40f + h * 50f,
            jump = h * 0.5f,
            squash = max(0f, 0.25f - h) * 0.4f,
            leafSway = sin(t * TWO_PI * 0.8f) * 12f,
        )
    },
    Move("한 발 서기", "⚖️", "한 발로 서서 균형을 잡아요. 흔들흔들, 넘어지지 않게!", 8f) { t ->
        MascotPose(
            leftArm = 95f + sin(t * 3f) * 8f,
            rightArm = 95f - sin(t * 3f) * 8f,
            tilt = sin(t * 2.2f) * 6f,
            leftFootLift = 1f,
            leafSway = sin(t * 2.2f) * 10f,
        )
    },
    Move("옆으로 쭉", "🌈", "팔을 올리고 몸을 옆으로 쭉~ 기울여요. 이쪽, 저쪽!", 8f) { t ->
        val s = sin(t * TWO_PI * 0.25f)
        MascotPose(
            tilt = s * 16f,
            leftArm = 30f + 140f * max(0f, s),
            rightArm = 30f + 140f * max(0f, -s),
            leafSway = s * 14f,
        )
    },
    Move("빙글빙글", "🌀", "제자리에서 빙글 한 바퀴 돌아요!", 7f) { t ->
        MascotPose(leftArm = 80f, rightArm = 80f, turn = cos(t * TWO_PI * 0.5f), leafSway = sin(t * TWO_PI) * 10f)
    },
    Move("숨쉬기", "🌬️", "코로 숨을 크게 들이마시고, 입으로 후~ 내쉬어요.", 10f) { t ->
        val s = sin(t * TWO_PI / 5f)
        MascotPose(leftArm = 30f + max(0f, s) * 50f, rightArm = 30f + max(0f, s) * 50f, squash = -s * 0.07f, leafSway = s * 6f)
    },
)

/**
 * 쑥쑥 체조: 쑥쑥이의 동작을 보고 따라 하며 몸을 움직입니다.
 * 화면을 보는 시간 사이에 신체 활동을 넣고, 마지막은 숨쉬기로 차분하게 마무리합니다.
 */
@Composable
fun MovementGame(env: GameEnv) {
    val moves = remember {
        if (env.difficulty.isYounger) {
            listOf(allMoves[0], allMoves[1], allMoves[2], allMoves[5], allMoves[6])
        } else {
            allMoves
        }
    }
    var started by remember { mutableStateOf(false) }
    var index by remember { mutableIntStateOf(0) }
    var elapsed by remember { mutableFloatStateOf(0f) }
    var cheering by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(800)
        env.say("쑥쑥이랑 같이 몸을 움직여요! 먼저 주변에 부딪힐 물건이 없는지 살펴봐요. 준비되면 시작을 눌러요!")
    }

    LaunchedEffect(started, index) {
        if (!started) return@LaunchedEffect
        val move = moves[index]
        cheering = false
        elapsed = 0f
        env.say("${move.title} ${move.instruction}")
        var last = withFrameNanos { it }
        while (isActive && elapsed < move.seconds) {
            withFrameNanos { now ->
                // 앱이 가려지면 프레임이 멈추므로 시간도 함께 멈춥니다.
                elapsed += ((now - last) / 1_000_000_000f).coerceIn(0f, 0.1f)
                last = now
            }
        }
        cheering = true
        env.play(Sfx.STAR)
        env.say(env.praise())
        delay(1600)
        if (index + 1 < moves.size) index++ else env.complete()
    }

    GameScaffold(
        title = env.game.title,
        color = Color(env.game.colorArgb),
        onHome = env.onHome,
        progress = when {
            !started -> 0
            cheering -> index + 1
            else -> index
        },
        total = moves.size,
        onReplayVoice = if (started) {
            { env.say(moves[index].instruction) }
        } else {
            null
        },
    ) {
        if (!started) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Mascot(Modifier.size(width = 200.dp, height = 240.dp), action = MascotAction.WAVE)
                Spacer(Modifier.size(24.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SpeechBubble(
                        text = "쑥쑥이랑 같이 몸을 움직여요!\n주변에 부딪힐 물건이 없는지 살펴봐요.",
                        fontSize = 20.sp,
                    )
                    Spacer(Modifier.height(20.dp))
                    PillButton(text = "시작!", icon = "🤸", onClick = { started = true })
                }
            }
        } else {
            val move = moves[index]
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MascotCanvas(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    mood = if (cheering) MascotMood.EXCITED else MascotMood.HAPPY,
                    pose = {
                        if (cheering) MascotPose(leftArm = 160f, rightArm = 160f, jump = 0.15f) else move.pose(elapsed)
                    },
                )
                Column(
                    Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AnimatedContent(
                        targetState = move,
                        transitionSpec = { (slideInHorizontally { it / 2 } + fadeIn()) togetherWith fadeOut() },
                        label = "moveTitle",
                    ) { m ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${m.icon} ${m.title}", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = KidsColors.Ink)
                            Text(
                                text = m.instruction,
                                fontSize = 19.sp,
                                color = KidsColors.InkSoft,
                                textAlign = TextAlign.Center,
                                lineHeight = 26.sp,
                            )
                        }
                    }
                    // 링은 그리기 단계에서, 남은 초는 정수가 바뀔 때만 다시 구성합니다.
                    val secondsLeft by remember(move) {
                        derivedStateOf { (move.seconds - elapsed).coerceAtLeast(0f).toInt() + 1 }
                    }
                    CountdownRing(
                        progress = { (elapsed / move.seconds).coerceIn(0f, 1f) },
                        secondsLeft = secondsLeft,
                        done = cheering,
                    )
                    if (!cheering) {
                        PillButton(
                            text = "다음 동작",
                            icon = "▶",
                            fontSize = 17.sp,
                            color = Color(0xFF42A5F5),
                            onClick = { elapsed = move.seconds },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CountdownRing(progress: () -> Float, secondsLeft: Int, done: Boolean) {
    Box(Modifier.size(96.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 10.dp.toPx()
            val inset = stroke / 2f
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(Color(0x22000000), 0f, 360f, false, topLeft = Offset(inset, inset), size = arcSize, style = Stroke(stroke))
            drawArc(
                KidsColors.Leaf,
                -90f,
                360f * progress(),
                false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(stroke, cap = StrokeCap.Round),
            )
        }
        Text(
            text = if (done) "⭐" else "$secondsLeft",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = KidsColors.Ink,
        )
    }
}
