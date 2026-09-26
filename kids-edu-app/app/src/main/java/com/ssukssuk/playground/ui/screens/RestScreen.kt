package com.ssukssuk.playground.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.content.Korean
import com.ssukssuk.playground.content.Lines
import com.ssukssuk.playground.content.OfflineIdea
import com.ssukssuk.playground.content.OfflineIdeas
import com.ssukssuk.playground.ui.RestReason
import com.ssukssuk.playground.ui.components.ChunkyBox
import com.ssukssuk.playground.ui.components.GameIcon
import com.ssukssuk.playground.ui.components.KidIcons
import com.ssukssuk.playground.ui.components.LocalServices
import com.ssukssuk.playground.ui.components.Mascot
import com.ssukssuk.playground.ui.components.MascotAction
import com.ssukssuk.playground.ui.components.MascotMood
import com.ssukssuk.playground.ui.components.SkyBackground
import com.ssukssuk.playground.ui.components.SkyStyle
import com.ssukssuk.playground.ui.components.VectorIcon
import com.ssukssuk.playground.ui.components.bouncyClick
import com.ssukssuk.playground.ui.theme.KidsColors
import com.ssukssuk.playground.ui.theme.ParentTextStyle
import kotlinx.coroutines.delay

/**
 * 쉬는 시간·오늘 놀이 끝 화면.
 *
 * 화면을 끄라고 다그치거나 아쉬움을 자극하지 않고, 화면 밖에서 할 놀이를 권합니다.
 * 이어서 놀려면 보호자 확인이 필요합니다(오늘 시간을 다 썼다면 15분 더).
 */
@Composable
fun RestScreen(reason: RestReason, childName: String, onParent: () -> Unit) {
    val services = LocalServices.current
    val ideas = remember { OfflineIdeas.all.shuffled().take(3) }
    val dayDone = reason == RestReason.DAY_DONE
    LaunchedEffect(reason) {
        delay(500)
        services.speaker.speak(if (dayDone) Lines.restDayDone(childName) else Lines.restBreak(childName))
    }
    Box(Modifier.fillMaxSize()) {
        SkyBackground(style = if (dayDone) SkyStyle.EVENING else SkyStyle.DAY)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 40.dp, vertical = 20.dp),
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Mascot(
                    modifier = Modifier.size(width = 112.dp, height = 124.dp),
                    mood = if (dayDone) MascotMood.SLEEPY else MascotMood.HAPPY,
                    action = if (dayDone) MascotAction.SLEEP else MascotAction.WAVE,
                )
                Spacer(Modifier.width(16.dp))
                ChunkyBox(
                    modifier = Modifier.padding(bottom = 18.dp),
                    shadow = if (dayDone) KidsColors.EveningShadow else KidsColors.SkyShadow,
                    radius = 28.dp,
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Column(Modifier.padding(horizontal = 24.dp, vertical = 14.dp)) {
                        Text(if (dayDone) "오늘 놀이는 여기까지!" else "잠깐 쉬어 갈까?", fontSize = 32.sp, color = KidsColors.Ink)
                        val call = if (childName.isBlank()) "" else "${Korean.vocative(childName)}, "
                        Text(
                            text = if (dayDone) "${call}이제 몸으로 놀아 볼까?" else "${call}물 한 모금 마시고 기지개 쭉!",
                            fontSize = 21.sp,
                            color = KidsColors.InkSoft,
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ideas.forEach { idea ->
                    IdeaCard(
                        idea = idea,
                        modifier = Modifier.weight(1f),
                        onClick = { services.speaker.speak(Lines.offlineIdea(idea)) },
                    )
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                val lock = remember { KidIcons.lock(KidsColors.InkSoft) }
                Row(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.75f), RoundedCornerShape(50))
                        .bouncyClick(onClick = onParent)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .semantics { contentDescription = "보호자 확인" },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    VectorIcon(lock, size = 18.dp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (dayDone) "보호자: 오늘 15분 더 하기" else "보호자: 계속하기",
                        style = ParentTextStyle,
                        fontSize = 15.sp,
                        color = KidsColors.InkSoft,
                        modifier = Modifier.clearAndSetSemantics { },
                    )
                }
            }
        }
    }
}

@Composable
private fun IdeaCard(idea: OfflineIdea, modifier: Modifier, onClick: () -> Unit) {
    val domain = idea.game.domain
    ChunkyBox(
        modifier = modifier
            .heightIn(max = 190.dp)
            .fillMaxHeight()
            .semantics { contentDescription = idea.title },
        shadow = Color(0xFFC6DDA8),
        radius = 30.dp,
        depth = 7.dp,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                Modifier
                    .size(64.dp)
                    .background(Color(domain.softArgb), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center,
            ) {
                GameIcon(game = idea.game, color = Color(domain.colorArgb), size = 44.dp)
            }
            Text(idea.title, fontSize = 24.sp, color = KidsColors.Ink, textAlign = TextAlign.Center)
            Text(idea.detail, style = ParentTextStyle, fontSize = 14.sp, color = KidsColors.InkSoft, textAlign = TextAlign.Center)
        }
    }
}
