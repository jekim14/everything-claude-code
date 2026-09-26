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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.content.Lines
import com.ssukssuk.playground.content.Sticker
import com.ssukssuk.playground.core.Game
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.ui.theme.KidsColors
import com.ssukssuk.playground.ui.theme.ParentTextStyle
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

/** 한 판을 마친 결과: 큰 칭찬 글, 깜짝 선물 후보(없으면 빈 목록), 보호자 대화 카드 */
data class RoundResult(
    val game: Game,
    val headline: String,
    val giftChoices: List<Sticker>,
    val talkCard: String?,
)

/**
 * 놀이를 마쳤을 때의 축하 화면.
 *
 * 점수 대신 아이가 한 과정(끝까지, 다시 생각해서)을 칭찬하고, 그날 처음 마친 놀이면 선물 상자 세 개 중
 * 하나를 스스로 고르게 합니다. 옆에는 보호자가 아이와 이어서 이야기할 거리를 보여 줍니다.
 */
@Composable
fun CelebrationOverlay(
    result: RoundResult,
    childName: String,
    onClaim: (Sticker) -> Unit,
    onAgain: () -> Unit,
    onHome: () -> Unit,
) {
    val services = LocalServices.current
    val cardScale = remember { Animatable(0.5f) }
    var picked by remember(result) { mutableStateOf<Sticker?>(null) }
    val reveal = remember(result) { Animatable(0f) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(result) {
        services.sound.play(Sfx.CHEER)
        val speech = Lines.celebration(childName, result.headline)
        services.speaker.speak(if (result.giftChoices.isEmpty()) speech else "$speech ${Lines.GIFT_PROMPT}")
        cardScale.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessLow))
    }
    fun pick(sticker: Sticker) {
        if (picked != null) return
        picked = sticker
        onClaim(sticker)
        services.sound.play(Sfx.STAR)
        services.speaker.speak(Lines.giftPicked(sticker))
        scope.launch { reveal.animateTo(1f, spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessLow)) }
    }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x8C2B2748))
            // 뒤쪽 놀이 화면이 눌리지 않도록 터치를 막습니다.
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        ConfettiRain(count = 50)
        val showTalk = result.talkCard != null
        val mainWidth = if (showTalk) minOf(560.dp, maxWidth * 0.62f) else minOf(560.dp, maxWidth * 0.9f)
        val talkWidth = minOf(300.dp, maxWidth * 0.34f)
        Row(
            modifier = Modifier.graphicsLayer {
                scaleX = cardScale.value
                scaleY = cardScale.value
            },
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChunkyBox(
                modifier = Modifier.width(mainWidth),
                shadow = KidsColors.InkSoft,
                radius = 36.dp,
                depth = 8.dp,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Mascot(
                        modifier = Modifier.size(width = 104.dp, height = 132.dp),
                        mood = MascotMood.EXCITED,
                        action = MascotAction.CHEER,
                    )
                    Spacer(Modifier.width(14.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text(result.headline, fontSize = 30.sp, color = KidsColors.Ink, textAlign = TextAlign.Center, lineHeight = 36.sp)
                        if (result.giftChoices.isNotEmpty()) {
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = picked?.let { "${it.name} 스티커!" } ?: "깜짝 선물! 하나 골라 볼까?",
                                fontSize = 19.sp,
                                color = KidsColors.InkSoft,
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                result.giftChoices.forEachIndexed { i, sticker ->
                                    GiftBox(
                                        index = i,
                                        sticker = sticker,
                                        picked = picked,
                                        reveal = { reveal.value },
                                        onPick = { pick(sticker) },
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            PillButton(text = "또 할래!", onClick = onAgain, fontSize = 21.sp)
                            PillButton(
                                text = "다른 놀이",
                                onClick = onHome,
                                color = Color(0xFFF1EEF8),
                                shadow = Color(0xFFCFC9E2),
                                contentColor = KidsColors.Ink,
                                fontSize = 21.sp,
                            )
                        }
                    }
                }
            }
            if (showTalk) TalkCard(text = result.talkCard!!, modifier = Modifier.width(talkWidth))
        }
    }
}

private val giftColors = listOf(
    Color(0xFFFFE4DA) to Color(0xFFC8472A),
    Color(0xFFD6F3EF) to Color(0xFF117A70),
    Color(0xFFECE5FF) to Color(0xFF5A3FB0),
)

@Composable
private fun GiftBox(index: Int, sticker: Sticker, picked: Sticker?, reveal: () -> Float, onPick: () -> Unit) {
    val (soft, deep) = giftColors[index % giftColors.size]
    val isPicked = picked == sticker
    val dim = picked != null && !isPicked
    ChunkyBox(
        modifier = Modifier
            .size(84.dp)
            .graphicsLayer { alpha = if (dim) 0.35f else 1f }
            .semantics { contentDescription = if (isPicked) "${sticker.name} 스티커" else "선물 상자 ${index + 1}" },
        color = if (isPicked) KidsColors.Paper else soft,
        shadow = if (isPicked) KidsColors.Sun else deep.copy(alpha = 0.35f),
        radius = 24.dp,
        onClick = onPick,
        enabled = picked == null,
    ) {
        if (isPicked) {
            Text(
                sticker.emoji,
                fontSize = 44.sp,
                modifier = Modifier.graphicsLayer {
                    val r = reveal()
                    scaleX = r
                    scaleY = r
                    rotationZ = (1f - r) * -40f
                },
            )
        } else {
            val icon = remember(deep) { KidIcons.gift(deep) }
            VectorIcon(icon, size = 50.dp)
        }
    }
}

/** 보호자에게 보여 주는 "함께 이야기해요" 카드 */
@Composable
fun TalkCard(text: String, modifier: Modifier = Modifier) {
    ChunkyBox(
        modifier = modifier,
        color = KidsColors.TalkCard,
        shadow = KidsColors.TalkCardShadow,
        radius = 28.dp,
        depth = 8.dp,
        contentAlignment = Alignment.TopStart,
    ) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val icon = remember { KidIcons.talk(KidsColors.Ink) }
                VectorIcon(icon, size = 30.dp)
                Spacer(Modifier.width(8.dp))
                Text("함께 이야기해요", fontSize = 21.sp, color = KidsColors.Ink)
            }
            Text("보호자와 함께 · 1분", style = ParentTextStyle, fontSize = 13.sp, color = KidsColors.TalkCardInk)
            Text(text, style = ParentTextStyle, fontSize = 16.sp, lineHeight = 24.sp, color = KidsColors.Ink)
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
                fontSize = 44.sp,
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
