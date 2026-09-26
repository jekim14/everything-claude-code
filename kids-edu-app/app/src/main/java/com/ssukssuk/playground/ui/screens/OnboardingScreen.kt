package com.ssukssuk.playground.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.content.Korean
import com.ssukssuk.playground.content.Lines
import com.ssukssuk.playground.core.Settings
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.ui.components.ChunkyBox
import com.ssukssuk.playground.ui.components.LocalServices
import com.ssukssuk.playground.ui.components.Mascot
import com.ssukssuk.playground.ui.components.MascotAction
import com.ssukssuk.playground.ui.components.MascotMood
import com.ssukssuk.playground.ui.components.SkyBackground
import com.ssukssuk.playground.ui.components.SpeechBubble
import com.ssukssuk.playground.ui.components.rememberPulse
import com.ssukssuk.playground.ui.theme.KidsColors
import com.ssukssuk.playground.ui.theme.ParentTextStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 첫 실행: 쑥쑥이가 인사하고 나이를 물어봅니다. 나이는 놀이 난이도의 출발점이 됩니다.
 * 보호자는 아이 이름(부르는 이름)을 적어 둘 수 있습니다. 이름은 이 기기 안에만 저장됩니다.
 */
@Composable
fun OnboardingScreen(onDone: (age: Int, childName: String) -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    var chosen by remember { mutableStateOf<Int?>(null) }
    var name by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        delay(600)
        services.speaker.speak(Lines.ONBOARDING_HELLO)
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
                modifier = Modifier.size(width = 200.dp, height = 240.dp),
                mood = if (chosen == null) MascotMood.HAPPY else MascotMood.EXCITED,
                action = if (chosen == null) MascotAction.WAVE else MascotAction.CHEER,
            )
            Spacer(Modifier.width(24.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SpeechBubble(
                    text = chosen?.let { "${Korean.counterNumber(it)} 살이구나! 반가워!" } ?: "안녕! 나는 쑥쑥이야.\n너는 몇 살이니?",
                    fontSize = 24.sp,
                )
                Spacer(Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    listOf(4 to Color(0xFFE0603E), 5 to Color(0xFF13978B)).forEach { (age, color) ->
                        AgeButton(age = age, color = color, highlighted = chosen == age) {
                            if (chosen != null) return@AgeButton
                            chosen = age
                            services.sound.play(Sfx.CHEER)
                            services.speaker.speak(Lines.withName(Settings.cleanName(name), Lines.onboardingAge(age)))
                            scope.launch {
                                delay(1800)
                                onDone(age, name)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                ParentNameField(name = name, onChange = { name = it.take(Settings.NAME_MAX_LENGTH) })
            }
        }
    }
}

/** 보호자가 적는 아이 이름 칸 */
@Composable
private fun ParentNameField(name: String, onChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.88f), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("보호자: 부르는 이름", style = ParentTextStyle, fontSize = 14.sp, color = KidsColors.InkSoft)
            Spacer(Modifier.width(10.dp))
            BasicTextField(
                value = name,
                onValueChange = onChange,
                singleLine = true,
                textStyle = ParentTextStyle.copy(fontSize = 18.sp, color = KidsColors.Ink),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier
                    .width(140.dp)
                    .background(KidsColors.Paper, RoundedCornerShape(10.dp))
                    .border(1.dp, KidsColors.PaperShadow, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .semantics { contentDescription = "아이 이름 입력" },
                decorationBox = { inner ->
                    Box {
                        if (name.isEmpty()) Text("예: 하늘", style = ParentTextStyle, fontSize = 18.sp, color = KidsColors.InkSoft.copy(alpha = 0.6f))
                        inner()
                    }
                },
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "이름은 이 기기에만 저장돼요. 나이는 만 나이, 보호자 메뉴에서 바꿀 수 있어요.",
            style = ParentTextStyle,
            fontSize = 12.sp,
            color = KidsColors.InkSoft,
        )
    }
}

@Composable
private fun AgeButton(age: Int, color: Color, highlighted: Boolean, onClick: () -> Unit) {
    val pulse = rememberPulse(0.97f, 1.03f, 1100)
    ChunkyBox(
        modifier = Modifier
            .graphicsLayer {
                val s = if (highlighted) 1.1f else pulse.value
                scaleX = s
                scaleY = s
            }
            .semantics { contentDescription = "${Korean.counterNumber(age)} 살" },
        color = color,
        shadow = color.copy(alpha = 0.55f).compositeOver(KidsColors.Ink),
        radius = 32.dp,
        depth = 7.dp,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 34.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text("$age", fontSize = 60.sp, color = Color.White)
                Text("살", fontSize = 28.sp, color = Color.White, modifier = Modifier.padding(bottom = 10.dp))
            }
            Text("${Korean.counterNumber(age)} 살", fontSize = 18.sp, color = Color.White)
        }
    }
}
