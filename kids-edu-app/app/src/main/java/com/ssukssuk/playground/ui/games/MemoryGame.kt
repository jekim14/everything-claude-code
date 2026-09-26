package com.ssukssuk.playground.ui.games

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.content.MemoryFace
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.logic.FlipOutcome
import com.ssukssuk.playground.logic.MemoryBoard
import com.ssukssuk.playground.ui.components.BurstEffect
import com.ssukssuk.playground.ui.components.GameScaffold
import com.ssukssuk.playground.ui.components.PraisePop
import com.ssukssuk.playground.ui.components.bouncyClick
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil

private const val CARD_ASPECT = 0.78f

/** 짝꿍 카드: 처음에 그림을 잠깐 보여 준 뒤 덮고, 같은 그림 두 장을 찾습니다(기억·비교). */
@Composable
fun MemoryGame(env: GameEnv) {
    val difficulty = env.difficulty
    val board = remember { MemoryBoard.create(env.random, difficulty.memoryPairs) }
    val faceUp = remember { mutableStateListOf(*Array(board.faces.size) { false }) }
    val matched = remember { mutableStateListOf(*Array(board.faces.size) { false }) }
    var previewing by remember { mutableStateOf(true) }
    var celebrate by remember { mutableIntStateOf(0) }
    var praise by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val matchedPairs = matched.count { it } / 2

    fun sync() {
        board.faces.indices.forEach { i ->
            faceUp[i] = board.isFaceUp(i)
            matched[i] = board.isMatched(i)
        }
    }

    fun prompt() = env.say("똑같은 그림 짝꿍을 찾아봐요!")

    LaunchedEffect(Unit) {
        delay(600)
        env.say("그림을 잘 보고 기억해요!")
        delay(difficulty.memoryPreviewMillis + 1200)
        previewing = false
        env.play(Sfx.FLIP)
        prompt()
    }

    fun flip(index: Int) {
        if (previewing) return
        val face = board.faces[index]
        when (board.flip(index)) {
            FlipOutcome.Ignored -> return
            is FlipOutcome.First -> {
                env.play(Sfx.FLIP)
                env.say(face.name)
            }
            is FlipOutcome.Match -> {
                env.play(Sfx.CORRECT)
                celebrate++
                if (board.isComplete) {
                    val word = env.praise()
                    praise = word
                    env.say("$word 짝꿍을 모두 찾았어요!")
                    scope.launch {
                        delay(1800)
                        env.complete(board.pairCount, board.moves)
                    }
                } else {
                    env.say("짝꿍을 찾았어요! ${face.name}!")
                }
            }
            is FlipOutcome.Mismatch -> {
                env.play(Sfx.FLIP)
                env.say("${face.name}! 짝꿍이 아니에요. 다시 찾아봐요.")
                scope.launch {
                    delay(1300)
                    board.hideMismatch()
                    sync()
                }
            }
        }
        sync()
    }

    GameScaffold(
        title = env.game.title,
        color = Color(env.game.colorArgb),
        onHome = env.onHome,
        progress = matchedPairs,
        total = board.pairCount,
        onReplayVoice = ::prompt,
    ) {
        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            val count = board.faces.size
            val gap = 12.dp
            // 카드가 가장 크게 보이는 열 수를 고릅니다.
            var bestColumns = 2
            var bestWidth = 0.dp
            for (columns in 2..count) {
                val rows = ceil(count / columns.toFloat()).toInt()
                val byWidth = (maxWidth - gap * (columns - 1)) / columns
                val byHeight = ((maxHeight - gap * (rows - 1)) / rows) * CARD_ASPECT
                val width = if (byWidth < byHeight) byWidth else byHeight
                if (width > bestWidth) {
                    bestWidth = width
                    bestColumns = columns
                }
            }
            val cardWidth = if (bestWidth > 150.dp) 150.dp else bestWidth
            Column(verticalArrangement = Arrangement.spacedBy(gap)) {
                board.faces.indices.chunked(bestColumns).forEach { rowIndices ->
                    Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                        rowIndices.forEach { i ->
                            MemoryCard(
                                face = board.faces[i],
                                faceUp = previewing || faceUp[i],
                                matched = matched[i],
                                width = cardWidth,
                                onClick = { flip(i) },
                            )
                        }
                    }
                }
            }
            BurstEffect(trigger = celebrate, modifier = Modifier.fillMaxSize())
        }
        PraisePop(text = praise, trigger = if (praise == null) 0 else celebrate, modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun MemoryCard(face: MemoryFace, faceUp: Boolean, matched: Boolean, width: Dp, onClick: () -> Unit) {
    val rotation by animateFloatAsState(
        targetValue = if (faceUp) 180f else 0f,
        animationSpec = tween(420),
        label = "cardFlip",
    )
    val pop = remember { Animatable(1f) }
    LaunchedEffect(matched) {
        if (matched) {
            pop.animateTo(1.15f, tween(140))
            pop.animateTo(1f, spring(dampingRatio = 0.35f, stiffness = Spring.StiffnessMedium))
        }
    }
    val height = width / CARD_ASPECT
    val shape = RoundedCornerShape(width * 0.14f)
    Box(
        modifier = Modifier
            .size(width = width, height = height)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 14f * density
                scaleX = pop.value
                scaleY = pop.value
            }
            .bouncyClick(enabled = !faceUp, onClick = onClick)
            .semantics { contentDescription = if (faceUp) face.name else "뒤집힌 카드" },
    ) {
        if (rotation <= 90f) {
            // 뒷면
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(6.dp, shape)
                    .background(Brush.linearGradient(listOf(Color(0xFFB39DDB), Color(0xFF7E57C2))), shape)
                    .border(3.dp, Color.White.copy(alpha = 0.7f), shape),
                contentAlignment = Alignment.Center,
            ) {
                Text("🌱", fontSize = (width.value * 0.38f).sp)
            }
        } else {
            // 앞면 (거울처럼 보이지 않도록 한 번 더 뒤집어 그립니다)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
                    .shadow(6.dp, shape)
                    .background(if (matched) Color(0xFFE8F5E9) else Color.White, shape)
                    .border(if (matched) 4.dp else 2.dp, if (matched) KidsColors.Correct else Color(0xFFE0E0E0), shape),
                contentAlignment = Alignment.Center,
            ) {
                Text(face.emoji, fontSize = (width.value * 0.5f).sp)
                if (matched) {
                    Text(
                        text = "✓",
                        fontSize = (width.value * 0.16f).sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = KidsColors.Correct,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(width * 0.06f),
                    )
                }
            }
        }
    }
}
