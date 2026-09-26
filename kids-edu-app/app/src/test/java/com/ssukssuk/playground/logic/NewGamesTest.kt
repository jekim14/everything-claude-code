package com.ssukssuk.playground.logic

import com.ssukssuk.playground.content.HangulContent
import com.ssukssuk.playground.content.SyllableContent
import com.ssukssuk.playground.core.Difficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class NewGamesTest {
    private val seeds = 1..30

    @Test
    fun `difficulty moves one stage at a time`() {
        assertEquals(1, Difficulty.startingStage(4))
        assertEquals(2, Difficulty.startingStage(5))
        assertEquals(2, Difficulty.nextStage(1, 4, 5))
        assertEquals(1, Difficulty.nextStage(1, 2, 5))
        assertEquals(2, Difficulty.nextStage(2, 3, 5))
        assertEquals(3, Difficulty.nextStage(3, 5, 5))
        assertEquals(2, Difficulty.nextStage(2, 0, 0))
        assertEquals(listOf('ㅏ'), Difficulty(1).syllableVowels)
        assertTrue(Difficulty(3).isStretch)
    }

    @Test
    fun `syllable questions offer the needed pieces`() {
        for (stage in Difficulty.MIN_STAGE..Difficulty.MAX_STAGE) {
            val d = Difficulty(stage)
            for (seed in seeds) {
                val questions = SyllableQuiz.generate(
                    Random(seed), d.questionsPerRound, d.syllableVowels,
                    d.syllableConsonantChoices, d.syllableVowelChoices, d.hangulSimilarDistractors,
                )
                assertEquals(d.questionsPerRound, questions.size)
                assertEquals(questions.size, questions.map { it.target }.toSet().size)
                questions.forEach { q ->
                    assertTrue(q.target.initial in q.consonants)
                    assertTrue(q.target.vowel in q.vowels)
                    assertEquals(d.syllableConsonantChoices, q.consonants.toSet().size)
                    assertEquals(d.syllableVowelChoices, q.vowels.toSet().size)
                    assertTrue(q.vowels.all { it.letter in d.syllableVowels })
                    assertEquals(SyllableResult.CORRECT, SyllableQuiz.check(q.target, q.target.initial, q.target.vowel))
                    assertEquals(q.target.syllable, SyllableQuiz.made(q.target.initial, q.target.vowel))
                }
            }
        }
    }

    @Test
    fun `syllable check tells which piece is wrong`() {
        val na = SyllableContent.words.first { it.syllable == '나' }
        val giyeok = HangulContent.byLetter("ㄱ")!!
        val o = SyllableContent.vowel('ㅗ')
        assertEquals(SyllableResult.WRONG_CONSONANT, SyllableQuiz.check(na, giyeok, na.vowel))
        assertEquals(SyllableResult.WRONG_VOWEL, SyllableQuiz.check(na, na.initial, o))
        assertEquals(SyllableResult.WRONG_BOTH, SyllableQuiz.check(na, giyeok, o))
        assertEquals('고', SyllableQuiz.made(giyeok, o))
    }

    @Test
    fun `number path hops one stone at a time and stops at the end`() {
        val path = NumberPath(10)
        assertTrue(path.canRoll)
        assertEquals(HopResult.NotNow, path.tap(1))
        assertEquals(2, path.roll(2))
        assertEquals(1, path.nextStone)
        assertEquals(HopResult.Wrong, path.tap(2))
        assertEquals(HopResult.Hopped(1), path.tap(1))
        assertEquals(HopResult.Landed(2), path.tap(2))
        assertEquals(0, path.rollStart)
        assertNull(path.nextStone)
        repeat(3) {
            path.roll(2)
            path.tap(path.nextStone!!)
            path.tap(path.nextStone!!)
        }
        assertEquals(8, path.position)
        assertEquals(2, path.roll(3))
        path.tap(9)
        assertEquals(HopResult.Landed(10), path.tap(10))
        assertTrue(path.isFinished)
        assertFalse(path.canRoll)
    }

    @Test
    fun `number path arrival choices are nearby and sorted`() {
        for (seed in seeds) {
            val path = NumberPath(10)
            path.roll(3)
            repeat(3) { path.tap(path.nextStone!!) }
            val choices = path.arrivalChoices(Random(seed))
            assertEquals(choices.sorted(), choices)
            assertEquals(3, choices.toSet().size)
            assertTrue(3 in choices)
            assertTrue(choices.all { it in 1..10 })
        }
    }

    @Test
    fun `die stays in range`() {
        val random = Random(5)
        repeat(200) { assertTrue(NumberPath.rollDie(random, 2) in 1..2) }
    }

    @Test
    fun `stop go plan mostly asks to tap and can switch the rule`() {
        for (seed in seeds) {
            val plain = StopGoPlan.generate(Random(seed), trials = 10, switchRule = false)
            assertEquals(10, plain.size)
            assertTrue(plain.first().shouldTap)
            assertEquals(7, plain.count { it.shouldTap })
            assertTrue(plain.none { it.reversed })
            assertNull(StopGoPlan.switchIndex(plain))
            plain.zipWithNext().forEach { (a, b) -> assertTrue(a.hole != b.hole) }
            assertTrue(plain.all { it.hole in 0 until StopGoPlan.HOLES })

            val switched = StopGoPlan.generate(Random(seed), trials = 12, switchRule = true)
            assertEquals(6, StopGoPlan.switchIndex(switched))
            assertTrue(switched[6].shouldTap)
            // 규칙이 바뀌면 빨간 친구를 눌러야 합니다.
            switched.drop(6).forEach { assertEquals(!it.green, it.shouldTap) }
            switched.take(6).forEach { assertEquals(it.green, it.shouldTap) }
        }
    }

    @Test
    fun `stop go score counts taps and holds`() {
        val plan = listOf(
            StopGoTrial(green = true, hole = 0, reversed = false),
            StopGoTrial(green = false, hole = 1, reversed = false),
            StopGoTrial(green = false, hole = 2, reversed = true),
        )
        assertEquals(3, StopGoPlan.score(plan, listOf(true, false, true)))
        assertEquals(0, StopGoPlan.score(plan, listOf(false, true, false)))
    }
}
