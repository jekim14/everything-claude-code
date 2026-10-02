package com.ssukssuk.playground.platform

import android.content.Context
import android.speech.tts.TextToSpeech
import com.ssukssuk.playground.core.Speaker
import java.util.Locale

/**
 * 기기의 음성 합성(TTS) 엔진으로 한국어 안내를 읽어 줍니다.
 * 유아가 알아듣기 쉽도록 조금 천천히, 조금 높은 목소리로 설정합니다.
 */
class AndroidSpeaker(context: Context) : Speaker, TextToSpeech.OnInitListener {
    private val tts = TextToSpeech(context.applicationContext, this)

    @Volatile
    private var ready = false

    @Volatile
    private var pending: String? = null

    private var utteranceId = 0

    override var enabled: Boolean = true
        set(value) {
            field = value
            if (!value) stop()
        }

    override val isAvailable: Boolean
        get() = ready

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) return
        val result = tts.setLanguage(Locale.KOREA)
        ready = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
        if (!ready) return
        tts.setSpeechRate(0.9f)
        tts.setPitch(1.1f)
        pending?.let { speak(it) }
        pending = null
    }

    override fun speak(text: String) {
        if (!enabled || text.isBlank()) return
        if (!ready) {
            pending = text
            return
        }
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ssukssuk-${utteranceId++}")
    }

    override fun stop() {
        pending = null
        if (ready) tts.stop()
    }

    fun shutdown() {
        ready = false
        tts.stop()
        tts.shutdown()
    }
}
