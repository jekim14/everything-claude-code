package com.ssukssuk.playground.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.content.Stickers
import com.ssukssuk.playground.core.Difficulty
import com.ssukssuk.playground.core.Domain
import com.ssukssuk.playground.core.Game
import com.ssukssuk.playground.core.Settings
import com.ssukssuk.playground.ui.AppState
import com.ssukssuk.playground.ui.components.LocalServices
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlin.math.roundToInt

private val PanelBackground = Color(0xFFF3F5F9)
private val Muted = Color(0xFF6E7485)

/**
 * 보호자·교사 메뉴.
 * 누리과정 영역별 놀이 기록, 놀이별 첫 시도 정답률, 나이·소리·쉬는 시간 설정을 제공합니다.
 */
@Composable
fun ParentScreen(appState: AppState) {
    Column(
        Modifier
            .fillMaxSize()
            .background(PanelBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = appState::goHome) {
                Text("← 아이 화면으로", fontSize = 16.sp)
            }
            Spacer(Modifier.width(8.dp))
            Text("보호자 · 교사 메뉴", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = KidsColors.Ink)
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(
                Modifier
                    .weight(1.25f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SummaryCard(appState)
                DomainCard(appState)
                RecordsCard(appState)
                Spacer(Modifier.height(8.dp))
            }
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SettingsCard(appState)
                AboutCard()
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun Panel(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = KidsColors.Ink)
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

private fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    return if (minutes < 60) "${minutes}분" else "${minutes / 60}시간 ${minutes % 60}분"
}

@Composable
private fun SummaryCard(appState: AppState) {
    val collected = Stickers.all.count { (appState.stickerCounts[it.id] ?: 0) > 0 }
    Panel("한눈에 보기") {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            StatTile("오늘 놀이", formatDuration(appState.playSecondsToday()), Modifier.weight(1f))
            StatTile("누적 놀이", formatDuration(appState.totalPlaySeconds()), Modifier.weight(1f))
            StatTile("완료한 놀이", "${appState.stars}회", Modifier.weight(1f))
            StatTile("스티커", "$collected/${Stickers.all.size}", Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier) {
    Column(
        modifier
            .background(PanelBackground, RoundedCornerShape(14.dp))
            .padding(vertical = 10.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = KidsColors.Ink, maxLines = 1)
        Text(label, fontSize = 12.sp, color = Muted, fontWeight = FontWeight.Normal)
    }
}

@Composable
private fun DomainCard(appState: AppState) {
    val playsByDomain = Domain.entries.associateWith { domain ->
        Game.entries.filter { it.domain == domain }.sumOf { appState.record(it).plays }
    }
    val max = (playsByDomain.values.maxOrNull() ?: 0).coerceAtLeast(1)
    Panel("누리과정 영역별 놀이 횟수") {
        Domain.entries.forEach { domain ->
            val plays = playsByDomain[domain] ?: 0
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Box(
                    Modifier
                        .size(12.dp)
                        .background(Color(domain.colorArgb), CircleShape),
                )
                Spacer(Modifier.width(8.dp))
                Text(domain.title, fontSize = 14.sp, modifier = Modifier.width(96.dp), color = KidsColors.Ink)
                Box(
                    Modifier
                        .weight(1f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(PanelBackground),
                ) {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(plays / max.toFloat())
                            .background(Color(domain.colorArgb)),
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text("${plays}회", fontSize = 14.sp, color = Muted, modifier = Modifier.width(44.dp))
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            "영역별로 골고루 놀 수 있도록 적게 한 영역의 놀이를 함께 해 보세요.",
            fontSize = 12.sp,
            color = Muted,
            fontWeight = FontWeight.Normal,
        )
    }
}

@Composable
private fun RecordsCard(appState: AppState) {
    Panel("놀이별 기록") {
        Game.entries.forEachIndexed { index, game ->
            if (index > 0) HorizontalDivider(color = PanelBackground)
            val record = appState.record(game)
            Row(Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(game.icon, fontSize = 22.sp, modifier = Modifier.width(36.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(game.title, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = KidsColors.Ink)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = game.domain.title,
                            fontSize = 11.sp,
                            color = Color.White,
                            modifier = Modifier
                                .background(Color(game.domain.colorArgb), RoundedCornerShape(50))
                                .padding(horizontal = 7.dp, vertical = 1.dp),
                        )
                    }
                    Text(game.curriculum, fontSize = 12.sp, color = Muted, fontWeight = FontWeight.Normal)
                }
                Spacer(Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text("${record.plays}회", fontSize = 14.sp, color = KidsColors.Ink)
                    val accuracy = record.accuracy
                    Text(
                        text = if (accuracy == null) "자유 놀이" else "첫 시도 ${(accuracy * 100).roundToInt()}%",
                        fontSize = 12.sp,
                        color = Muted,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "첫 시도 정답률은 참고용이에요. 아이에게는 점수 대신 끝까지 해낸 것을 칭찬해 주세요.",
            fontSize = 12.sp,
            color = Muted,
            fontWeight = FontWeight.Normal,
        )
    }
}

@Composable
private fun SettingsCard(appState: AppState) {
    val settings = appState.settings
    val speaker = LocalServices.current.speaker
    var confirmReset by remember { mutableStateOf(false) }
    Panel("설정") {
        SettingLabel("아이 나이 (만)")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Difficulty.SUPPORTED_AGES.forEach { age ->
                ChoiceChip("만 ${age}세", selected = settings.age == age) {
                    appState.updateSettings(settings.copy(age = age))
                }
            }
        }
        Text(
            if (settings.difficulty.isYounger) {
                "수 1~5, 짝꿍 카드 4쌍, 모양 3개로 성공 경험을 먼저 쌓아요."
            } else {
                "수 1~10, 짝꿍 카드 6쌍, 비슷한 자음 구별 등 조금 더 도전해요."
            },
            fontSize = 12.sp,
            color = Muted,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(top = 4.dp),
        )
        Spacer(Modifier.height(12.dp))
        SwitchRow("음성 안내", settings.voiceOn) { appState.updateSettings(settings.copy(voiceOn = it)) }
        SwitchRow("효과음", settings.soundOn) { appState.updateSettings(settings.copy(soundOn = it)) }
        if (!speaker.isAvailable) {
            Text(
                "한국어 음성 엔진을 찾지 못했어요. 기기 설정 > 텍스트 음성 변환(TTS)에서 한국어 음성 데이터를 설치해 주세요.",
                fontSize = 12.sp,
                color = Color(0xFFD84315),
                fontWeight = FontWeight.Normal,
            )
        }
        Spacer(Modifier.height(12.dp))
        SettingLabel("쉬는 시간 알림")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Settings.REST_OPTIONS.forEach { minutes ->
                ChoiceChip(if (minutes == 0) "끄기" else "${minutes}분", selected = settings.restMinutes == minutes) {
                    appState.updateSettings(settings.copy(restMinutes = minutes))
                }
            }
        }
        Text(
            "놀이 도중에는 끊지 않고, 놀이를 마친 뒤 쉬는 시간 화면을 보여 줘요.",
            fontSize = 12.sp,
            color = Muted,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(top = 4.dp),
        )
        Spacer(Modifier.height(14.dp))
        HorizontalDivider(color = PanelBackground)
        Spacer(Modifier.height(10.dp))
        if (!confirmReset) {
            OutlinedButton(onClick = { confirmReset = true }) {
                Text("놀이 기록 초기화", fontSize = 14.sp)
            }
        } else {
            Text("별, 스티커, 놀이 기록이 모두 지워져요. 계속할까요?", fontSize = 13.sp, color = KidsColors.Ink)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
                Button(
                    onClick = {
                        appState.resetProgress()
                        confirmReset = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD84315)),
                ) { Text("모두 지우기", fontSize = 14.sp) }
                OutlinedButton(onClick = { confirmReset = false }) { Text("취소", fontSize = 14.sp) }
            }
        }
    }
}

@Composable
private fun SettingLabel(text: String) {
    Text(text, fontSize = 14.sp, color = KidsColors.Ink, modifier = Modifier.padding(bottom = 6.dp))
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 15.sp, color = KidsColors.Ink, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun ChoiceChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(if (selected) KidsColors.Leaf else Color.White)
            .border(1.dp, if (selected) KidsColors.Leaf else Color(0xFFCFD5E0), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
    ) {
        Text(label, fontSize = 13.sp, color = if (selected) Color.White else KidsColors.Ink)
    }
}

@Composable
private fun AboutCard() {
    Panel("앱 안내") {
        Bullet("개인정보", "인터넷 권한이 없고 광고·결제·외부 전송이 없어요. 기록은 이 기기에만 저장돼요.")
        Bullet("놀이 중심", "2019 개정 누리과정의 5개 영역과 연결된 10가지 놀이로 구성했어요.")
        Bullet("음성 안내", "글을 읽지 못해도 혼자 놀 수 있도록 모든 안내를 소리로 들려줘요.")
        Bullet("격려하는 피드백", "틀려도 부정적인 말 대신 다시 해 보도록 격려하고, 점수 대신 스티커로 참여를 칭찬해요.")
        Bullet("함께 이야기하기", "기분 친구 놀이가 끝나면 '나는 언제 그런 기분이었는지' 아이와 이야기해 보세요.")
        Bullet("앱 고정", "기기 설정의 '앱 고정(화면 고정)'을 켜면 아이가 다른 앱으로 나가지 않아요.")
        Spacer(Modifier.height(6.dp))
        Text("쑥쑥 놀이터 1.0.0", fontSize = 12.sp, color = Muted, fontWeight = FontWeight.Normal)
    }
}

@Composable
private fun Bullet(title: String, body: String) {
    Column(Modifier.padding(vertical = 4.dp)) {
        Text("• $title", fontSize = 14.sp, color = KidsColors.Ink)
        Text(body, fontSize = 12.sp, color = Muted, fontWeight = FontWeight.Normal, modifier = Modifier.padding(start = 10.dp))
    }
}
