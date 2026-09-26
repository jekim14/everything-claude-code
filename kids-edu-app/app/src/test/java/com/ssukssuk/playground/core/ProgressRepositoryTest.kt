package com.ssukssuk.playground.core

import com.ssukssuk.playground.content.Stickers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.random.Random

class ProgressRepositoryTest {
    @Test
    fun `saves and loads settings`() {
        val repo = ProgressRepository(InMemoryStore())
        assertEquals(Settings(), repo.loadSettings())
        val settings = Settings(age = 5, voiceOn = false, soundOn = true, restMinutes = 15, dailyLimitMinutes = 45, childName = "하늘")
        repo.saveSettings(settings)
        assertEquals(settings, repo.loadSettings())
    }

    @Test
    fun `cleans the child name`() {
        assertEquals("하늘", Settings.cleanName("  하늘 "))
        assertEquals("김 하늘", Settings.cleanName("김   하늘"))
        assertEquals(Settings.NAME_MAX_LENGTH, Settings.cleanName("가나다라마바사아자").length)
    }

    @Test
    fun `completing a game updates record`() {
        val repo = ProgressRepository(InMemoryStore())
        repo.completeGame(Game.COUNTING, score = 4, total = 5, age = 4, random = Random(3))
        val record = repo.record(Game.COUNTING)
        assertEquals(1, record.plays)
        assertEquals(0.8f, record.accuracy!!, 0.0001f)
        assertNull(repo.record(Game.DRAWING).accuracy)
    }

    @Test
    fun `stage starts from age and adapts to first try accuracy`() {
        val repo = ProgressRepository(InMemoryStore())
        assertEquals(1, repo.stage(Game.COUNTING, age = 4))
        assertEquals(2, repo.stage(Game.COUNTING, age = 5))

        assertEquals(2, repo.completeGame(Game.COUNTING, 5, 5, age = 4).stage)
        assertEquals(3, repo.completeGame(Game.COUNTING, 4, 5, age = 4).stage)
        assertEquals(3, repo.completeGame(Game.COUNTING, 5, 5, age = 4).stage)
        assertEquals(3, repo.completeGame(Game.COUNTING, 3, 5, age = 4).stage)
        assertEquals(2, repo.completeGame(Game.COUNTING, 2, 5, age = 4).stage)
        assertEquals(2, repo.difficulty(Game.COUNTING, age = 4).stage)
        // 다른 놀이는 따로 셉니다.
        assertEquals(1, repo.stage(Game.HANGUL, age = 4))
        // 자유 놀이는 단계가 바뀌지 않습니다.
        assertEquals(1, repo.completeGame(Game.DRAWING, 0, 0, age = 4).stage)

        repo.resetStages()
        assertEquals(2, repo.stage(Game.COUNTING, age = 5))
    }

    @Test
    fun `gift comes once a day per structured game and never for free play`() {
        var day = LocalDate.of(2026, 9, 1)
        val repo = ProgressRepository(InMemoryStore(), today = { day })
        val first = repo.completeGame(Game.HANGUL, 3, 5, age = 4, random = Random(1))
        assertEquals(ProgressRepository.GIFT_CHOICES, first.giftChoices.size)
        assertEquals(first.giftChoices.size, first.giftChoices.toSet().size)
        assertTrue(repo.completeGame(Game.HANGUL, 5, 5, age = 4).giftChoices.isEmpty())
        assertEquals(3, repo.completeGame(Game.COUNTING, 1, 5, age = 4).giftChoices.size)
        assertTrue(repo.completeGame(Game.DRAWING, 0, 0, age = 4).giftChoices.isEmpty())
        day = day.plusDays(1)
        assertEquals(3, repo.completeGame(Game.HANGUL, 0, 5, age = 4).giftChoices.size)
    }

    @Test
    fun `gift choices prefer stickers not yet collected`() {
        val repo = ProgressRepository(InMemoryStore())
        val random = Random(11)
        repeat(Stickers.all.size - 2) {
            val choices = repo.giftChoices(random)
            assertTrue(choices.all { (repo.stickerCounts()[it.id] ?: 0) == 0 })
            assertTrue(repo.claimSticker(choices.first()))
        }
        val lastChoices = repo.giftChoices(random)
        assertEquals(2, lastChoices.count { (repo.stickerCounts()[it.id] ?: 0) == 0 })
        val owned = lastChoices.first { (repo.stickerCounts()[it.id] ?: 0) > 0 }
        assertFalse(repo.claimSticker(owned))
        assertEquals(Stickers.all.size - 1, repo.stickerCounts().values.sum())
    }

    @Test
    fun `skill status needs enough first try answers`() {
        assertEquals(SkillStatus.NOT_STARTED, GameRecord().status)
        assertEquals(SkillStatus.ENJOYED, GameRecord(plays = 2).status)
        assertEquals(SkillStatus.PRACTICING, GameRecord(plays = 1, firstTryCorrect = 5, questions = 5).status)
        assertEquals(SkillStatus.CAN_DO, GameRecord(plays = 2, firstTryCorrect = 8, questions = 10).status)
        assertEquals(SkillStatus.PRACTICING, GameRecord(plays = 4, firstTryCorrect = 10, questions = 20).status)
    }

    @Test
    fun `daily play time resets on new day`() {
        var day = LocalDate.of(2026, 9, 1)
        val repo = ProgressRepository(InMemoryStore(), today = { day })
        repo.addPlaySeconds(120)
        repo.addPlaySeconds(30)
        repo.addExtraMinutesToday(10)
        assertEquals(150, repo.playSecondsToday())
        assertEquals(10, repo.extraMinutesToday())
        day = day.plusDays(1)
        assertEquals(0, repo.playSecondsToday())
        assertEquals(0, repo.extraMinutesToday())
        repo.addPlaySeconds(60)
        assertEquals(60, repo.playSecondsToday())
        assertEquals(210, repo.totalPlaySeconds())
    }

    @Test
    fun `reset keeps settings`() {
        val repo = ProgressRepository(InMemoryStore())
        repo.saveSettings(Settings(age = 4, restMinutes = 10, childName = "하늘"))
        repo.completeGame(Game.HANGUL, 5, 5, age = 4)
        repo.claimSticker(Stickers.all.first())
        repo.addPlaySeconds(100)
        repo.resetProgress()
        assertEquals(GameRecord(), repo.record(Game.HANGUL))
        assertEquals(1, repo.stage(Game.HANGUL, age = 4))
        assertTrue(repo.stickerCounts().isEmpty())
        assertEquals(0, repo.totalPlaySeconds())
        assertEquals(4, repo.loadSettings().age)
        assertEquals(10, repo.loadSettings().restMinutes)
        assertEquals("하늘", repo.loadSettings().childName)
    }

    @Test
    fun `sticker encoding ignores bad values`() {
        assertEquals(mapOf("cat" to 2, "dog" to 1), ProgressRepository.decodeCounts("cat:2,dog:1,,bad,x:y"))
        assertEquals("cat:2", ProgressRepository.encodeCounts(mapOf("cat" to 2, "dog" to 0)))
    }
}
