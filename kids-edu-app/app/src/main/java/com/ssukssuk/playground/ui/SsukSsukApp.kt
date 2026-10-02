package com.ssukssuk.playground.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ssukssuk.playground.core.Game
import com.ssukssuk.playground.content.Phrases
import com.ssukssuk.playground.content.TalkCards
import com.ssukssuk.playground.ui.components.CelebrationOverlay
import com.ssukssuk.playground.ui.components.RoundResult
import com.ssukssuk.playground.ui.components.LocalServices
import com.ssukssuk.playground.ui.games.GameContent
import com.ssukssuk.playground.ui.games.GameEnv
import com.ssukssuk.playground.ui.screens.HomeScreen
import com.ssukssuk.playground.ui.screens.OnboardingScreen
import com.ssukssuk.playground.ui.screens.ParentGateScreen
import com.ssukssuk.playground.ui.screens.ParentScreen
import com.ssukssuk.playground.ui.screens.RestScreen
import com.ssukssuk.playground.ui.screens.StickerBookScreen
import com.ssukssuk.playground.ui.theme.SsukSsukTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun SsukSsukApp(appState: AppState) {
    SsukSsukTheme {
        CompositionLocalProvider(LocalServices provides appState.services) {
            LaunchedEffect(appState.isForeground) {
                if (!appState.isForeground) return@LaunchedEffect
                while (true) {
                    delay(1000)
                    appState.tick()
                }
            }
            AnimatedContent(
                targetState = appState.screen,
                transitionSpec = {
                    (fadeIn(tween(320)) + scaleIn(initialScale = 0.96f, animationSpec = tween(320))) togetherWith
                        fadeOut(tween(180))
                },
                label = "screen",
            ) { screen ->
                when (screen) {
                    Screen.Onboarding -> OnboardingScreen(onDone = appState::finishOnboarding)
                    Screen.Home -> HomeScreen(appState)
                    is Screen.Play -> GameHost(screen.game, appState)
                    Screen.Stickers -> StickerBookScreen(appState)
                    is Screen.Gate -> ParentGateScreen(
                        onSolved = { appState.onGateSolved(screen.purpose) },
                        onCancel = appState::back,
                    )
                    Screen.Parent -> ParentScreen(appState)
                    Screen.Rest -> RestScreen(
                        reason = appState.restReason ?: RestReason.BREAK,
                        childName = appState.childName,
                        onParent = { appState.navigate(Screen.Gate(GatePurpose.END_REST)) },
                    )
                }
            }
        }
    }
}

/** 놀이 화면을 띄우고, 끝나면 칭찬·깜짝 선물·대화 카드를 보여 줍니다. */
@Composable
private fun GameHost(game: Game, appState: AppState) {
    val services = LocalServices.current
    var session by remember { mutableIntStateOf(0) }
    var result by remember { mutableStateOf<RoundResult?>(null) }
    val onHome = {
        services.speaker.stop()
        appState.goHome()
    }
    Box(Modifier.fillMaxSize()) {
        key(session) {
            val env = remember {
                val random = Random(System.nanoTime())
                GameEnv(
                    game = game,
                    difficulty = appState.difficulty(game),
                    services = services,
                    random = random,
                    onHome = onHome,
                    onComplete = { score, total ->
                        val completion = appState.completeGame(game, score, total)
                        result = RoundResult(
                            game = game,
                            headline = Phrases.roundHeadline(hadRetry = score < total, freePlay = total == 0),
                            giftChoices = completion.giftChoices,
                            talkCard = TalkCards.forGame(game).randomOrNull(random),
                        )
                    },
                )
            }
            GameContent(env)
        }
        (result ?: appState.demoResult)?.let { current ->
            CelebrationOverlay(
                result = current,
                childName = appState.childName,
                onClaim = { appState.claimSticker(it) },
                onAgain = {
                    result = null
                    appState.demoResult = null
                    if (appState.restDue) appState.goHome() else session++
                },
                onHome = {
                    appState.demoResult = null
                    onHome()
                },
            )
        }
    }
}
