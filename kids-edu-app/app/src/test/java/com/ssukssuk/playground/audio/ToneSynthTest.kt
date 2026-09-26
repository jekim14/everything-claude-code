package com.ssukssuk.playground.audio

import com.ssukssuk.playground.core.Sfx
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class ToneSynthTest {
    @Test
    fun `effects are short and volume limited`() {
        Sfx.entries.forEach { sfx ->
            val samples = ToneSynth.effect(sfx)
            assertTrue("$sfx is empty", samples.isNotEmpty())
            assertTrue("$sfx is too long", samples.size < ToneSynth.SAMPLE_RATE)
            val peak = samples.maxOf { abs(it.toInt()) }
            assertTrue("$sfx is silent", peak > 1000)
            assertTrue("$sfx is too loud: $peak", peak <= Short.MAX_VALUE * 0.51)
        }
    }

    @Test
    fun `renders all eight xylophone notes`() {
        (0..7).forEach { i ->
            val samples = ToneSynth.xylophoneNote(i)
            assertTrue(samples.size > ToneSynth.SAMPLE_RATE / 2)
            assertTrue(samples.maxOf { abs(it.toInt()) } > 1000)
        }
    }

    @Test
    fun `wav header is valid`() {
        val samples = ShortArray(100) { (it * 100).toShort() }
        val wav = WavEncoder.encode(samples, 22050)
        assertEquals(WavEncoder.HEADER_SIZE + 200, wav.size)
        assertEquals("RIFF", String(wav, 0, 4, Charsets.US_ASCII))
        assertEquals("WAVE", String(wav, 8, 4, Charsets.US_ASCII))
        assertEquals("data", String(wav, 36, 4, Charsets.US_ASCII))
        fun intAt(offset: Int) = (wav[offset].toInt() and 0xFF) or
            ((wav[offset + 1].toInt() and 0xFF) shl 8) or
            ((wav[offset + 2].toInt() and 0xFF) shl 16) or
            ((wav[offset + 3].toInt() and 0xFF) shl 24)
        assertEquals(36 + 200, intAt(4))
        assertEquals(22050, intAt(24))
        assertEquals(200, intAt(40))
        // 두 번째 샘플(100)은 리틀 엔디언으로 저장된다
        assertEquals(100, (wav[46].toInt() and 0xFF) or ((wav[47].toInt() and 0xFF) shl 8))
    }
}
