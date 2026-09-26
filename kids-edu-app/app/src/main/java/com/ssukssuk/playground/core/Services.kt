package com.ssukssuk.playground.core

/** 안내 음성(TTS). 글을 읽지 못하는 유아도 혼자 놀 수 있도록 모든 안내를 소리로 들려줍니다. */
interface Speaker {
    var enabled: Boolean

    /** 한국어 음성 엔진을 사용할 수 있는지 여부 */
    val isAvailable: Boolean

    /** 이전 안내를 멈추고 [text]를 읽습니다. */
    fun speak(text: String)

    fun stop()
}

/** 짧은 효과음 종류 */
enum class Sfx { TAP, POP, CORRECT, WRONG, FLIP, CHEER, STAR }

interface SoundPlayer {
    /** 효과음 켜기/끄기. 실로폰 음은 놀이 자체이므로 이 설정과 관계없이 재생합니다. */
    var enabled: Boolean

    fun play(sfx: Sfx)

    /** 실로폰 음 (0 = 낮은 도 … 7 = 높은 도) */
    fun playNote(index: Int)
}

class Services(
    val speaker: Speaker,
    val sound: SoundPlayer,
)

/** 미리보기·테스트용 무음 구현 */
object SilentSpeaker : Speaker {
    override var enabled: Boolean = true
    override val isAvailable: Boolean = false
    override fun speak(text: String) = Unit
    override fun stop() = Unit
}

object SilentSoundPlayer : SoundPlayer {
    override var enabled: Boolean = true
    override fun play(sfx: Sfx) = Unit
    override fun playNote(index: Int) = Unit
}
