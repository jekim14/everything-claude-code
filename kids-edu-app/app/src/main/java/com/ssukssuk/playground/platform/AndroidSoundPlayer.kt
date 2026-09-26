package com.ssukssuk.playground.platform

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.ssukssuk.playground.audio.ToneSynth
import com.ssukssuk.playground.audio.WavEncoder
import com.ssukssuk.playground.core.Sfx
import com.ssukssuk.playground.core.SoundPlayer
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

/**
 * 코드로 합성한 효과음·실로폰 음을 캐시 폴더에 WAV로 만들어 SoundPool로 재생합니다.
 * 소리 파일을 앱에 넣지 않아 용량이 작고, 짧은 소리를 겹쳐 빠르게 낼 수 있습니다.
 */
class AndroidSoundPlayer(context: Context) : SoundPlayer {
    private val pool: SoundPool = SoundPool.Builder()
        .setMaxStreams(8)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
        )
        .build()

    private val effectIds = ConcurrentHashMap<Sfx, Int>()
    private val noteIds = ConcurrentHashMap<Int, Int>()

    override var enabled: Boolean = true

    init {
        val dir = File(context.cacheDir, "sounds").apply { mkdirs() }
        thread(name = "ssukssuk-sound-synth", isDaemon = true) {
            // 실로폰 음을 먼저 준비해 첫 연주가 늦지 않도록 합니다.
            ToneSynth.NOTE_FREQUENCIES.indices.forEach { i ->
                noteIds[i] = load(File(dir, "note_$i.wav"), ToneSynth.xylophoneNote(i))
            }
            Sfx.entries.forEach { sfx ->
                effectIds[sfx] = load(File(dir, "sfx_${sfx.name.lowercase()}.wav"), ToneSynth.effect(sfx))
            }
        }
    }

    private fun load(file: File, samples: ShortArray): Int {
        file.writeBytes(WavEncoder.encode(samples))
        return pool.load(file.absolutePath, 1)
    }

    override fun play(sfx: Sfx) {
        if (!enabled) return
        effectIds[sfx]?.let { pool.play(it, 1f, 1f, 1, 0, 1f) }
    }

    override fun playNote(index: Int) {
        noteIds[index]?.let { pool.play(it, 1f, 1f, 2, 0, 1f) }
    }

    fun release() = pool.release()
}
