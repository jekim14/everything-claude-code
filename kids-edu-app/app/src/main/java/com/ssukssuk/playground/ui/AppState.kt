package com.ssukssuk.playground.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ssukssuk.playground.core.Difficulty
import com.ssukssuk.playground.core.Game
import com.ssukssuk.playground.core.GameRecord
import com.ssukssuk.playground.core.ProgressRepository
import com.ssukssuk.playground.core.Reward
import com.ssukssuk.playground.core.Services
import com.ssukssuk.playground.core.Settings

sealed interface Screen {
    data object Onboarding : Screen
    data object Home : Screen
    data class Play(val game: Game) : Screen
    data object Stickers : Screen
    data class Gate(val purpose: GatePurpose) : Screen
    data object Parent : Screen
    data object Rest : Screen
}

enum class GatePurpose { PARENT_AREA, END_REST }

/**
 * 앱 전체 상태: 현재 화면, 설정, 스티커·별, 놀이 시간.
 *
 * 쉬는 시간 알림은 놀이 도중에 끊지 않고, 놀이를 마치거나 처음 화면으로 돌아올 때 보여 줍니다.
 */
@Stable
class AppState(
    private val repository: ProgressRepository,
    val services: Services,
    private val clock: () -> Long = { System.currentTimeMillis() },
) {
    var settings by mutableStateOf(repository.loadSettings())
        private set

    var screen by mutableStateOf<Screen>(if (settings.age == 0) Screen.Onboarding else Screen.Home)
        private set

    var stars by mutableIntStateOf(repository.totalStars())
        private set

    var stickerCounts by mutableStateOf(repository.stickerCounts())
        private set

    var records by mutableStateOf(repository.records())
        private set

    /** 쉬는 시간 이후 이어서 논 시간(초) */
    var sessionSeconds by mutableIntStateOf(0)
        private set

    var isForeground by mutableStateOf(true)
        private set

    private var unsavedSeconds = 0
    private var backgroundSince: Long? = null

    init {
        applyServiceSettings()
    }

    val difficulty: Difficulty get() = settings.difficulty

    val canGoBack: Boolean get() = screen != Screen.Home && screen != Screen.Onboarding

    val restDue: Boolean get() = settings.restMinutes > 0 && sessionSeconds >= settings.restMinutes * 60

    fun navigate(target: Screen) {
        screen = if (restDue && (target is Screen.Play || target == Screen.Home)) Screen.Rest else target
    }

    fun goHome() = navigate(Screen.Home)

    /** 시스템 뒤로 가기. 쉬는 시간 화면은 보호자 확인 없이 벗어날 수 없습니다. */
    fun back() {
        when (val current = screen) {
            Screen.Rest -> Unit
            is Screen.Gate -> screen = if (current.purpose == GatePurpose.END_REST) Screen.Rest else Screen.Home
            else -> goHome()
        }
    }

    fun chooseAge(age: Int) {
        updateSettings(settings.copy(age = age))
        screen = Screen.Home
    }

    fun updateSettings(newSettings: Settings) {
        settings = newSettings
        repository.saveSettings(newSettings)
        applyServiceSettings()
    }

    fun completeGame(game: Game, score: Int, total: Int): Reward {
        val reward = repository.completeGame(game, score, total)
        stars = reward.totalStars
        stickerCounts = repository.stickerCounts()
        records = repository.records()
        return reward
    }

    fun onGateSolved(purpose: GatePurpose) {
        when (purpose) {
            GatePurpose.PARENT_AREA -> screen = Screen.Parent
            GatePurpose.END_REST -> {
                sessionSeconds = 0
                screen = Screen.Home
            }
        }
    }

    /** 앱이 화면에 보이는 동안 1초마다 호출됩니다. */
    fun tick() {
        val counting = when (screen) {
            Screen.Rest, Screen.Parent, is Screen.Gate, Screen.Onboarding -> false
            else -> true
        }
        if (!counting) return
        sessionSeconds++
        unsavedSeconds++
        if (unsavedSeconds >= 15) flushPlayTime()
        if (restDue && screen == Screen.Home) screen = Screen.Rest
    }

    fun setForeground(foreground: Boolean) {
        if (foreground == isForeground) return
        isForeground = foreground
        if (!foreground) {
            backgroundSince = clock()
            services.speaker.stop()
            flushPlayTime()
        } else {
            val since = backgroundSince
            backgroundSince = null
            // 10분 넘게 앱을 떠나 있었다면 이미 쉬었다고 보고 시간을 새로 셉니다.
            if (since != null && clock() - since >= BREAK_RESET_MILLIS) {
                sessionSeconds = 0
                if (screen == Screen.Rest) screen = Screen.Home
            }
        }
    }

    fun flushPlayTime() {
        repository.addPlaySeconds(unsavedSeconds)
        unsavedSeconds = 0
    }

    fun playSecondsToday(): Int = repository.playSecondsToday() + unsavedSeconds

    fun totalPlaySeconds(): Int = repository.totalPlaySeconds() + unsavedSeconds

    fun record(game: Game): GameRecord = records[game] ?: GameRecord()

    fun resetProgress() {
        unsavedSeconds = 0
        repository.resetProgress()
        stars = repository.totalStars()
        stickerCounts = repository.stickerCounts()
        records = repository.records()
    }

    private fun applyServiceSettings() {
        services.speaker.enabled = settings.voiceOn
        services.sound.enabled = settings.soundOn
        if (!settings.voiceOn) services.speaker.stop()
    }

    companion object {
        private const val BREAK_RESET_MILLIS = 10 * 60 * 1000L
    }
}
