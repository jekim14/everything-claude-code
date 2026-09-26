package com.ssukssuk.playground.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.core.Reward
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

private class ConfettiPiece(
    val x: Float,
    val startY: Float,
    val speed: Float,
    val swayFreq: Float,
    val phase: Float,
    val spin: Float,
    val w: Float,
    val h: Float,
    val color: Color,
)

private val confettiColors = listOf(
    Color(0xFFFF6F61), Color(0xFFFFD54F), Color(0xFF4FC3F7),
    Color(0xFF81C784), Color(0xFFBA68C8), Color(0xFFFF8A65),
)

/** 알록달록 색종이가 흩날리는 효과 */
@Composable
fun ConfettiRain(modifier: Modifier = Modifier, count: Int = 90) {
    val pieces = remember {
        val random = Random(System.nanoTime())
        List(count) {
            ConfettiPiece(
                x = random.nextFloat(),
                startY = -random.nextFloat() * 1.2f,
                speed = 0.18f + random.nextFloat() * 0.22f,
                swayFreq = 1f + random.nextFloat() * 2f,
                phase = random.nextFloat() * 6.28f,
                spin = 90f + random.nextFloat() * 270f,
                w = 6f + random.nextFloat() * 6f,
                h = 10f + random.nextFloat() * 8f,
                color = confettiColors[random.nextInt(confettiColors.size)],
            )
        }
    }
    var time by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) {
            withFrameNanos { now -> time = (now - start) / 1_000_000_000f }
        }
    }
    Canvas(modifier.fillMaxSize()) {
        val now = time
        pieces.forEach { p ->
            val progress = p.startY + p.speed * now
            val y = ((progress % 1.25f) - 0.1f) * size.height
            val x = (p.x + sin(now * p.swayFreq + p.phase) * 0.03f) * size.width
            val pos = Offset(x, y)
            rotate(p.spin * now + p.phase * 57f, pivot = pos) {
                drawRect(
                    color = p.color,
                    topLeft = Offset(x - p.w * density / 2f, y - p.h * density / 2f),
                    size = Size(p.w * density, p.h * density),
                )
            }
        }
    }
}

/**
 * 놀이를 마쳤을 때의 축하 화면: 색종이, 만세하는 쑥쑥이, 새 스티커.
 * 점수 대신 '끝까지 해낸 것'을 칭찬합니다.
 */
@Composable
fun CelebrationOverlay(
    reward: Reward,
    onAgain: () -> Unit,
    onHome: () -> Unit,
) {
    val services = LocalServices.current
    val cardScale = remember { Animatable(0.4f) }
    val stickerScale = remember { Animatable(0f) }
    val stickerSpin = remember { Animatable(-30f) }
    LaunchedEffect(reward) {
        services.sound.play(Sfx.CHEER)
        val name = reward.sticker.name
        services.speaker.speak(
            if (reward.isNew) "참 잘했어요! 새 스티커, $name 스티커를 받았어요!" else "참 잘했어요! $name 스티커를 하나 더 받았어요!",
        )
        launch { cardScale.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow)) }
        delay(350)
        services.sound.play(Sfx.STAR)
        launch { stickerSpin.animateTo(0f, spring(dampingRatio = 0.3f, stiffness = Spring.StiffnessLow)) }
        stickerScale.animateTo(1f, spring(dampingRatio = 0.35f, stiffness = Spring.StiffnessLow))
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            // 뒤쪽 놀이 화면이 눌리지 않도록 터치를 막습니다.
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { },
        contentAlignment = Alignment.Center,
    ) {
        ConfettiRain()
        Row(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = cardScale.value
                    scaleY = cardScale.value
                }
                .shadow(16.dp, RoundedCornerShape(36.dp))
                .background(KidsColors.Cream, RoundedCornerShape(36.dp))
                .padding(horizontal = 28.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Mascot(
                modifier = Modifier.size(width = 150.dp, height = 190.dp),
                mood = MascotMood.EXCITED,
                action = MascotAction.CHEER,
            )
            Spacer(Modifier.width(20.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("참 잘했어요!", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = KidsColors.Accent)
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .size(104.dp)
                        .graphicsLayer {
                            scaleX = stickerScale.value
                            scaleY = stickerScale.value
                            rotationZ = stickerSpin.value
                        }
                        .shadow(8.dp, CircleShape)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(reward.sticker.emoji, fontSize = 56.sp)
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (reward.isNew) "새 스티커: ${reward.sticker.name}" else "${reward.sticker.name} 스티커",
                    fontSize = 20.sp,
                    color = KidsColors.InkSoft,
                )
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PillButton(text = "한 번 더", icon = "🔁", onClick = onAgain, color = KidsColors.Leaf)
                    PillButton(text = "처음으로", icon = "🏠", onClick = onHome, color = Color(0xFF42A5F5))
                }
            }
        }
    }
}

/** 화면 가운데 잠깐 떠오르는 큰 칭찬 글자. [trigger]가 바뀔 때마다 다시 나타납니다. */
@Composable
fun PraisePop(text: String?, trigger: Int, modifier: Modifier = Modifier) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(trigger) {
        if (text == null || trigger == 0) return@LaunchedEffect
        scale.snapTo(0.3f)
        alpha.snapTo(1f)
        launch { scale.animateTo(1f, spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMediumLow)) }
        delay(900)
        alpha.animateTo(0f, tween(300))
    }
    if (text != null) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 46.sp,
                fontWeight = FontWeight.ExtraBold,
                color = KidsColors.Accent,
                modifier = Modifier.graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    this.alpha = alpha.value
                },
            )
        }
    }
}
