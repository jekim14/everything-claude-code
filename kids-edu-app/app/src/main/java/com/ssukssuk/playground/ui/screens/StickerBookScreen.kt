package com.ssukssuk.playground.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.content.Lines
import com.ssukssuk.playground.content.Sticker
import com.ssukssuk.playground.content.Stickers
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.ui.AppState
import com.ssukssuk.playground.ui.components.GameScaffold
import com.ssukssuk.playground.ui.components.LocalServices
import com.ssukssuk.playground.ui.components.bouncyClick
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StickerBookScreen(appState: AppState) {
    val services = LocalServices.current
    val counts = appState.stickerCounts
    val collected = Stickers.all.count { (counts[it.id] ?: 0) > 0 }
    LaunchedEffect(Unit) {
        delay(500)
        services.speaker.speak(if (collected == 0) Lines.STICKER_BOOK_EMPTY else Lines.STICKER_BOOK_INTRO)
    }
    GameScaffold(title = "내 스티커 책", color = Color(0xFF9A6300), onHome = appState::goHome) {
        Column(Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(92.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(28.dp)),
            ) {
                items(Stickers.all, key = { it.id }) { sticker ->
                    StickerCell(sticker, counts[sticker.id] ?: 0)
                }
            }
        }
    }
}

@Composable
private fun StickerCell(sticker: Sticker, count: Int) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val wiggle = remember { Animatable(0f) }
    val owned = count > 0
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .graphicsLayer { rotationZ = wiggle.value }
            .shadow(if (owned) 6.dp else 0.dp, CircleShape)
            .background(if (owned) Color.White else Color(0xFFE0E0E0), CircleShape)
            .bouncyClick {
                if (owned) {
                    services.sound.play(Sfx.STAR)
                    services.speaker.speak(sticker.name)
                    scope.launch {
                        wiggle.animateTo(0f, keyframes {
                            durationMillis = 500
                            -14f at 80
                            14f at 200
                            -8f at 320
                            8f at 420
                        })
                    }
                } else {
                    services.speaker.speak(Lines.STICKER_LOCKED)
                }
            }
            .semantics { contentDescription = if (owned) sticker.name else "아직 없는 스티커" },
        contentAlignment = Alignment.Center,
    ) {
        if (owned) {
            Text(sticker.emoji, fontSize = 44.sp)
        } else {
            Text("?", fontSize = 36.sp, color = Color(0xFFBDBDBD))
        }
    }
}
