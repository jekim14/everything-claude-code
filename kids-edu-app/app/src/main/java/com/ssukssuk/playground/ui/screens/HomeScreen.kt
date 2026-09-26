package com.ssukssuk.playground.ui.screens

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.core.Game
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.ui.AppState
import com.ssukssuk.playground.ui.GatePurpose
import com.ssukssuk.playground.ui.Screen
import com.ssukssuk.playground.ui.components.LocalServices
import com.ssukssuk.playground.ui.components.Mascot
import com.ssukssuk.playground.ui.components.MascotAction
import com.ssukssuk.playground.ui.components.RoundIconButton
import com.ssukssuk.playground.ui.components.SkyBackground
import com.ssukssuk.playground.ui.components.SpeechBubble
import com.ssukssuk.playground.ui.components.bouncyClick
import com.ssukssuk.playground.ui.components.rememberBob
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlinx.coroutines.delay
import kotlin.math.ceil

private val greetings = listOf(
    "오늘은 뭐 하고 놀까?",
    "안녕! 나는 쑥쑥이야!",
    "하고 싶은 놀이를 눌러 봐!",
    "같이 놀자! 무엇이든 좋아!",
)

@Composable
fun HomeScreen(appState: AppState) {
    val services = LocalServices.current
    var greetingIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        delay(400)
        services.speaker.speak(greetings[0])
    }
    Box(Modifier.fillMaxSize()) {
        SkyBackground()
        Column(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Mascot(
                    modifier = Modifier
                        .size(width = 96.dp, height = 108.dp)
                        .bouncyClick {
                            greetingIndex = (greetingIndex + 1) % greetings.size
                            services.sound.play(Sfx.STAR)
                            services.speaker.speak(greetings[greetingIndex])
                        },
                    action = MascotAction.WAVE,
                )
                SpeechBubble(
                    text = greetings[greetingIndex],
                    modifier = Modifier.padding(start = 18.dp),
                    fontSize = 20.sp,
                )
                Spacer(Modifier.weight(1f))
                StarCounter(appState.stars)
                Spacer(Modifier.width(10.dp))
                RoundIconButton(
                    icon = "📒",
                    contentDescription = "스티커 책",
                    onClick = {
                        services.speaker.speak("스티커 책")
                        appState.navigate(Screen.Stickers)
                    },
                )
                Spacer(Modifier.width(10.dp))
                ParentButton(onClick = { appState.navigate(Screen.Gate(GatePurpose.PARENT_AREA)) })
            }
            GameGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 12.dp),
                onPick = { game ->
                    services.sound.play(Sfx.TAP)
                    services.speaker.speak(game.title)
                    appState.navigate(Screen.Play(game))
                },
            )
        }
    }
}

@Composable
private fun GameGrid(modifier: Modifier, onPick: (Game) -> Unit) {
    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        val games = Game.entries
        val gap = 14.dp
        val columns = when {
            maxWidth > maxHeight -> 5
            maxWidth > 700.dp -> 4
            else -> 3
        }
        val rows = ceil(games.size / columns.toFloat()).toInt()
        val tileWidth = (maxWidth - gap * (columns - 1)) / columns
        val tileHeight = (maxHeight - gap * (rows - 1)) / rows
        val tile: Dp = min(min(tileWidth, tileHeight), 230.dp)
        Column(verticalArrangement = Arrangement.spacedBy(gap), horizontalAlignment = Alignment.CenterHorizontally) {
            games.chunked(columns).forEachIndexed { rowIndex, rowGames ->
                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    rowGames.forEachIndexed { columnIndex, game ->
                        GameTile(
                            game = game,
                            index = rowIndex * columns + columnIndex,
                            size = tile,
                            onClick = { onPick(game) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GameTile(game: Game, index: Int, size: Dp, onClick: () -> Unit) {
    val appear = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(index * 70L)
        appear.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow))
    }
    val bob = rememberBob(periodMillis = 2600, phase = index * 0.17f)
    val color = Color(game.colorArgb)
    val shape = RoundedCornerShape(size * 0.2f)
    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                scaleX = appear.value
                scaleY = appear.value
                translationY = bob * 3.dp.toPx()
            }
            .shadow(8.dp, shape)
            .background(
                Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.55f).compositeOver(color), color)),
                shape,
            )
            .bouncyClick(onClick = onClick)
            .semantics { contentDescription = game.title },
        contentAlignment = Alignment.Center,
    ) {
        // 누리과정 영역 색 표시 (보호자용)
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .padding(size * 0.08f)
                .size(size * 0.08f)
                .background(Color(game.domain.colorArgb), CircleShape),
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clearAndSetSemantics { }) {
            Text(
                text = game.icon,
                fontSize = (size.value * 0.36f).sp,
                fontWeight = FontWeight.ExtraBold,
                color = KidsColors.Ink,
                modifier = Modifier.graphicsLayer { rotationZ = bob * 4f },
            )
            Text(
                text = game.title,
                fontSize = (size.value * 0.13f).sp,
                fontWeight = FontWeight.ExtraBold,
                color = KidsColors.Ink,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun StarCounter(stars: Int) {
    Row(
        modifier = Modifier
            .shadow(4.dp, RoundedCornerShape(50))
            .background(Color.White, RoundedCornerShape(50))
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .semantics { contentDescription = "받은 별 ${stars}개" },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("⭐", fontSize = 24.sp, modifier = Modifier.clearAndSetSemantics { })
        Spacer(Modifier.width(6.dp))
        Text("$stars", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = KidsColors.Ink, modifier = Modifier.clearAndSetSemantics { })
    }
}

@Composable
private fun ParentButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(50))
            .bouncyClick(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .semantics { contentDescription = "보호자 메뉴" },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("🔒", fontSize = 16.sp, modifier = Modifier.clearAndSetSemantics { })
        Spacer(Modifier.width(4.dp))
        Text("보호자", fontSize = 15.sp, color = KidsColors.InkSoft, modifier = Modifier.clearAndSetSemantics { })
    }
}
