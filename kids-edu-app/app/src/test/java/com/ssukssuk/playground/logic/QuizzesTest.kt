package com.ssukssuk.playground.logic

import com.ssukssuk.playground.content.Emotion
import com.ssukssuk.playground.content.EmotionContent
import com.ssukssuk.playground.content.HangulContent
import com.ssukssuk.playground.core.Difficulty
import com.ssukssuk.playground.core.PatternKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class QuizzesTest {
    private val seeds = 0 until 200

    @Test
    fun `hangul choices are distinct and include answer`() {
        for (seed in seeds) {
            val questions = HangulQuiz.generate(Random(seed), count = 5, choiceCount = 3, similarDistractors = seed % 2 == 0)
            assertEquals(5, questions.size)
            assertEquals(5, questions.map { it.target }.toSet().size)
            questions.forEach { q ->
                assertEquals(3, q.choices.size)
                assertEquals(3, q.choices.toSet().size)
                assertTrue(q.target in q.choices)
            }
        }
    }

    @Test
    fun `older kids get similar consonant distractors`() {
        val questions = HangulQuiz.generate(Random(1), count = 14, choiceCount = 3, similarDistractors = true)
        questions.forEach { q ->
            val similar = HangulContent.similarTo(q.target)
            if (similar != null) assertTrue(similar in q.choices)
        }
    }

    @Test
    fun `counting choices are sorted in range and include answer`() {
        for (seed in seeds) {
            for (max in listOf(5, 10)) {
                val questions = CountingQuiz.generate(Random(seed), count = 5, max = max)
                questions.zipWithNext().forEach { (a, b) -> assertTrue(a.answer != b.answer) }
                questions.forEach { q ->
                    assertTrue(q.answer in 1..max)
                    assertTrue(q.answer in q.choices)
                    assertEquals(3, q.choices.toSet().size)
                    assertTrue(q.choices.all { it in 1..max })
                    assertEquals(q.choices.sorted(), q.choices)
                }
            }
        }
    }

    @Test
    fun `pattern answer continues the repeating unit`() {
        val kinds = Difficulty(2).patternKinds
        for (seed in seeds) {
            val questions = PatternQuiz.generate(Random(seed), count = 5, kinds = kinds)
            assertEquals(PatternKind.AB, questions.first().kind)
            questions.forEach { q ->
                val unitSize = q.kind.unitSize
                val unit = q.shown.take(unitSize)
                q.shown.forEachIndexed { i, item -> assertEquals(unit[i % unitSize], item) }
                assertEquals(unit[q.shown.size % unitSize], q.answer)
                assertTrue(q.answer in q.choices)
                assertEquals(3, q.choices.toSet().size)
            }
        }
    }

    @Test
    fun `emotion choices follow the order children learn emotions`() {
        for (seed in seeds) {
            val younger = EmotionQuiz.generate(Random(seed), count = 5, choiceCount = 3, stage = 1)
            assertEquals(5, younger.size)
            assertEquals(5, younger.map { it.situation }.toSet().size)
            younger.forEach { q ->
                assertTrue(Emotion.SURPRISED !in q.choices)
                assertTrue(q.situation.emotion in q.choices)
                assertEquals(3, q.choices.toSet().size)
            }
            // 1단계에서도 무서움은 나온다 (기쁨·슬픔·화남·무서움)
            assertTrue(younger.map { it.situation.emotion }.toSet().size >= 4)
            val older = EmotionQuiz.generate(Random(seed), count = 5, choiceCount = 4, stage = 2)
            older.forEach { q ->
                assertTrue(q.situation.emotion in q.choices)
                assertEquals(4, q.choices.toSet().size)
            }
            // 감정이 한 가지로 몰리지 않는다
            assertTrue(older.map { it.situation.emotion }.toSet().size >= 4)
            val stretch = EmotionQuiz.generate(Random(seed), count = 5, choiceCount = 5, stage = 3)
            stretch.forEach { q -> assertEquals(Emotion.entries.toSet(), q.choices.toSet()) }
        }
    }

    @Test
    fun `plausible alternative emotions are never the main answer`() {
        EmotionContent.situations.forEach { s -> assertTrue(s.emotion !in s.alsoOk) }
    }

    @Test
    fun `parent gate uses two digit sums`() {
        for (seed in seeds) {
            val q = ParentGate.generate(Random(seed))
            assertTrue(q.answer in 11..18)
        }
    }
}
