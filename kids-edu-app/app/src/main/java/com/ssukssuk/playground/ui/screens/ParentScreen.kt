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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.content.Korean
import com.ssukssuk.playground.content.OfflineIdeas
import com.ssukssuk.playground.content.TalkCards
import com.ssukssuk.playground.core.Difficulty
import com.ssukssuk.playground.core.Domain
import com.ssukssuk.playground.core.Game
import com.ssukssuk.playground.core.Settings
import com.ssukssuk.playground.core.SkillStatus
import com.ssukssuk.playground.ui.AppState
import com.ssukssuk.playground.ui.components.KidIcons
import com.ssukssuk.playground.ui.components.LocalServices
import com.ssukssuk.playground.ui.components.PillButton
import com.ssukssuk.playground.ui.components.VectorIcon
import com.ssukssuk.playground.ui.theme.KidsColors
import com.ssukssuk.playground.ui.theme.ParentTextStyle
import kotlin.math.roundToInt

private enum class ParentTab(val label: String) {
    GROWTH("성장 기록"),
    TOGETHER("함께 놀기"),
    SETTINGS("설정"),
    ABOUT("앱 안내"),
}

private val Muted = KidsColors.InkSoft

/**
 * 보호자·교사 메뉴.
 * 점수 대신 '할 수 있게 된 것'과 '연습하고 있는 것', 영역별로 논 횟수, 오늘 논 시간을 보여 주고,
 * 화면 밖에서 함께 할 놀이와 설정(이름·나이·소리·놀이 시간)을 모았습니다.
 */
@Composable
fun ParentScreen(appState: AppState) {
    var tab by rememberSaveable { mutableStateOf(ParentTab.GROWTH) }
    Row(
        Modifier
            .fillMaxSize()
            .background(KidsColors.ParentBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        Column(
            modifier = Modifier
                .width(196.dp)
                .fillMaxHeight()
                .background(Color.White)
                .padding(horizontal = 14.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text("보호자 공간", fontSize = 22.sp, color = KidsColors.Ink, modifier = Modifier.padding(start = 10.dp, bottom = 12.dp))
            ParentTab.entries.forEach { item ->
                val selected = item == tab
                Text(
                    text = item.label,
                    style = body(16.sp, if (selected) FontWeight.Bold else FontWeight.Normal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (selected) KidsColors.ParentLine else Color.Transparent)
                        .clickable { tab = item }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                )
            }
            Spacer(Modifier.weight(1f))
            PillButton(text = "아이 화면으로", onClick = appState::goHome, fontSize = 17.sp, modifier = Modifier.fillMaxWidth())
        }
        Column(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when (tab) {
                ParentTab.GROWTH -> GrowthTab(appState)
                ParentTab.TOGETHER -> TogetherTab()
                ParentTab.SETTINGS -> SettingsTab(appState)
                ParentTab.ABOUT -> AboutTab()
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun body(size: TextUnit = 15.sp, weight: FontWeight = FontWeight.Normal, color: Color = KidsColors.Ink): TextStyle =
    ParentTextStyle.copy(fontSize = size, fontWeight = weight, color = color, lineHeight = size * 1.5f)

@Composable
private fun Panel(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(22.dp))
            .padding(horizontal = 22.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(title, fontSize = 20.sp, color = KidsColors.Ink)
        content()
    }
}

private fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    return if (minutes < 60) "${minutes}분" else "${minutes / 60}시간 ${minutes % 60}분"
}

// ── 성장 기록 ─────────────────────────────────────────────────

@Composable
private fun GrowthTab(appState: AppState) {
    val name = appState.childName
    Row(verticalAlignment = Alignment.Bottom) {
        Text(if (name.isBlank()) "성장 기록" else "${Korean.friendlyName(name)}의 성장 기록", fontSize = 30.sp, color = KidsColors.Ink)
        Spacer(Modifier.width(14.dp))
        Text("점수 대신 할 수 있게 된 것을 보여 드려요", style = body(14.sp, color = Muted), modifier = Modifier.padding(bottom = 4.dp))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        SkillsPanel(appState, Modifier.weight(1f))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            TimePanel(appState)
            DomainPanel(appState)
        }
    }
    RecordsPanel(appState)
}

@Composable
private fun SkillsPanel(appState: AppState, modifier: Modifier) {
    val byStatus = Game.entries.groupBy { appState.record(it).status }
    Panel("할 수 있게 된 것", modifier) {
        val canDo = byStatus[SkillStatus.CAN_DO].orEmpty()
        if (canDo.isEmpty()) {
            Text("놀이를 여러 번 하면 여기에 모여요. 첫 시도에 잘 맞히는 놀이가 생기면 알려 드릴게요.", style = body(14.sp, color = Muted))
        }
        canDo.forEach { SkillRow(it.skill, it.title, done = true) }
        Spacer(Modifier.height(4.dp))
        Text("연습하고 있는 것", fontSize = 20.sp, color = KidsColors.Ink)
        val practicing = byStatus[SkillStatus.PRACTICING].orEmpty()
        if (practicing.isEmpty()) Text("아직 없어요.", style = body(14.sp, color = Muted))
        practicing.forEach { SkillRow(it.skill, "${it.title} · ${appState.stage(it)}단계", done = false) }
        val enjoyed = byStatus[SkillStatus.ENJOYED].orEmpty()
        if (enjoyed.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text("즐겨 한 자유 놀이: " + enjoyed.joinToString(", ") { it.title }, style = body(14.sp, color = Muted))
        }
    }
}

@Composable
private fun SkillRow(skill: String, detail: String, done: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(24.dp)
                .background(if (done) Color(0xFFD6F3EF) else Color(0xFFFFF0CC), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (done) {
                val check = remember { KidIcons.check(Color(0xFF117A70)) }
                VectorIcon(check, size = 16.dp)
            } else {
                Box(Modifier.size(8.dp).background(Color(0xFF9A6300), CircleShape))
            }
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(skill, style = body(16.sp))
            Text(detail, style = body(12.sp, color = Muted))
        }
    }
}

@Composable
private fun TimePanel(appState: AppState) {
    val today = appState.playSecondsToday()
    val limit = appState.settings.dailyLimitMinutes
    Panel("오늘 논 시간") {
        Row(verticalAlignment = Alignment.Bottom) {
            Text("${today / 60}", fontSize = 40.sp, color = KidsColors.Ink)
            Text(" 분", style = body(17.sp), modifier = Modifier.padding(bottom = 8.dp))
            Spacer(Modifier.weight(1f))
            Text(if (limit == 0) "하루 제한 없음" else "하루 ${limit}분까지", style = body(14.sp, color = Muted))
        }
        if (limit > 0) {
            val limitSeconds = appState.todayLimitSeconds ?: (limit * 60)
            Bar(fraction = today.toFloat() / limitSeconds, color = Color(0xFF7656D6))
        }
        Text(
            "누적 ${formatDuration(appState.totalPlaySeconds())} · ${appState.settings.restMinutes.takeIf { it > 0 }?.let { "${it}분마다 쉬어 가요" } ?: "쉬는 시간 알림 꺼짐"}",
            style = body(13.sp, color = Muted),
        )
    }
}

@Composable
private fun Bar(fraction: Float, color: Color, height: Int = 14) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape((height / 2).dp))
            .background(Color(0xFFEEEAF6)),
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .background(color, RoundedCornerShape((height / 2).dp)),
        )
    }
}

@Composable
private fun DomainPanel(appState: AppState) {
    val playsByDomain = Domain.entries.associateWith { domain ->
        Game.entries.filter { it.domain == domain }.sumOf { appState.record(it).plays }
    }
    val max = (playsByDomain.values.maxOrNull() ?: 0).coerceAtLeast(1)
    Panel("누리과정 영역별로 논 횟수") {
        Domain.entries.forEach { domain ->
            val plays = playsByDomain[domain] ?: 0
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(domain.title, style = body(14.sp), modifier = Modifier.width(104.dp))
                Box(Modifier.weight(1f)) { Bar(plays / max.toFloat(), Color(domain.colorArgb), height = 10) }
                Text("$plays", style = body(14.sp), modifier = Modifier.width(34.dp).padding(start = 8.dp))
            }
        }
        Text("적게 한 영역의 놀이를 함께 해 보면 골고루 자라요.", style = body(12.sp, color = Muted))
    }
}

@Composable
private fun RecordsPanel(appState: AppState) {
    Panel("놀이별 기록") {
        Game.entries.forEachIndexed { index, game ->
            if (index > 0) HorizontalDivider(color = KidsColors.ParentLine)
            val record = appState.record(game)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(10.dp).background(Color(game.domain.colorArgb), CircleShape))
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("${game.title} · ${game.domain.title}", style = body(15.sp, FontWeight.Bold))
                    Text(game.curriculum, style = body(12.sp, color = Muted))
                }
                Spacer(Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text("${record.plays}회", style = body(14.sp))
                    val accuracy = record.accuracy
                    Text(
                        text = if (accuracy == null) "자유 놀이" else "첫 시도 ${(accuracy * 100).roundToInt()}% · ${appState.stage(game)}단계",
                        style = body(12.sp, color = Muted),
                    )
                }
            }
        }
        Text(
            "단계는 나이에서 시작해 놀이마다 첫 시도 정답률(80% 이상 ↑, 50% 미만 ↓)로 한 판씩 조절돼요. 아이에게는 점수를 보여 주지 않아요.",
            style = body(12.sp, color = Muted),
        )
    }
}

// ── 함께 놀기 ────────────────────────────────────────────────

@Composable
private fun TogetherTab() {
    Text("화면 밖에서 함께 해 보세요", fontSize = 30.sp, color = KidsColors.Ink)
    Text(
        "앱에서 한 놀이를 생활 속 대화로 이어 가면 배움이 더 커져요. 놀이를 마칠 때마다 '함께 이야기해요' 카드도 나와요.",
        style = body(14.sp, color = Muted),
    )
    Panel("오늘의 몸 놀이 · 생활 놀이") {
        OfflineIdeas.all.forEach { idea ->
            Text("• ${idea.title} — ${idea.detail}", style = body(15.sp))
        }
    }
    Domain.entries.forEach { domain ->
        val games = Game.entries.filter { it.domain == domain }
        Panel(domain.title) {
            games.forEach { game ->
                TalkCards.forGame(game).forEach { card ->
                    Row {
                        Box(
                            Modifier
                                .padding(top = 7.dp)
                                .size(8.dp)
                                .background(Color(domain.colorArgb), CircleShape),
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(game.title, style = body(12.sp, color = Muted))
                            Text(card, style = body(15.sp))
                        }
                    }
                }
            }
        }
    }
}

// ── 설정 ────────────────────────────────────────────────────

@Composable
private fun SettingsTab(appState: AppState) {
    val settings = appState.settings
    val speaker = LocalServices.current.speaker
    var confirmReset by remember { mutableStateOf(false) }
    var nameDraft by remember { mutableStateOf(settings.childName) }
    Text("설정", fontSize = 30.sp, color = KidsColors.Ink)
    Panel("아이") {
        SettingLabel("부르는 이름")
        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(
                value = nameDraft,
                onValueChange = { nameDraft = it.take(Settings.NAME_MAX_LENGTH) },
                singleLine = true,
                textStyle = body(18.sp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier
                    .width(180.dp)
                    .background(KidsColors.Paper, RoundedCornerShape(10.dp))
                    .border(1.dp, KidsColors.PaperShadow, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .semantics { contentDescription = "아이 이름 입력" },
            )
            Spacer(Modifier.width(10.dp))
            OutlinedButton(onClick = { appState.updateSettings(settings.copy(childName = nameDraft)) }) {
                Text("저장", style = body(14.sp))
            }
        }
        Text(
            "쑥쑥이가 \"안녕, ${if (settings.childName.isBlank()) "OO아" else Korean.vocative(settings.childName)}!\" 하고 불러 줘요. 이름은 이 기기에만 저장되고 어디로도 보내지 않아요.",
            style = body(12.sp, color = Muted),
        )
        SettingLabel("나이 (만)")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Difficulty.SUPPORTED_AGES.forEach { age ->
                ChoiceChip("만 ${age}세", selected = settings.age == age) {
                    appState.updateSettings(settings.copy(age = age))
                }
            }
        }
        Text("나이를 바꾸면 놀이별 단계가 새 나이의 출발점으로 돌아가요.", style = body(12.sp, color = Muted))
    }
    Panel("소리") {
        SwitchRow("음성 안내", settings.voiceOn) { appState.updateSettings(settings.copy(voiceOn = it)) }
        SwitchRow("효과음", settings.soundOn) { appState.updateSettings(settings.copy(soundOn = it)) }
        val clips = speaker.recordedClips
        Text(
            if (clips > 0) {
                "미리 녹음한 고품질 음성 ${clips}개를 쓰고, 없는 문장만 기기 음성으로 읽어요."
            } else {
                "기기의 음성 합성(TTS)으로 읽어요. 고품질 음성 파일을 넣는 방법은 README를 참고하세요."
            },
            style = body(12.sp, color = Muted),
        )
        if (!speaker.isAvailable) {
            Text(
                "한국어 음성 엔진을 찾지 못했어요. 기기 설정 > 텍스트 음성 변환(TTS)에서 한국어 음성 데이터를 설치해 주세요.",
                style = body(12.sp, color = Color(0xFFC0392B)),
            )
        }
    }
    Panel("놀이 시간") {
        SettingLabel("하루 놀이 시간")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Settings.DAILY_OPTIONS.forEach { minutes ->
                ChoiceChip(if (minutes == 0) "제한 없음" else "${minutes}분", selected = settings.dailyLimitMinutes == minutes) {
                    appState.updateSettings(settings.copy(dailyLimitMinutes = minutes))
                }
            }
        }
        Text("WHO·미국소아과학회는 만 2~5세의 화면 시간을 하루 1시간 이내로 권해요.", style = body(12.sp, color = Muted))
        SettingLabel("쉬어 가는 간격")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Settings.REST_OPTIONS.forEach { minutes ->
                ChoiceChip(if (minutes == 0) "끄기" else "${minutes}분", selected = settings.restMinutes == minutes) {
                    appState.updateSettings(settings.copy(restMinutes = minutes))
                }
            }
        }
        Text("놀이 도중에는 끊지 않고, 놀이를 마친 뒤 쉬는 화면을 보여 줘요. 끝나기 3분 전에 미리 알려 줘요.", style = body(12.sp, color = Muted))
    }
    Panel("기록") {
        if (!confirmReset) {
            OutlinedButton(onClick = { confirmReset = true }) { Text("놀이 기록 초기화", style = body(14.sp)) }
        } else {
            Text("스티커, 놀이 기록, 단계가 모두 지워져요. 계속할까요?", style = body(14.sp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        appState.resetProgress()
                        confirmReset = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B)),
                ) { Text("모두 지우기", style = body(14.sp, color = Color.White)) }
                OutlinedButton(onClick = { confirmReset = false }) { Text("취소", style = body(14.sp)) }
            }
        }
    }
}

@Composable
private fun SettingLabel(text: String) {
    Text(text, style = body(15.sp, FontWeight.Bold))
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(label, style = body(15.sp), modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun ChoiceChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(if (selected) KidsColors.Correct else Color.White)
            .border(1.dp, if (selected) KidsColors.Correct else Color(0xFFCFD5E0), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
    ) {
        Text(label, style = body(13.sp, color = if (selected) Color.White else KidsColors.Ink))
    }
}

// ── 앱 안내 ─────────────────────────────────────────────────

@Composable
private fun AboutTab() {
    Text("앱 안내", fontSize = 30.sp, color = KidsColors.Ink)
    Panel("이렇게 만들었어요") {
        Bullet("개인정보", "인터넷 권한이 없고 광고·결제·외부 전송이 없어요. 이름과 기록은 이 기기에만 저장돼요.")
        Bullet("놀이 중심", "2019 개정 누리과정 5개 영역과 연결된 13가지 놀이예요.")
        Bullet("과정 칭찬", "'최고야' 대신 '다시 생각해서 찾아냈구나'처럼 아이가 한 과정을 칭찬해요(Cimpian 외 2007).")
        Bullet("설명하는 피드백", "틀리면 왜 다른지 말해 주고, 두 번 틀리면 같이 세어 보거나 정답 쪽을 살짝 알려 줘요.")
        Bullet("나에게 맞는 단계", "나이는 출발점이고, 놀이마다 아이가 해낸 만큼 단계가 오르내려요.")
        Bullet("깜짝 선물", "매번 주는 보상은 놀이 자체의 재미를 줄일 수 있어, 그날 처음 마친 놀이에서만 스티커를 골라요(Deci 외 1999).")
        Bullet("한글", "자음은 이름과 소리를 함께('기역은 그 소리'), 그림 낱말은 받침 없는 첫 음절(고, 나, 도)로 골랐어요(Cho 2009).")
        Bullet("함께 이야기하기", "놀이를 마치면 보호자에게 대화 거리를 보여 줘요. 어른과 함께 쓸 때 효과가 더 커요(Taylor 외 2024).")
        Bullet("앱 고정", "기기 설정의 '앱 고정(화면 고정)'을 켜면 아이가 다른 앱으로 나가지 않아요.")
    }
    Text("쑥쑥 놀이터 2.0.0 · 글꼴: 주아체(SIL OFL 1.1)", style = body(12.sp, color = Muted))
}

@Composable
private fun Bullet(title: String, text: String) {
    Column {
        Text("• $title", style = body(15.sp, FontWeight.Bold))
        Text(text, style = body(13.sp, color = Muted), modifier = Modifier.padding(start = 12.dp))
    }
}
