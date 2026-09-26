package com.ssukssuk.playground.content

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KoreanTest {
    @Test
    fun `detects final consonant`() {
        assertFalse(Korean.hasBatchim("사과"))
        assertTrue(Korean.hasBatchim("별"))
        assertTrue(Korean.hasBatchim("기역"))
        assertFalse(Korean.hasBatchim("ABC"))
    }

    @Test
    fun `attaches particles by final consonant`() {
        assertEquals("사과가", Korean.iGa("사과"))
        assertEquals("별이", Korean.iGa("별"))
        assertEquals("꽃이", Korean.iGa("꽃"))
        assertEquals("토끼는", Korean.eunNeun("토끼"))
        assertEquals("곰은", Korean.eunNeun("곰"))
        assertEquals("풍선을", Korean.eulReul("풍선"))
        assertEquals("나비를", Korean.eulReul("나비"))
        assertEquals("사과와", Korean.waGwa("사과"))
        assertEquals("별과", Korean.waGwa("별"))
        assertEquals("다섯 마리예요", Korean.ieyo("다섯 마리"))
        assertEquals("별이에요", Korean.ieyo("별"))
    }

    @Test
    fun `chooses euro or ro with rieul exception`() {
        assertEquals("기역으로", Korean.euro("기역"))
        assertEquals("니은으로", Korean.euro("니은"))
        assertEquals("리을로", Korean.euro("리을"))
        assertEquals("나비로", Korean.euro("나비"))
        assertEquals("히읗으로", Korean.euro("히읗"))
    }

    @Test
    fun `builds native korean numbers`() {
        assertEquals("하나", Korean.countWord(1))
        assertEquals("다섯", Korean.countWord(5))
        assertEquals("열", Korean.countWord(10))
        assertEquals("한", Korean.counterNumber(1))
        assertEquals("두", Korean.counterNumber(2))
        assertEquals("세", Korean.counterNumber(3))
        assertEquals("네", Korean.counterNumber(4))
        assertEquals("여덟", Korean.counterNumber(8))
    }

    @Test
    fun `calls names and composes syllables`() {
        assertEquals("하늘아", Korean.vocative("하늘"))
        assertEquals("민서야", Korean.vocative("민서"))
        assertEquals('나', Korean.compose('ㄴ', 'ㅏ'))
        assertEquals('가', Korean.compose('ㄱ', 'ㅏ'))
        assertEquals('호', Korean.compose('ㅎ', 'ㅗ'))
        assertEquals('이', Korean.compose('ㅇ', 'ㅣ'))
        assertEquals(null, Korean.compose('a', 'ㅏ'))
    }

    @Test
    fun `content is complete and unique`() {
        assertEquals(14, HangulContent.consonants.size)
        assertEquals(14, HangulContent.consonants.map { it.letter }.toSet().size)
        HangulContent.consonants.forEach { c ->
            assertTrue(c.word.isNotBlank() && c.emoji.isNotBlank())
            // 낱말 첫 음절은 그 자음으로 시작하는 받침 없는 단모음 음절
            val first = c.word.first()
            val vowelIndex = ((first - '가') % (21 * 28)) / 28
            assertEquals(c.word, first, Korean.compose(c.letter.single(), "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ"[vowelIndex]))
            assertTrue(c.word, vowelIndex in listOf(0, 4, 8, 13, 18, 20))
            assertEquals(c.letter == "ㅇ", c.isSilentInitial)
        }
        SyllableContent.words.forEach { w -> assertEquals(w.word, w.syllable, w.word.first()) }
        assertEquals(SyllableContent.words.size, SyllableContent.words.map { it.syllable }.toSet().size)
        assertEquals(Stickers.all.size, Stickers.all.map { it.id }.toSet().size)
        Songs.all.forEach { song ->
            assertTrue(song.notes.all { it in 0..7 })
            assertTrue(song.shortLength in 1..song.notes.size)
        }
        Emotion.entries.forEach { emotion ->
            assertTrue(EmotionContent.situations.count { it.emotion == emotion } >= 3)
        }
    }
}
