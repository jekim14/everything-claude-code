package com.ssukssuk.playground.content

import com.ssukssuk.playground.core.Difficulty
import com.ssukssuk.playground.logic.CountingQuiz
import com.ssukssuk.playground.logic.EmotionQuiz
import com.ssukssuk.playground.logic.PatternQuiz
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.io.File
import kotlin.random.Random

class VoiceScriptTest {
    private val known = VoiceScript.segments().toSet()

    @Test
    fun `hash matches reference FNV-1a values`() {
        // tools/make-voice.mjs 와 같은 값이어야 합니다.
        assertEquals("cbf29ce484222325", VoiceKey.hash(""))
        assertEquals("af63dc4c8601ec8c", VoiceKey.hash("a"))
        assertEquals("ad134a629984a394", VoiceKey.hash("안녕, 하늘아!"))
        assertEquals("a67e018eba9354fa", VoiceKey.hash("  하나,  둘, 셋. "))
        assertEquals("v_a67e018eba9354fa.mp3", VoiceKey.fileName("하나, 둘, 셋."))
    }

    @Test
    fun `splits after sentence marks but not inside quotes`() {
        assertEquals(
            listOf("딩동댕!", "잘 보고 골랐구나.", "고양이는 기역으로 시작해요!"),
            VoiceKey.segments("딩동댕! 잘 보고 골랐구나.  고양이는 기역으로 시작해요!"),
        )
        assertEquals(
            listOf("선물을 받으면 기뻐요.", "'고마워!' 하고 마음을 전해 볼까요?"),
            VoiceKey.segments("선물을 받으면 기뻐요. '고마워!' 하고 마음을 전해 볼까요?"),
        )
    }

    @Test
    fun `segments are unique by file name`() {
        val segments = VoiceScript.segments()
        assertTrue(segments.size > 300)
        assertEquals(segments.size, segments.map { VoiceKey.fileName(it) }.toSet().size)
        assertTrue(segments.none { Lines.NAME_TOKEN in it })
    }

    @Test
    fun `name lines are built from the templates`() {
        assertEquals("안녕, 하늘아!", Lines.helloName("하늘"))
        assertEquals("민서야!", Lines.callName("민서"))
        assertEquals("하늘아! 오늘 놀이는 여기까지!", VoiceKey.segments(Lines.restDayDone("하늘")).take(2).joinToString(" "))
        assertEquals(Lines.REST_DAY_DONE, Lines.restDayDone(""))
    }

    @Test
    fun `generated game lines are all in the voice list`() {
        val missing = mutableSetOf<String>()
        fun check(text: String) = VoiceKey.segments(text).filter { it !in known }.forEach { missing += it }
        val praises = Phrases.praiseFirstTry + Phrases.praiseAfterRetry
        for (seed in 1..20) {
            val random = Random(seed)
            for (stage in Difficulty.MIN_STAGE..Difficulty.MAX_STAGE) {
                val d = Difficulty(stage)
                PatternQuiz.generate(random, 5, d.patternKinds).forEach { q ->
                    check(Lines.patternRead("", q.shown, withAnswer = false))
                    check(Lines.patternRead(praises.random(random), q.shown + q.answer, withAnswer = true))
                }
                CountingQuiz.generate(random, 5, d.countMax, d.countChoices).forEach { q ->
                    check(Lines.countingCorrect(praises.random(random), q.answer, q.item))
                    check(Lines.countingDemo(q.answer, q.item))
                }
                EmotionQuiz.generate(random, 5, d.emotionChoices, stage).forEach { q ->
                    q.choices.forEach { e ->
                        when (e) {
                            q.situation.emotion -> check(Lines.emotionCorrect(praises.random(random), e, q.situation))
                            in q.situation.alsoOk -> check(Lines.emotionAlsoOk(e, q.situation))
                            else -> check(Lines.emotionWrong(e))
                        }
                    }
                }
            }
        }
        assertTrue("음성 목록에 없는 문장: $missing", missing.isEmpty())
    }

    /**
     * voice/lines.txt 가 코드와 같은지 확인합니다.
     * 문장을 바꿨다면: ./gradlew :app:testDebugUnitTest --tests '*VoiceScriptTest*' -PupdateVoiceLines=true
     */
    @Test
    fun `voice lines file is up to date`() {
        val path = System.getProperty("voiceLinesFile")
        val file = path?.let(::File) ?: File("../voice/lines.txt")
        assumeTrue("voice lines file not configured", file.parentFile?.exists() == true)
        val expected = VoiceScript.exportText()
        if (System.getProperty("updateVoiceLines") == "true") {
            file.writeText(expected, Charsets.UTF_8)
            return
        }
        assertTrue("${file.path} 가 없습니다. -PupdateVoiceLines=true 로 만들어 주세요.", file.exists())
        assertEquals(
            "voice/lines.txt 가 오래되었습니다. -PupdateVoiceLines=true 로 다시 만들어 주세요.",
            expected,
            file.readText(Charsets.UTF_8),
        )
    }
}
