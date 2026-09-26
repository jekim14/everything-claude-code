package com.ssukssuk.playground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.ssukssuk.playground.core.ProgressRepository
import com.ssukssuk.playground.core.Services
import com.ssukssuk.playground.platform.AndroidSoundPlayer
import com.ssukssuk.playground.platform.AndroidSpeaker
import com.ssukssuk.playground.platform.SharedPrefsStore
import com.ssukssuk.playground.ui.AppState
import com.ssukssuk.playground.ui.SsukSsukApp

class MainActivity : ComponentActivity() {
    private lateinit var speaker: AndroidSpeaker
    private lateinit var sound: AndroidSoundPlayer

    /** 화면 캡처 계측 테스트에서 화면을 바꾸기 위해 모듈 안에 공개합니다. */
    internal lateinit var appState: AppState
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemBars()

        speaker = AndroidSpeaker(this)
        sound = AndroidSoundPlayer(this)
        val repository = ProgressRepository(SharedPrefsStore(getSharedPreferences(PREFS_NAME, MODE_PRIVATE)))
        appState = AppState(repository, Services(speaker, sound))

        setContent {
            BackHandler(enabled = appState.canGoBack) { appState.back() }
            SsukSsukApp(appState)
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
        appState.updateForeground(true)
    }

    override fun onPause() {
        appState.updateForeground(false)
        super.onPause()
    }

    override fun onDestroy() {
        speaker.shutdown()
        sound.release()
        super.onDestroy()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    /** 아이가 실수로 다른 앱으로 나가지 않도록 상태 표시줄과 내비게이션 바를 숨깁니다. */
    private fun hideSystemBars() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    private companion object {
        const val PREFS_NAME = "ssukssuk_playground"
    }
}
