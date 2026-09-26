package com.ssukssuk.playground.platform

import android.content.Context
import android.content.res.AssetManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.ssukssuk.playground.content.VoiceKey
import com.ssukssuk.playground.core.Speaker
import java.io.IOException

/**
 * 미리 만든 고품질 음성 파일(assets/voice, assets/voice_personal)이 있으면 그 음성으로,
 * 없으면 기기의 음성 합성([fallback])으로 읽습니다.
 *
 * 한 번의 안내 안에서 목소리가 바뀌지 않도록, 문장 중 하나라도 파일이 없으면 안내 전체를 음성 합성으로 읽습니다.
 * 단, 빠진 문장이 아이 이름을 부르는 말뿐이면 그 부분만 빼고 녹음된 음성으로 읽습니다.
 */
class ClipSpeaker(context: Context, private val fallback: Speaker) : Speaker {
    private val assets: AssetManager = context.applicationContext.assets
    private val clips: Map<String, String> = indexClips()
    private val main = Handler(Looper.getMainLooper())
    private val attributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
        .build()

    private var player: MediaPlayer? = null
    private var generation = 0
    private var childName = ""

    override val recordedClips: Int get() = clips.size

    override var enabled: Boolean = true
        set(value) {
            field = value
            fallback.enabled = value
            if (!value) stop()
        }

    override val isAvailable: Boolean
        get() = clips.isNotEmpty() || fallback.isAvailable

    override fun useName(name: String) {
        childName = name.trim()
        fallback.useName(name)
    }

    override fun speak(text: String) {
        if (!enabled || text.isBlank()) return
        stop()
        val segments = VoiceKey.segments(text)
        val paths = segments.map { clips[VoiceKey.fileName(it)] }
        val missing = segments.filterIndexed { i, _ -> paths[i] == null }
        val onlyNameMissing = childName.isNotEmpty() && missing.all { childName in it } && paths.any { it != null }
        if (missing.isNotEmpty() && !onlyNameMissing) {
            fallback.speak(text)
            return
        }
        playFrom(paths.filterNotNull(), 0, generation)
    }

    private fun playFrom(paths: List<String>, index: Int, gen: Int) {
        if (gen != generation || index >= paths.size) return
        val mp = MediaPlayer()
        try {
            assets.openFd(paths[index]).use { fd -> mp.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length) }
        } catch (e: IOException) {
            mp.release()
            playFrom(paths, index + 1, gen)
            return
        }
        mp.setAudioAttributes(attributes)
        mp.setOnPreparedListener { if (gen == generation) it.start() else release(it) }
        mp.setOnCompletionListener {
            release(it)
            main.postDelayed({ playFrom(paths, index + 1, gen) }, GAP_MILLIS)
        }
        mp.setOnErrorListener { failed, _, _ ->
            release(failed)
            main.post { playFrom(paths, index + 1, gen) }
            true
        }
        player = mp
        mp.prepareAsync()
    }

    private fun release(mp: MediaPlayer) {
        if (player === mp) player = null
        mp.release()
    }

    override fun stop() {
        generation++
        main.removeCallbacksAndMessages(null)
        player?.let {
            runCatching { it.stop() }
            it.release()
        }
        player = null
        fallback.stop()
    }

    private fun indexClips(): Map<String, String> {
        val result = HashMap<String, String>()
        // 이름이 든 파일이 같은 이름의 일반 파일보다 앞서도록 personal 을 나중에 넣습니다.
        for (dir in listOf(DIR_VOICE, DIR_PERSONAL)) {
            val names = runCatching { assets.list(dir) }.getOrNull().orEmpty()
            names.filter { it.startsWith("v_") }.forEach { result[it] = "$dir/$it" }
        }
        return result
    }

    private companion object {
        const val DIR_VOICE = "voice"
        const val DIR_PERSONAL = "voice_personal"

        /** 문장과 문장 사이 쉼 */
        const val GAP_MILLIS = 140L
    }
}
