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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.content.Korean
import com.ssukssuk.playground.content.Lines
import com.ssukssuk.playground.core.Game
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.ui.AppState
import com.ssukssuk.playground.ui.GatePurpose
import com.ssukssuk.playground.ui.Screen
import com.ssukssuk.playground.ui.components.ChunkyBox
import com.ssukssuk.playground.ui.components.GameIcon
import com.ssukssuk.playground.ui.components.KidIcons
import com.ssukssuk.playground.ui.components.LocalServices
import com.ssukssuk.playground.ui.components.Mascot
import com.ssukssuk.playground.ui.components.MascotAction
import com.ssukssuk.playground.ui.components.RoundIconButton
import com.ssukssuk.playground.ui.components.SkyBackground
import com.ssukssuk.playground.ui.components.VectorIcon
import com.ssukssuk.playground.ui.components.bouncyClick
import com.ssukssuk.playground.ui.theme.KidsColors
import com.ssukssuk.playground.ui.theme.ParentTextStyle
import kotlinx.coroutines.delay
import kotlin.math.ceil

@Composable
fun HomeScreen(appState: AppState) {
    val services = LocalServices.current
    val name = appState.childName
    var greetingIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        delay(400)
        val untilRest = appState.secondsUntilRest
        when {
            untilRest != null && untilRest <= REST_WARNING_SECONDS -> services.speaker.speak(Lines.REST_SOON)
            !appState.greeted -> {
                appState.greeted = true
                services.speaker.speak(Lines.homeHello(name, Lines.homeGreetings[0]))
            }
            else -> services.speaker.speak(Lines.homeGreetings[0])
        }
    }
    Box(Modifier.fillMaxSize()) {
        SkyBackground()
        Column(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 24.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Mascot(
                    modifier = Modifier
                        .size(width = 88.dp, height = 100.dp)
                        .bouncyClick {
                            greetingIndex = (greetingIndex + 1) % Lines.homeGreetings.size
                            services.sound.play(Sfx.STAR)
                            services.speaker.speak(Lines.homeGreetings[greetingIndex])
                        },
                    action = MascotAction.WAVE,
                )
                Spacer(Modifier.width(18.dp))
                GreetingBubble(
                    title = if (name.isBlank()) "안녕!" else "안녕, ${Korean.vocative(name)}!",
                    subtitle = Lines.homeGreetings[greetingIndex],
                )
                Spacer(Modifier.weight(1f))
                val limit = appState.todayLimitSeconds
                if (limit != null) {
                    SunMeter(progress = appState.todaySeconds.toFloat() / limit)
                    Spacer(Modifier.width(12.dp))
                }
                RoundIconButton(
                    icon = KidIcons.book(KidsColors.Ink),
                    contentDescription = "스티커 책",
                    shadow = KidsColors.SkyShadow,
                    size = 64.dp,
                    onClick = {
                        services.speaker.speak(Lines.STICKER_BOOK)
                        appState.navigate(Screen.Stickers)
                    },
                )
                Spacer(Modifier.width(12.dp))
                ParentButton(onClick = { appState.navigate(Screen.Gate(GatePurpose.PARENT_AREA)) })
            }
            GameGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp, bottom = 16.dp),
                onPick = { game ->
                    services.sound.play(Sfx.TAP)
                    services.speaker.speak(Lines.gameTitle(game))
                    appState.navigate(Screen.Play(game))
                },
            )
        }
    }
}

private const val REST_WARNING_SECONDS = 3 * 60

@Composable
private fun GreetingBubble(title: String, subtitle: String) {
    Box {
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .padding(start = 1.dp)
                .size(18.dp)
                .graphicsLayer {
                    translationX = -9.dp.toPx()
                    rotationZ = 45f
                }
                .background(Color.White),
        )
        ChunkyBox(shadow = KidsColors.SkyShadow, radius = 26.dp, depth = 5.dp, contentAlignment = Alignment.CenterStart) {
            Column(Modifier.padding(horizontal = 22.dp, vertical = 12.dp)) {
                Text(title, fontSize = 26.sp, color = KidsColors.Ink)
                Text(subtitle, fontSize = 20.sp, color = KidsColors.InkSoft)
            }
        }
    }
}

/** 오늘 놀 시간: 해님에서 달님까지 막대가 차오릅니다. 글을 몰라도 끝이 다가오는 것을 볼 수 있습니다. */
@Composable
private fun SunMeter(progress: Float) {
    val sun = remember { KidIcons.sun(KidsColors.SunDeep) }
    val moon = remember { KidIcons.moon(KidsColors.Moon) }
    ChunkyBox(
        modifier = Modifier.semantics { contentDescription = "오늘 놀이 시간" },
        shadow = KidsColors.SkyShadow,
        radius = 30.dp,
        depth = 5.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VectorIcon(sun, size = 28.dp)
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .size(width = 96.dp, height = 12.dp)
                    .background(Color(0xFFEAF4FB), RoundedCornerShape(6.dp)),
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(progress.coerceIn(0.02f, 1f))
                        .height(12.dp)
                        .background(KidsColors.Sun, RoundedCornerShape(6.dp)),
                )
            }
            Spacer(Modifier.width(8.dp))
            VectorIcon(moon, size = 24.dp)
        }
    }
}

@Composable
private fun GameGrid(modifier: Modifier, onPick: (Game) -> Unit) {
    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        val games = Game.entries
        val gap = 18.dp
        val columns = when {
            maxWidth > maxHeight -> 5
            maxWidth > 700.dp -> 4
            else -> 3
        }
        val rows = ceil(games.size / columns.toFloat()).toInt()
        val tileWidth = (maxWidth - gap * (columns - 1)) / columns
        val tileHeight = (maxHeight - gap * (rows - 1)) / rows
        val width: Dp = min(tileWidth, 200.dp)
        val height: Dp = min(tileHeight, width * 0.86f)
        Column(verticalArrangement = Arrangement.spacedBy(gap), horizontalAlignment = Alignment.CenterHorizontally) {
            games.chunked(columns).forEachIndexed { rowIndex, rowGames ->
                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    rowGames.forEachIndexed { columnIndex, game ->
                        GameTile(
                            game = game,
                            index = rowIndex * columns + columnIndex,
                            width = width,
                            height = height,
                            onClick = { onPick(game) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GameTile(game: Game, index: Int, width: Dp, height: Dp, onClick: () -> Unit) {
    val appear = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(index * 50L)
        appear.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow))
    }
    val domain = game.domain
    val plate = min(height * 0.52f, width * 0.46f)
    ChunkyBox(
        modifier = Modifier
            .size(width, height)
            .graphicsLayer {
                scaleX = appear.value
                scaleY = appear.value
                alpha = appear.value.coerceIn(0f, 1f)
            }
            .semantics { contentDescription = game.title },
        shadow = Color(domain.shadowArgb),
        radius = min(28.dp, height * 0.2f),
        onClick = onClick,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(height * 0.05f),
            modifier = Modifier.clearAndSetSemantics { },
        ) {
            Box(
                Modifier
                    .size(plate)
                    .background(Color(domain.softArgb), RoundedCornerShape(plate * 0.29f)),
                contentAlignment = Alignment.Center,
            ) {
                GameIcon(game = game, color = Color(domain.colorArgb), size = plate * 0.7f)
            }
            Text(
                text = game.title,
                fontSize = (height.value * 0.15f).coerceIn(14f, 22f).sp,
                color = KidsColors.Ink,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun ParentButton(onClick: () -> Unit) {
    val lock = remember { KidIcons.lock(KidsColors.InkSoft) }
    Row(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.75f), RoundedCornerShape(50))
            .bouncyClick(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .semantics { contentDescription = "보호자 메뉴" },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VectorIcon(lock, size = 18.dp)
        Spacer(Modifier.width(6.dp))
        Text("보호자", style = ParentTextStyle, fontSize = 15.sp, color = KidsColors.InkSoft, modifier = Modifier.clearAndSetSemantics { })
    }
}
