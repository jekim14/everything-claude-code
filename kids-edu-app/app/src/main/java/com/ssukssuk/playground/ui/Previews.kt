package com.ssukssuk.playground.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ssukssuk.playground.content.Emotion
import com.ssukssuk.playground.core.InMemoryStore
import com.ssukssuk.playground.core.ProgressRepository
import com.ssukssuk.playground.core.Services
import com.ssukssuk.playground.core.Settings
import com.ssukssuk.playground.core.SilentSoundPlayer
import com.ssukssuk.playground.core.SilentSpeaker
import com.ssukssuk.playground.ui.components.Mascot
import com.ssukssuk.playground.ui.components.MascotAction
import com.ssukssuk.playground.ui.games.EmotionFace
import com.ssukssuk.playground.ui.theme.SsukSsukTheme

// Android Studio 미리보기용 화면 (가로 휴대폰 크기)

private fun previewState(): AppState {
    val repository = ProgressRepository(InMemoryStore())
    repository.saveSettings(Settings(age = 5))
    return AppState(repository, Services(SilentSpeaker, SilentSoundPlayer))
}

@Preview(name = "홈", widthDp = 800, heightDp = 360)
@Composable
private fun HomePreview() {
    SsukSsukApp(previewState())
}

@Preview(name = "쑥쑥이", widthDp = 200, heightDp = 240, showBackground = true)
@Composable
private fun MascotPreview() {
    SsukSsukTheme {
        Mascot(Modifier.fillMaxSize(), action = MascotAction.WAVE)
    }
}

@Preview(name = "표정", widthDp = 120, heightDp = 120, showBackground = true)
@Composable
private fun EmotionFacePreview() {
    SsukSsukTheme {
        EmotionFace(Emotion.SURPRISED, Modifier.fillMaxSize())
    }
}
