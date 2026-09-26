package com.ssukssuk.playground.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.ui.components.LocalServices
import com.ssukssuk.playground.ui.components.Mascot
import com.ssukssuk.playground.ui.components.MascotAction
import com.ssukssuk.playground.ui.components.MascotMood
import com.ssukssuk.playground.ui.components.PillButton
import com.ssukssuk.playground.ui.components.SkyBackground
import kotlinx.coroutines.delay

/** 정해진 놀이 시간이 지나면 나오는 쉬는 시간 화면 */
@Composable
fun RestScreen(onParent: () -> Unit) {
    val services = LocalServices.current
    LaunchedEffect(Unit) {
        delay(600)
        services.speaker.speak("쑥쑥이도 잠깐 쉬어요. 눈을 감고 기지개를 쭉 켜 볼까요? 다음에 또 같이 놀아요!")
    }
    val transition = rememberInfiniteTransition(label = "zzz")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "zzzTime",
    )
    Box(Modifier.fillMaxSize()) {
        SkyBackground(night = true)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(Modifier.size(width = 240.dp, height = 280.dp)) {
                Mascot(Modifier.fillMaxSize(), mood = MascotMood.SLEEPY, action = MascotAction.SLEEP)
                repeat(3) { i ->
                    val p = (t + i / 3f) % 1f
                    Text(
                        text = "Z",
                        fontSize = (20 + i * 8).sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .graphicsLayer {
                                translationX = -30.dp.toPx() + p * 30.dp.toPx()
                                translationY = 80.dp.toPx() - p * 90.dp.toPx()
                                alpha = 1f - p
                            },
                    )
                }
            }
            Spacer(Modifier.width(24.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("쉬는 시간이에요 🌙", fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "눈을 감고 기지개를 쭉~ 켜 봐요.\n창밖 먼 곳을 바라보면 눈이 편안해져요.",
                    fontSize = 20.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 30.sp,
                )
                Spacer(Modifier.height(24.dp))
                PillButton(
                    text = "보호자 확인하고 계속하기",
                    icon = "🔒",
                    onClick = onParent,
                    color = Color.White.copy(alpha = 0.22f),
                    fontSize = 18.sp,
                )
            }
        }
    }
}
