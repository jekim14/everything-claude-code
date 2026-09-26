package com.ssukssuk.playground.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

enum class MascotMood { HAPPY, EXCITED, SLEEPY, SURPRISED, THINKING }

enum class MascotAction { IDLE, WAVE, CHEER, SLEEP, NOD }

/**
 * 쑥쑥이의 자세.
 * 팔 각도는 아래로 내린 상태가 0도, 옆으로 벌리면 90도, 머리 위로 올리면 180도입니다.
 */
data class MascotPose(
    val leftArm: Float = 25f,
    val rightArm: Float = 25f,
    /** 몸 기울기(도) */
    val tilt: Float = 0f,
    /** 점프 높이 (몸 반지름 대비 0..1) */
    val jump: Float = 0f,
    val leftFootLift: Float = 0f,
    val rightFootLift: Float = 0f,
    /** 양수는 납작하게, 음수는 길쭉하게 */
    val squash: Float = 0f,
    /** 좌우 배율. 빙글 도는 모습을 흉내 낼 때 -1..1 사이로 바꿉니다. */
    val turn: Float = 1f,
    /** 새싹 잎 흔들림(도) */
    val leafSway: Float = 0f,
)

/** 스스로 움직이는 쑥쑥이. [action]에 따라 숨쉬기·손 흔들기·만세·잠자기를 합니다. */
@Composable
fun Mascot(
    modifier: Modifier = Modifier,
    mood: MascotMood = MascotMood.HAPPY,
    action: MascotAction = MascotAction.IDLE,
) {
    val transition = rememberInfiniteTransition(label = "mascot")
    val cycle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing)),
        label = "mascotCycle",
    )
    val blink = remember { Animatable(0f) }
    LaunchedEffect(mood) {
        if (mood == MascotMood.SLEEPY) return@LaunchedEffect
        while (true) {
            delay(Random.nextLong(1800, 4200))
            blink.animateTo(1f, tween(70))
            blink.animateTo(0f, tween(110))
        }
    }
    MascotCanvas(
        modifier = modifier,
        mood = mood,
        pose = { poseForAction(action, cycle) },
        blink = { blink.value },
    )
}

/** 자세를 직접 지정해 그리는 쑥쑥이 (체조 놀이에서 사용) */
@Composable
fun MascotCanvas(
    modifier: Modifier = Modifier,
    mood: MascotMood = MascotMood.HAPPY,
    pose: () -> MascotPose,
    blink: () -> Float = { 0f },
) {
    Canvas(modifier.semantics { contentDescription = "새싹 친구 쑥쑥이" }) {
        drawMascot(pose(), mood, blink())
    }
}

fun poseForAction(action: MascotAction, cycle: Float): MascotPose {
    val wave = sin(cycle * 2f * PI.toFloat())
    return when (action) {
        MascotAction.IDLE -> MascotPose(
            leftArm = 22f + wave * 4f,
            rightArm = 22f - wave * 4f,
            squash = wave * 0.025f,
            leafSway = wave * 7f,
        )
        MascotAction.WAVE -> MascotPose(
            leftArm = 22f,
            rightArm = 145f + sin(cycle * 6f * PI.toFloat()) * 22f,
            tilt = wave * 3f,
            squash = wave * 0.02f,
            leafSway = wave * 8f,
        )
        MascotAction.CHEER -> {
            val hop = abs(sin(cycle * 4f * PI.toFloat()))
            MascotPose(
                leftArm = 150f + hop * 15f,
                rightArm = 150f + hop * 15f,
                jump = hop * 0.35f,
                squash = (0.3f - hop).coerceAtLeast(0f) * 0.25f,
                leafSway = wave * 12f,
            )
        }
        MascotAction.SLEEP -> MascotPose(
            leftArm = 12f,
            rightArm = 12f,
            tilt = 8f,
            squash = 0.03f + wave * 0.025f,
            leafSway = -10f + wave * 3f,
        )
        MascotAction.NOD -> MascotPose(
            tilt = sin(cycle * 4f * PI.toFloat()) * 6f,
            squash = wave * 0.02f,
            leafSway = wave * 6f,
        )
    }
}

private val BodyColor = KidsColors.Sprout
private val BodyEdge = Color(0xFF6FBF52)
private val LimbColor = Color(0xFF86D466)
private val FootColor = Color(0xFF6CC553)
private val MouthColor = Color(0xFFB23A48)
private val TongueColor = Color(0xFFFF8A9A)

fun DrawScope.drawMascot(pose: MascotPose, mood: MascotMood, blink: Float) {
    val w = size.width
    val h = size.height
    val r = min(w * 0.3f, h * 0.31f)
    val groundY = h * 0.97f
    val cx = w / 2f
    val cy = groundY - r * 1.15f
    val lift = pose.jump * r

    withTransform({
        translate(top = -lift)
        rotate(pose.tilt, pivot = Offset(cx, cy + r * 0.5f))
        scale(
            scaleX = pose.turn * (1f + pose.squash),
            scaleY = 1f - pose.squash,
            pivot = Offset(cx, groundY),
        )
    }) {
        // 새싹 잎 (몸 뒤에 그려 잎자루가 가려지도록)
        val leafBase = Offset(cx, cy - r * 0.9f)
        drawLeaf(leafBase, r * 0.8f, -36f + pose.leafSway, KidsColors.Leaf)
        drawLeaf(leafBase, r * 0.8f, 36f + pose.leafSway, KidsColors.LeafLight)

        // 팔
        drawArm(Offset(cx - r * 0.84f, cy + r * 0.12f), r, pose.leftArm, isLeft = true)
        drawArm(Offset(cx + r * 0.84f, cy + r * 0.12f), r, pose.rightArm, isLeft = false)

        // 발
        val footSize = Size(r * 0.52f, r * 0.28f)
        drawOval(
            FootColor,
            topLeft = Offset(cx - r * 0.44f - footSize.width / 2f, groundY - footSize.height - pose.leftFootLift * r * 0.55f),
            size = footSize,
        )
        drawOval(
            FootColor,
            topLeft = Offset(cx + r * 0.44f - footSize.width / 2f, groundY - footSize.height - pose.rightFootLift * r * 0.55f),
            size = footSize,
        )

        // 몸
        drawCircle(BodyColor, radius = r, center = Offset(cx, cy))
        drawCircle(BodyEdge, radius = r, center = Offset(cx, cy), style = Stroke(width = r * 0.045f))
        drawOval(
            Color.White.copy(alpha = 0.35f),
            topLeft = Offset(cx - r * 0.62f, cy - r * 0.66f),
            size = Size(r * 0.5f, r * 0.3f),
        )

        drawFace(cx, cy, r, mood, blink)
    }
}

private fun DrawScope.drawLeaf(base: Offset, length: Float, angle: Float, color: Color) {
    rotate(angle, pivot = base) {
        val path = Path().apply {
            moveTo(base.x, base.y)
            cubicTo(base.x - length * 0.46f, base.y - length * 0.15f, base.x - length * 0.42f, base.y - length * 0.82f, base.x, base.y - length)
            cubicTo(base.x + length * 0.42f, base.y - length * 0.82f, base.x + length * 0.46f, base.y - length * 0.15f, base.x, base.y)
            close()
        }
        drawPath(path, color)
        drawLine(
            color = Color.White.copy(alpha = 0.45f),
            start = Offset(base.x, base.y - length * 0.12f),
            end = Offset(base.x, base.y - length * 0.78f),
            strokeWidth = length * 0.05f,
            cap = StrokeCap.Round,
        )
    }
}

private fun DrawScope.drawArm(shoulder: Offset, r: Float, angleDeg: Float, isLeft: Boolean) {
    val rad = angleDeg * PI.toFloat() / 180f
    val dirX = sin(rad) * if (isLeft) -1f else 1f
    val dirY = cos(rad)
    val length = r * 0.62f
    val hand = Offset(shoulder.x + dirX * length, shoulder.y + dirY * length)
    drawLine(LimbColor, start = shoulder, end = hand, strokeWidth = r * 0.2f, cap = StrokeCap.Round)
    drawCircle(LimbColor, radius = r * 0.14f, center = hand)
}

private fun DrawScope.drawFace(cx: Float, cy: Float, r: Float, mood: MascotMood, blink: Float) {
    val ink = KidsColors.Ink
    val eyeY = cy - r * 0.05f
    val eyeDx = r * 0.36f
    val eyeW = r * 0.17f
    val eyeH = r * 0.24f
    val lineWidth = r * 0.07f

    // 볼
    listOf(-1f, 1f).forEach { side ->
        drawOval(
            KidsColors.Cheek.copy(alpha = 0.7f),
            topLeft = Offset(cx + side * r * 0.58f - r * 0.13f, cy + r * 0.16f),
            size = Size(r * 0.26f, r * 0.15f),
        )
    }

    // 눈
    listOf(-1f, 1f).forEach { side ->
        val ex = cx + side * eyeDx
        when (mood) {
            MascotMood.SLEEPY -> drawArc(
                color = ink,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(ex - eyeW * 0.8f, eyeY - eyeH * 0.3f),
                size = Size(eyeW * 1.6f, eyeH * 0.6f),
                style = Stroke(width = lineWidth, cap = StrokeCap.Round),
            )
            MascotMood.EXCITED -> drawArc(
                color = ink,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(ex - eyeW * 0.8f, eyeY - eyeH * 0.2f),
                size = Size(eyeW * 1.6f, eyeH * 0.7f),
                style = Stroke(width = lineWidth, cap = StrokeCap.Round),
            )
            else -> {
                val scale = if (mood == MascotMood.SURPRISED) 1.25f else 1f
                val lookUp = if (mood == MascotMood.THINKING) -r * 0.06f else 0f
                val height = eyeH * scale * (1f - blink * 0.9f)
                drawOval(
                    ink,
                    topLeft = Offset(ex - eyeW * scale / 2f, eyeY + lookUp - height / 2f),
                    size = Size(eyeW * scale, height),
                )
                if (blink < 0.5f) {
                    drawCircle(
                        Color.White,
                        radius = eyeW * 0.22f * scale,
                        center = Offset(ex + eyeW * 0.14f, eyeY + lookUp - eyeH * 0.18f * scale),
                    )
                }
            }
        }
    }

    // 입
    val mouthY = cy + r * 0.2f
    when (mood) {
        MascotMood.HAPPY -> drawArc(
            color = ink,
            startAngle = 20f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(cx - r * 0.19f, mouthY - r * 0.12f),
            size = Size(r * 0.38f, r * 0.26f),
            style = Stroke(width = lineWidth, cap = StrokeCap.Round),
        )
        MascotMood.EXCITED -> {
            val topLeft = Offset(cx - r * 0.2f, mouthY - r * 0.1f)
            val mouthSize = Size(r * 0.4f, r * 0.36f)
            drawArc(MouthColor, 0f, 180f, useCenter = true, topLeft = topLeft, size = mouthSize)
            drawArc(
                TongueColor,
                20f,
                140f,
                useCenter = true,
                topLeft = Offset(cx - r * 0.11f, mouthY + r * 0.02f),
                size = Size(r * 0.22f, r * 0.2f),
            )
        }
        MascotMood.SLEEPY -> drawOval(
            ink,
            topLeft = Offset(cx - r * 0.06f, mouthY),
            size = Size(r * 0.12f, r * 0.1f),
        )
        MascotMood.SURPRISED -> drawOval(
            ink,
            topLeft = Offset(cx - r * 0.09f, mouthY - r * 0.02f),
            size = Size(r * 0.18f, r * 0.24f),
        )
        MascotMood.THINKING -> drawLine(
            ink,
            start = Offset(cx - r * 0.1f, mouthY + r * 0.06f),
            end = Offset(cx + r * 0.12f, mouthY + r * 0.02f),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round,
        )
    }
}
