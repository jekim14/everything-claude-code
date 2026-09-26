package com.ssukssuk.playground

import android.content.Context
import android.content.Intent
import android.os.ParcelFileDescriptor
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.ssukssuk.playground.content.Phrases
import com.ssukssuk.playground.content.Stickers
import com.ssukssuk.playground.content.TalkCards
import com.ssukssuk.playground.core.Game
import com.ssukssuk.playground.ui.GatePurpose
import com.ssukssuk.playground.ui.Screen
import com.ssukssuk.playground.ui.components.RoundResult
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

/**
 * 실제 기기(에뮬레이터)에서 모든 화면을 차례로 띄워 캡처합니다.
 * 각 화면이 오류 없이 그려지는지 확인하는 스모크 테스트이기도 합니다.
 * 캡처는 기기의 /data/local/tmp/ssukssuk-screens 에 PNG로 저장됩니다.
 */
@RunWith(AndroidJUnit4::class)
class ScreenshotTour {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun captureEveryScreen() {
        context.getSharedPreferences("ssukssuk_playground", Context.MODE_PRIVATE).edit().clear().commit()
        shell("rm -rf $DEVICE_DIR")
        shell("mkdir -p $DEVICE_DIR")

        // 애니메이션이 쉬지 않고 도는 앱이라 '메인 스레드가 한가해질 때'를 기다리는
        // ActivityScenario 대신, 액티비티를 직접 띄우고 메인 스레드에서 바로 실행합니다.
        context.startActivity(
            Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK),
        )
        val activity = awaitResumedActivity()
        fun go(action: (MainActivity) -> Unit) {
            instrumentation.runOnMainSync { action(activity) }
        }

        shot("00_onboarding", waitMillis = 6000)
        go {
            // 캡처용 이름은 예시 이름을 씁니다.
            it.appState.finishOnboarding(5, "하늘")
            // 소프트웨어 렌더링 에뮬레이터는 느려서 캡처 도중 쉬는 시간이 되지 않도록 알림과 하루 제한을 끕니다.
            it.appState.updateSettings(it.appState.settings.copy(restMinutes = 0, dailyLimitMinutes = 0))
        }
        shot("01_home", waitMillis = 3500)
        Game.entries.forEachIndexed { i, game ->
            go { it.appState.navigate(Screen.Play(game)) }
            shot("%02d_%s".format(i + 2, game.id), waitMillis = 4000)
            go { it.appState.goHome() }
        }
        var n = Game.entries.size + 2
        go {
            it.appState.navigate(Screen.Play(Game.COUNTING))
            it.appState.demoResult = RoundResult(
                game = Game.COUNTING,
                headline = Phrases.roundHeadline(hadRetry = true, freePlay = false),
                giftChoices = Stickers.all.take(3),
                talkCard = TalkCards.forGame(Game.COUNTING).first(),
            )
        }
        shot("%02d_celebration".format(n++), waitMillis = 3500)
        go {
            it.appState.demoResult = null
            it.appState.goHome()
            it.appState.navigate(Screen.Stickers)
        }
        shot("%02d_stickers".format(n++))
        go { it.appState.navigate(Screen.Gate(GatePurpose.PARENT_AREA)) }
        shot("%02d_parent_gate".format(n++))
        go { it.appState.onGateSolved(GatePurpose.PARENT_AREA) }
        shot("%02d_parent".format(n++))
        go { it.appState.navigate(Screen.Rest) }
        shot("%02d_rest_break".format(n++))
        // 오늘 놀 시간을 다 쓴 상태로 만들어 '오늘 놀이 끝' 화면을 띄웁니다.
        go { it.appState.updateForeground(false) }
        context.getSharedPreferences("ssukssuk_playground", Context.MODE_PRIVATE).edit()
            .putInt("play_day", LocalDate.now().toEpochDay().toInt())
            .putInt("play_seconds_today", 60 * 60)
            .commit()
        go {
            it.appState.updateForeground(true)
            it.appState.updateSettings(it.appState.settings.copy(dailyLimitMinutes = 30))
            it.appState.onGateSolved(GatePurpose.PARENT_AREA)
            it.appState.goHome()
        }
        shot("%02d_rest_day_done".format(n++))
        go {
            it.appState.updateSettings(it.appState.settings.copy(dailyLimitMinutes = 0, age = 4))
            it.appState.goHome()
            it.appState.navigate(Screen.Play(Game.SYLLABLE))
        }
        shot("%02d_syllable_age4".format(n++), waitMillis = 4000)
        go { it.finish() }
    }

    private fun awaitResumedActivity(): MainActivity {
        repeat(300) {
            var found: MainActivity? = null
            instrumentation.runOnMainSync {
                found = ActivityLifecycleMonitorRegistry.getInstance()
                    .getActivitiesInStage(Stage.RESUMED)
                    .filterIsInstance<MainActivity>()
                    .firstOrNull()
            }
            found?.let { return it }
            Thread.sleep(200)
        }
        error("MainActivity가 화면에 나타나지 않았습니다.")
    }

    /** 셸의 screencap으로 화면을 저장합니다. 크기 조정은 CI 호스트에서 합니다. */
    private fun shot(name: String, waitMillis: Long = 2500) {
        Thread.sleep(waitMillis)
        shell("screencap -p $DEVICE_DIR/$name.png")
    }

    /** 명령이 끝날 때까지 기다립니다. */
    private fun shell(command: String) {
        ParcelFileDescriptor.AutoCloseInputStream(instrumentation.uiAutomation.executeShellCommand(command)).use {
            it.readBytes()
        }
    }

    private companion object {
        const val DEVICE_DIR = "/data/local/tmp/ssukssuk-screens"
    }
}
