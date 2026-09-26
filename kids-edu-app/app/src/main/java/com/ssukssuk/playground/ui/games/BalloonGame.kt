package com.ssukssuk.playground.ui.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.content.KidColor
import com.ssukssuk.playground.content.Korean
import com.ssukssuk.playground.content.Palette
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.ui.components.GameScaffold
import com.ssukssuk.playground.ui.components.drawCloud
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

private class Balloon(
    val color: KidColor,
    val x: Float,
    var y: Float,
    val speed: Float,
    val phase: Float,
    val scale: Float,
) {
    var popped = false
    var wobbleStart = -10f
}

private class Pop(val center: Offset, val color: Color, val start: Float)

/** 풍선 놀이의 움직임 상태. 매 프레임 갱신되므로 Compose 상태가 아닌 일반 객체로 둡니다. */
private class BalloonField(private val random: Random) {
    val balloons = ArrayList<Balloon>()
    val pops = ArrayList<Pop>()
    var time = 0f
    private var nextSpawn = 0.2f
    var size = Size.Zero

    fun balloonWidth(): Float = min(size.width * 0.13f, size.height * 0.3f)

    fun centerOf(b: Balloon): Offset {
        val sway = sin(time * 1.4f + b.phase) * size.width * 0.012f
        return Offset(b.x * size.width + sway, b.y * size.height)
    }

    fun update(dt: Float, target: KidColor?, colors: List<KidColor>, speed: Float, spawning: Boolean) {
        time += dt
        balloons.forEach { if (!it.popped) it.y -= it.speed * dt }
        balloons.removeAll { it.popped || it.y < -0.3f }
        pops.removeAll { time - it.start > 0.7f }
        val floating = balloons.size
        if (spawning && time >= nextSpawn && floating < 7 && target != null) {
            spawn(target, colors, speed)
            nextSpawn = time + 0.7f + random.nextFloat() * 0.6f
        }
    }

    private fun spawn(target: KidColor, colors: List<KidColor>, speed: Float) {
        val others = colors.filter { it != target }
        val targetsOnScreen = balloons.count { it.color == target }
        val color = if (others.isEmpty() || targetsOnScreen == 0 || random.nextFloat() < 0.45f) target else others.random(random)
        // 방금 올라온 풍선과 겹치지 않는 자리를 몇 번 찾아봅니다.
        var x = 0.5f
        for (attempt in 0 until 6) {
            x = 0.1f + random.nextFloat() * 0.8f
            if (balloons.none { it.y > 0.75f && abs(it.x - x) < 0.14f }) break
        }
        balloons += Balloon(
            color = color,
            x = x,
            y = 1.2f,
            speed = speed * (0.85f + random.nextFloat() * 0.35f),
            phase = random.nextFloat() * 6.28f,
            scale = 0.9f + random.nextFloat() * 0.25f,
        )
    }

    /** 위에 그려진 풍선부터 확인합니다. */
    fun hit(position: Offset): Balloon? {
        val w = balloonWidth()
        return balloons.asReversed().firstOrNull { b ->
            if (b.popped) return@firstOrNull false
            val c = centerOf(b)
            val rx = w * b.scale * 0.62f
            val ry = w * b.scale * 1.2f * 0.62f
            val dx = (position.x - c.x) / rx
            val dy = (position.y - c.y) / ry
            dx * dx + dy * dy <= 1f
        }
    }
}

/** 색깔 풍선: 말하는 색의 풍선만 골라 터뜨립니다. 다른 색을 누르면 그 색 이름을 알려 줍니다. */
@Composable
fun BalloonGame(env: GameEnv) {
    val difficulty = env.difficulty
    val colors = remember { Palette.balloonColors.take(difficulty.balloonColorCount) }
    val targets = remember { colors.shuffled(env.random).take(difficulty.balloonRounds) }
    val needed = difficulty.balloonTargetsPerColor
    val field = remember { BalloonField(env.random) }
    var round by remember { mutableIntStateOf(0) }
    var popped by remember { mutableIntStateOf(0) }
    var correctTaps by remember { mutableIntStateOf(0) }
    var wrongTaps by remember { mutableIntStateOf(0) }
    var roundDone by remember { mutableStateOf(false) }
    var frame by remember { mutableLongStateOf(0L) }
    val scope = rememberCoroutineScope()
    val target = targets[round]

    fun prompt() = env.say("${target.adjective} 풍선을 찾아서 톡 터뜨려 볼까요?")

    LaunchedEffect(round) {
        delay(if (round == 0) 800 else 200)
        prompt()
    }

    LaunchedEffect(field) {
        var last = withFrameNanos { it }
        while (isActive) {
            withFrameNanos { now ->
                val dt = ((now - last) / 1_000_000_000f).coerceIn(0f, 0.05f)
                last = now
                field.update(dt, targets[round], colors, difficulty.balloonSpeed, spawning = !roundDone)
                frame = now
            }
        }
    }

    // pointerInput 안에서 호출되므로 현재 라운드의 목표 색을 상태에서 다시 읽습니다.
    fun tap(position: Offset) {
        if (roundDone) return
        val target = targets[round]
        val balloon = field.hit(position) ?: return
        if (balloon.color == target) {
            balloon.popped = true
            field.pops += Pop(field.centerOf(balloon), Color(balloon.color.argb), field.time)
            popped++
            correctTaps++
            env.play(Sfx.POP)
            if (popped >= needed) {
                roundDone = true
                env.play(Sfx.CORRECT)
                env.say("와! ${target.adjective} 풍선을 ${Korean.counterNumber(needed)} 개 다 터뜨렸어요!")
                scope.launch {
                    delay(2400)
                    if (round + 1 < targets.size) {
                        round++
                        popped = 0
                        roundDone = false
                    } else {
                        env.complete(correctTaps, correctTaps + wrongTaps)
                    }
                }
            } else {
                env.say("${Korean.countWord(popped)}!")
            }
        } else {
            wrongTaps++
            balloon.wobbleStart = field.time
            env.play(Sfx.WRONG)
            env.say("이건 ${balloon.color.name} 풍선이에요. ${target.adjective} 풍선을 찾아봐요!")
        }
    }

    GameScaffold(
        title = env.game.title,
        color = Color(env.game.colorArgb),
        onHome = env.onHome,
        progress = round + if (roundDone) 1 else 0,
        total = targets.size,
        onReplayVoice = ::prompt,
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onPress = { position -> tap(position) })
                },
        ) {
            field.size = size
            // 매 프레임 바뀌는 frame 값을 읽어 다시 그리도록 합니다.
            if (frame < 0L) return@Canvas
            drawRect(Brush.verticalGradient(listOf(Color(0xFFBDE5FF), Color(0xFFFFF4E0))))
            drawCloud(Offset(size.width * 0.2f, size.height * 0.2f), size.minDimension * 0.08f, Color.White.copy(alpha = 0.8f))
            drawCloud(Offset(size.width * 0.78f, size.height * 0.32f), size.minDimension * 0.06f, Color.White.copy(alpha = 0.7f))
            val w = field.balloonWidth()
            field.balloons.forEach { b -> drawBalloon(field.centerOf(b), w * b.scale, Color(b.color.argb), field.time, b) }
            field.pops.forEach { p -> drawPop(p, (field.time - p.start) / 0.7f, w) }
        }
        TargetBanner(target = target, popped = popped, needed = needed, modifier = Modifier.align(Alignment.TopCenter))
    }
}

@Composable
private fun TargetBanner(target: KidColor, popped: Int, needed: Int, modifier: Modifier) {
    Row(
        modifier = modifier
            .padding(top = 8.dp)
            .shadow(6.dp, RoundedCornerShape(50))
            .background(Color.White, RoundedCornerShape(50))
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(width = 26.dp, height = 32.dp)
                .background(Color(target.argb), RoundedCornerShape(50)),
        )
        Spacer(Modifier.width(10.dp))
        Text("${target.adjective} 풍선을 터뜨려요!", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = KidsColors.Ink)
        Spacer(Modifier.width(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(needed) { i ->
                Box(
                    Modifier
                        .size(16.dp)
                        .background(if (i < popped) Color(target.argb) else Color(0xFFE0E0E0), CircleShape),
                )
            }
        }
    }
}

private fun DrawScope.drawBalloon(center: Offset, width: Float, color: Color, time: Float, balloon: Balloon) {
    val height = width * 1.2f
    val bottom = Offset(center.x, center.y + height / 2f)
    val sinceWobble = time - balloon.wobbleStart
    val wobble = if (sinceWobble in 0f..0.5f) sin(sinceWobble * 40f) * 14f * (1f - sinceWobble / 0.5f) else 0f
    rotate(wobble, pivot = bottom) {
        // 줄
        val sway = sin(time * 2f + balloon.phase) * width * 0.12f
        val string = Path().apply {
            moveTo(bottom.x, bottom.y + width * 0.08f)
            cubicTo(
                bottom.x + sway, bottom.y + height * 0.25f,
                bottom.x - sway, bottom.y + height * 0.45f,
                bottom.x + sway * 0.5f, bottom.y + height * 0.65f,
            )
        }
        drawPath(string, Color(0xFF9E9E9E), style = Stroke(width = width * 0.025f))
        // 매듭
        val knot = Path().apply {
            moveTo(bottom.x, bottom.y - width * 0.02f)
            lineTo(bottom.x - width * 0.07f, bottom.y + width * 0.09f)
            lineTo(bottom.x + width * 0.07f, bottom.y + width * 0.09f)
            close()
        }
        drawPath(knot, color)
        // 풍선
        drawOval(color, topLeft = Offset(center.x - width / 2f, center.y - height / 2f), size = Size(width, height))
        drawOval(
            Color.White.copy(alpha = 0.45f),
            topLeft = Offset(center.x - width * 0.3f, center.y - height * 0.36f),
            size = Size(width * 0.18f, height * 0.26f),
        )
    }
}

private fun DrawScope.drawPop(pop: Pop, progress: Float, unit: Float) {
    if (progress !in 0f..1f) return
    val alpha = 1f - progress
    val distance = unit * (0.3f + progress * 0.9f)
    repeat(10) { i ->
        val angle = i * 2f * PI.toFloat() / 10f
        val pos = pop.center + Offset(cos(angle), sin(angle)) * distance
        drawCircle(
            color = (if (i % 2 == 0) pop.color else KidsColors.Sun).copy(alpha = alpha),
            radius = unit * 0.07f * (1f - progress * 0.5f),
            center = pos,
        )
    }
    drawCircle(
        color = pop.color.copy(alpha = alpha * 0.5f),
        radius = unit * 0.6f * progress,
        center = pop.center,
        style = Stroke(width = unit * 0.05f),
    )
}
