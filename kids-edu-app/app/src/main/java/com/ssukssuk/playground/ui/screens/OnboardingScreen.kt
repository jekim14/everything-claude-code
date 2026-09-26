package com.ssukssuk.playground.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.ui.components.LocalServices
import com.ssukssuk.playground.ui.components.Mascot
import com.ssukssuk.playground.ui.components.MascotAction
import com.ssukssuk.playground.ui.components.MascotMood
import com.ssukssuk.playground.ui.components.SkyBackground
import com.ssukssuk.playground.ui.components.SpeechBubble
import com.ssukssuk.playground.ui.components.bouncyClick
import com.ssukssuk.playground.ui.components.rememberPulse
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** 첫 실행: 쑥쑥이가 인사하고 나이를 물어봅니다. 나이에 따라 놀이 난이도가 달라집니다. */
@Composable
fun OnboardingScreen(onChooseAge: (Int) -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    var chosen by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(Unit) {
        delay(600)
        services.speaker.speak("안녕! 나는 새싹 친구 쑥쑥이야. 너는 몇 살이니? 네 살이면 4를, 다섯 살이면 5를 눌러 줘!")
    }
    Box(Modifier.fillMaxSize()) {
        SkyBackground()
        Row(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Mascot(
                modifier = Modifier.size(width = 220.dp, height = 260.dp),
                mood = if (chosen == null) MascotMood.HAPPY else MascotMood.EXCITED,
                action = if (chosen == null) MascotAction.WAVE else MascotAction.CHEER,
            )
            Spacer(Modifier.width(24.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SpeechBubble(
                    text = if (chosen == null) "안녕! 나는 쑥쑥이야.\n너는 몇 살이니?" else "${chosen}살이구나! 반가워!",
                    fontSize = 24.sp,
                )
                Spacer(Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    listOf(4 to Color(0xFFFFB74D), 5 to Color(0xFF81C784)).forEach { (age, color) ->
                        AgeButton(age = age, color = color, highlighted = chosen == age) {
                            if (chosen != null) return@AgeButton
                            chosen = age
                            services.sound.play(Sfx.CHEER)
                            services.speaker.speak("${age}살이구나! 반가워! 우리 같이 놀자!")
                            scope.launch {
                                delay(1600)
                                onChooseAge(age)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "보호자 안내: 만 나이 기준이에요. 보호자 메뉴에서 언제든 바꿀 수 있어요.",
                    fontSize = 14.sp,
                    color = KidsColors.InkSoft,
                )
            }
        }
    }
}

@Composable
private fun AgeButton(age: Int, color: Color, highlighted: Boolean, onClick: () -> Unit) {
    val pulse = rememberPulse(0.97f, 1.04f, 1100)
    Column(
        modifier = Modifier
            .graphicsLayer {
                val s = if (highlighted) 1.12f else pulse.value
                scaleX = s
                scaleY = s
            }
            .shadow(10.dp, RoundedCornerShape(32.dp))
            .background(color, RoundedCornerShape(32.dp))
            .bouncyClick(onClick = onClick)
            .padding(horizontal = 30.dp, vertical = 12.dp)
            .semantics { contentDescription = "${age}살" },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("🎂", fontSize = 32.sp)
        Row(verticalAlignment = Alignment.Bottom) {
            Text("$age", fontSize = 56.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("살", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, modifier = Modifier.padding(bottom = 10.dp))
        }
    }
}
