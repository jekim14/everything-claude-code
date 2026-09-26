package com.ssukssuk.playground.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ssukssuk.playground.content.Sticker
import com.ssukssuk.playground.core.Completion
import com.ssukssuk.playground.core.Difficulty
import com.ssukssuk.playground.core.Game
import com.ssukssuk.playground.core.GameRecord
import com.ssukssuk.playground.core.ProgressRepository
import com.ssukssuk.playground.core.Services
import com.ssukssuk.playground.core.Settings
import com.ssukssuk.playground.ui.components.RoundResult

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

/** 쉬는 화면이 나온 까닭 */
enum class RestReason {
    /** 이어서 [Settings.restMinutes]분 놀았어요: 잠깐 쉬고 이어서 놀 수 있어요. */
    BREAK,

    /** 오늘 놀 시간([Settings.dailyLimitMinutes])을 다 썼어요. */
    DAY_DONE,
}

/**
 * 앱 전체 상태: 현재 화면, 설정, 스티커, 놀이 시간.
 *
 * 쉬는 시간은 놀이 도중에 끊지 않고, 놀이를 마치거나 처음 화면으로 돌아올 때 보여 줍니다.
 * 앱이 먼저 끝을 알려 주면 아이가 화면을 내려놓는 전환이 더 순조롭습니다(Hiniker 외 2016).
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

    var stickerCounts by mutableStateOf(repository.stickerCounts())
        private set

    var records by mutableStateOf(repository.records())
        private set

    /** 쉬는 시간 이후 이어서 논 시간(초) */
    var sessionSeconds by mutableIntStateOf(0)
        private set

    /** 오늘 논 시간(초). 화면에 보이는 해님 막대가 이 값을 읽습니다. */
    var todaySeconds by mutableIntStateOf(repository.playSecondsToday())
        private set

    var isForeground by mutableStateOf(true)
        private set

    /** 홈에서 이번에 인사를 했는지 (한 번 켤 때마다 이름을 불러 인사합니다) */
    var greeted = false

    /** 화면 캡처 테스트에서 축하 화면을 바로 띄울 때만 씁니다. */
    internal var demoResult by mutableStateOf<RoundResult?>(null)

    private var unsavedSeconds = 0
    private var backgroundSince: Long? = null

    init {
        applyServiceSettings()
    }

    val childName: String get() = settings.childName

    val canGoBack: Boolean get() = screen != Screen.Home && screen != Screen.Onboarding

    /** 오늘 놀 수 있는 시간(초). 제한이 없으면 null */
    val todayLimitSeconds: Int?
        get() = settings.dailyLimitMinutes.takeIf { it > 0 }?.let { (it + repository.extraMinutesToday()) * 60 }

    val restReason: RestReason?
        get() = when {
            todayLimitSeconds?.let { todaySeconds >= it } == true -> RestReason.DAY_DONE
            settings.restMinutes > 0 && sessionSeconds >= settings.restMinutes * 60 -> RestReason.BREAK
            else -> null
        }

    val restDue: Boolean get() = restReason != null

    /** 쉬는 시간까지 남은 시간(초). 둘 다 꺼져 있으면 null */
    val secondsUntilRest: Int?
        get() {
            val day = todayLimitSeconds?.let { it - todaySeconds }
            val session = settings.restMinutes.takeIf { it > 0 }?.let { it * 60 - sessionSeconds }
            return listOfNotNull(day, session).minOrNull()?.coerceAtLeast(0)
        }

    fun difficulty(game: Game): Difficulty = repository.difficulty(game, settings.age)

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

    /** 첫 실행: 나이(와 이름)를 정하고 홈으로 갑니다. */
    fun finishOnboarding(age: Int, childName: String) {
        updateSettings(settings.copy(age = age, childName = Settings.cleanName(childName)))
        screen = Screen.Home
    }

    fun updateSettings(newSettings: Settings) {
        val cleaned = newSettings.copy(childName = Settings.cleanName(newSettings.childName))
        if (cleaned.age != settings.age) repository.resetStages()
        settings = cleaned
        repository.saveSettings(cleaned)
        applyServiceSettings()
    }

    /** 놀이를 마쳤습니다. 기록을 남기고 다음 판 단계와 깜짝 선물 후보를 돌려줍니다. */
    fun completeGame(game: Game, score: Int, total: Int): Completion {
        val completion = repository.completeGame(game, score, total, settings.age)
        records = repository.records()
        return completion
    }

    /** 아이가 고른 깜짝 선물을 스티커 책에 붙입니다. 처음 받은 스티커면 true */
    fun claimSticker(sticker: Sticker): Boolean {
        val isNew = repository.claimSticker(sticker)
        stickerCounts = repository.stickerCounts()
        return isNew
    }

    fun onGateSolved(purpose: GatePurpose) {
        when (purpose) {
            GatePurpose.PARENT_AREA -> screen = Screen.Parent
            GatePurpose.END_REST -> {
                if (restReason == RestReason.DAY_DONE) {
                    // 보호자가 허락하면 오늘 15분 더 놀 수 있습니다.
                    repository.addExtraMinutesToday(EXTRA_MINUTES)
                }
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
        todaySeconds++
        unsavedSeconds++
        if (unsavedSeconds >= 15) flushPlayTime()
        if (restDue && screen == Screen.Home) screen = Screen.Rest
    }

    fun updateForeground(foreground: Boolean) {
        if (foreground == isForeground) return
        isForeground = foreground
        if (!foreground) {
            backgroundSince = clock()
            services.speaker.stop()
            flushPlayTime()
        } else {
            val since = backgroundSince
            backgroundSince = null
            todaySeconds = repository.playSecondsToday()
            // 10분 넘게 앱을 떠나 있었다면 이미 쉬었다고 보고 시간을 새로 셉니다.
            if (since != null && clock() - since >= BREAK_RESET_MILLIS) {
                sessionSeconds = 0
                greeted = false
                if (screen == Screen.Rest && restReason == null) screen = Screen.Home
            }
        }
    }

    fun flushPlayTime() {
        repository.addPlaySeconds(unsavedSeconds)
        unsavedSeconds = 0
    }

    fun playSecondsToday(): Int = todaySeconds

    fun totalPlaySeconds(): Int = repository.totalPlaySeconds() + unsavedSeconds

    fun record(game: Game): GameRecord = records[game] ?: GameRecord()

    fun stage(game: Game): Int = repository.stage(game, settings.age)

    fun resetProgress() {
        unsavedSeconds = 0
        repository.resetProgress()
        stickerCounts = repository.stickerCounts()
        records = repository.records()
        todaySeconds = repository.playSecondsToday()
        sessionSeconds = 0
    }

    private fun applyServiceSettings() {
        services.speaker.enabled = settings.voiceOn
        services.speaker.useName(settings.childName)
        services.sound.enabled = settings.soundOn
        if (!settings.voiceOn) services.speaker.stop()
    }

    companion object {
        private const val BREAK_RESET_MILLIS = 10 * 60 * 1000L
        const val EXTRA_MINUTES = 15
    }
}
