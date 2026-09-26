package com.ssukssuk.playground.ui.games

import androidx.compose.runtime.Composable
import com.ssukssuk.playground.content.Phrases
import com.ssukssuk.playground.core.Difficulty
import com.ssukssuk.playground.core.Game
import com.ssukssuk.playground.core.Services
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.core.SoundPlayer
import com.ssukssuk.playground.core.Speaker
import kotlin.random.Random

/** 각 놀이 화면에 전달되는 환경: 난이도, 음성·효과음, 끝났을 때의 콜백 */
class GameEnv(
    val game: Game,
    val difficulty: Difficulty,
    private val services: Services,
    val random: Random,
    val onHome: () -> Unit,
    private val onComplete: (score: Int, total: Int) -> Unit,
) {
    private var completed = false

    val speaker: Speaker get() = services.speaker
    val sound: SoundPlayer get() = services.sound

    fun say(text: String) = speaker.speak(text)

    fun play(sfx: Sfx) = sound.play(sfx)

    fun praise(): String = Phrases.praise.random(random)

    fun retry(): String = Phrases.retry.random(random)

    /**
     * 놀이를 끝냅니다. 한 번만 호출됩니다.
     * @param score 첫 시도에 맞힌 수 (자유 놀이는 0)
     * @param total 문제 수 (자유 놀이는 0)
     */
    fun complete(score: Int = 0, total: Int = 0) {
        if (completed) return
        completed = true
        onComplete(score, total)
    }
}

@Composable
fun GameContent(env: GameEnv) {
    when (env.game) {
        Game.HANGUL -> HangulGame(env)
        Game.COUNTING -> CountingGame(env)
        Game.BALLOON -> BalloonGame(env)
        Game.SHAPES -> ShapeGame(env)
        Game.MEMORY -> MemoryGame(env)
        Game.PATTERN -> PatternGame(env)
        Game.EMOTION -> EmotionGame(env)
        Game.DRAWING -> DrawingGame(env)
        Game.XYLOPHONE -> XylophoneGame(env)
        Game.MOVEMENT -> MovementGame(env)
    }
}
