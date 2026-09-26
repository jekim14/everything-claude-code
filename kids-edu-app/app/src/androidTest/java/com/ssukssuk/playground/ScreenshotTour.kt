package com.ssukssuk.playground

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.ssukssuk.playground.core.Game
import com.ssukssuk.playground.ui.GatePurpose
import com.ssukssuk.playground.ui.Screen
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * 실제 기기(에뮬레이터)에서 모든 화면을 차례로 띄워 캡처합니다.
 * 각 화면이 오류 없이 그려지는지 확인하는 스모크 테스트이기도 합니다.
 * 캡처는 앱 내부 저장소 files/screenshots 에 JPEG로 저장됩니다.
 */
@RunWith(AndroidJUnit4::class)
class ScreenshotTour {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val outDir = File(context.filesDir, "screenshots")

    @Test
    fun captureEveryScreen() {
        context.getSharedPreferences("ssukssuk_playground", Context.MODE_PRIVATE).edit().clear().commit()
        outDir.deleteRecursively()
        outDir.mkdirs()

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

        shot("00_onboarding")
        go {
            it.appState.chooseAge(5)
            // 소프트웨어 렌더링 에뮬레이터는 느려서 캡처 도중 쉬는 시간이 되지 않도록 알림을 끕니다.
            it.appState.updateSettings(it.appState.settings.copy(restMinutes = 0))
        }
        shot("01_home", waitMillis = 3500)
        Game.entries.forEachIndexed { i, game ->
            go { it.appState.navigate(Screen.Play(game)) }
            shot("%02d_%s".format(i + 2, game.id), waitMillis = 4000)
            go { it.appState.goHome() }
        }
        go { it.appState.navigate(Screen.Stickers) }
        shot("12_stickers")
        go { it.appState.navigate(Screen.Gate(GatePurpose.PARENT_AREA)) }
        shot("13_parent_gate")
        go { it.appState.onGateSolved(GatePurpose.PARENT_AREA) }
        shot("14_parent")
        go { it.appState.navigate(Screen.Rest) }
        shot("15_rest")
        go { it.appState.chooseAge(4) }
        go { it.appState.navigate(Screen.Play(Game.MEMORY)) }
        shot("16_memory_age4", waitMillis = 4000)
        go { it.finish() }
        assertTrue((outDir.list()?.size ?: 0) >= 17)
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

    private fun shot(name: String, waitMillis: Long = 2500) {
        Thread.sleep(waitMillis)
        val screen = instrumentation.uiAutomation.takeScreenshot()
            ?: error("screenshot failed: $name")
        val width = 1200
        val scaled = Bitmap.createScaledBitmap(screen, width, screen.height * width / screen.width, true)
        File(outDir, "$name.jpg").outputStream().use { scaled.compress(Bitmap.CompressFormat.JPEG, 82, it) }
    }
}
