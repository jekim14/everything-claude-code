package com.ssukssuk.playground.audio

import com.ssukssuk.playground.core.Sfx
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

/**
 * 효과음과 실로폰 소리를 코드로 직접 합성합니다.
 * 외부 음원 파일이 없어 저작권 걱정이 없고, 앱 용량도 작게 유지됩니다.
 * 유아의 청각을 고려해 음량을 낮게(최대 약 -6 dBFS) 유지하고 날카로운 고음을 피합니다.
 */
object ToneSynth {
    const val SAMPLE_RATE = 22050

    /** 도(C5)부터 높은 도(C6)까지 */
    val NOTE_FREQUENCIES = doubleArrayOf(523.25, 587.33, 659.25, 698.46, 783.99, 880.00, 987.77, 1046.50)

    private const val PEAK = 0.5

    fun xylophoneNote(index: Int): ShortArray {
        val f = NOTE_FREQUENCIES[index.coerceIn(0, NOTE_FREQUENCIES.lastIndex)]
        return render(0.9) { t ->
            val attack = min(1.0, t / 0.004)
            attack * (
                sin(2 * PI * f * t) * exp(-t / 0.32) +
                    0.28 * sin(2 * PI * f * 4.0 * t) * exp(-t / 0.06) +
                    0.10 * sin(2 * PI * f * 2.0 * t) * exp(-t / 0.15)
                )
        }
    }

    fun effect(sfx: Sfx): ShortArray = when (sfx) {
        Sfx.TAP -> render(0.07) { t -> sin(2 * PI * 740 * t) * envelope(t, 0.07, 0.003) }
        Sfx.FLIP -> render(0.12) { t ->
            val f = 520 + 900 * (t / 0.12)
            sin(2 * PI * f * t) * envelope(t, 0.12, 0.005) * 0.8
        }
        Sfx.POP -> {
            val noise = Random(7)
            render(0.16) { t ->
                val body = sin(2 * PI * (420 - 1400 * t) * t) * exp(-t / 0.03)
                val burst = (noise.nextDouble() * 2 - 1) * exp(-t / 0.012)
                (0.7 * body + 0.5 * burst) * envelope(t, 0.16, 0.001)
            }
        }
        Sfx.CORRECT -> arpeggio(listOf(783.99, 987.77, 1174.66), noteLength = 0.11, tail = 0.35)
        Sfx.CHEER -> arpeggio(listOf(523.25, 659.25, 783.99, 1046.50), noteLength = 0.12, tail = 0.6)
        Sfx.STAR -> arpeggio(listOf(1046.50, 1318.51, 1567.98), noteLength = 0.06, tail = 0.3, volume = 0.6)
        Sfx.WRONG -> render(0.32) { t ->
            // 부드러운 '뿌웅' 소리: 틀렸다는 느낌을 주되 겁먹지 않도록 낮고 짧게
            val f = 330 - 110 * (t / 0.32)
            (sin(2 * PI * f * t) + 0.3 * sin(2 * PI * f * 2 * t)) * envelope(t, 0.32, 0.01) * 0.55
        }
    }

    private fun arpeggio(
        freqs: List<Double>,
        noteLength: Double,
        tail: Double,
        volume: Double = 1.0,
    ): ShortArray {
        val total = noteLength * (freqs.size - 1) + tail
        return render(total) { t ->
            var sum = 0.0
            freqs.forEachIndexed { i, f ->
                val start = i * noteLength
                if (t >= start) {
                    val local = t - start
                    val attack = min(1.0, local / 0.004)
                    sum += attack * sin(2 * PI * f * local) * exp(-local / 0.18) * 0.55
                }
            }
            sum * volume * min(1.0, (total - t) / 0.02)
        }
    }

    /** 짧은 어택과 끝부분 페이드아웃 */
    private fun envelope(t: Double, duration: Double, attack: Double): Double {
        val a = if (attack <= 0) 1.0 else min(1.0, t / attack)
        val release = min(1.0, (duration - t) / (duration * 0.35))
        return a * release.coerceAtLeast(0.0)
    }

    private inline fun render(durationSec: Double, wave: (Double) -> Double): ShortArray {
        val length = (durationSec * SAMPLE_RATE).roundToInt()
        return ShortArray(length) { i ->
            val t = i.toDouble() / SAMPLE_RATE
            val v = (wave(t) * PEAK).coerceIn(-1.0, 1.0)
            (v * Short.MAX_VALUE).roundToInt().toShort()
        }
    }
}

/** 16비트 모노 PCM을 WAV 파일 바이트로 변환합니다. */
object WavEncoder {
    const val HEADER_SIZE = 44

    fun encode(samples: ShortArray, sampleRate: Int = ToneSynth.SAMPLE_RATE): ByteArray {
        val dataSize = samples.size * 2
        val out = ByteArray(HEADER_SIZE + dataSize)
        var p = 0
        fun writeAscii(s: String) = s.forEach { out[p++] = it.code.toByte() }
        fun writeInt(v: Int) {
            out[p++] = (v and 0xFF).toByte()
            out[p++] = (v shr 8 and 0xFF).toByte()
            out[p++] = (v shr 16 and 0xFF).toByte()
            out[p++] = (v shr 24 and 0xFF).toByte()
        }
        fun writeShort(v: Int) {
            out[p++] = (v and 0xFF).toByte()
            out[p++] = (v shr 8 and 0xFF).toByte()
        }

        writeAscii("RIFF")
        writeInt(36 + dataSize)
        writeAscii("WAVE")
        writeAscii("fmt ")
        writeInt(16) // PCM fmt chunk size
        writeShort(1) // PCM
        writeShort(1) // mono
        writeInt(sampleRate)
        writeInt(sampleRate * 2) // byte rate
        writeShort(2) // block align
        writeShort(16) // bits per sample
        writeAscii("data")
        writeInt(dataSize)
        for (s in samples) writeShort(s.toInt())
        return out
    }
}
