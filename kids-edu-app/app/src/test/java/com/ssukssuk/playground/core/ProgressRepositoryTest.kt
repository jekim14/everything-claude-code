package com.ssukssuk.playground.core

import com.ssukssuk.playground.content.Stickers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.random.Random

class ProgressRepositoryTest {
    @Test
    fun `saves and loads settings`() {
        val repo = ProgressRepository(InMemoryStore())
        assertEquals(0, repo.loadSettings().age)
        repo.saveSettings(Settings(age = 5, voiceOn = false, soundOn = true, restMinutes = 15))
        assertEquals(Settings(age = 5, voiceOn = false, soundOn = true, restMinutes = 15), repo.loadSettings())
        assertEquals(5, repo.loadSettings().difficulty.age)
    }

    @Test
    fun `completing a game updates record stars and sticker`() {
        val repo = ProgressRepository(InMemoryStore())
        val reward = repo.completeGame(Game.COUNTING, score = 4, total = 5, random = Random(3))
        assertTrue(reward.isNew)
        assertEquals(1, reward.totalStars)
        val record = repo.record(Game.COUNTING)
        assertEquals(1, record.plays)
        assertEquals(0.8f, record.accuracy!!, 0.0001f)
        assertNull(repo.record(Game.DRAWING).accuracy)
        assertEquals(mapOf(reward.sticker.id to 1), repo.stickerCounts())
    }

    @Test
    fun `awards uncollected stickers first`() {
        val repo = ProgressRepository(InMemoryStore())
        val random = Random(11)
        val first = List(Stickers.all.size) { repo.completeGame(Game.DRAWING, 0, 0, random) }
        assertTrue(first.all { it.isNew })
        assertEquals(Stickers.all.size, repo.stickerCounts().size)
        val extra = repo.completeGame(Game.DRAWING, 0, 0, random)
        assertTrue(!extra.isNew)
        assertEquals(Stickers.all.size + 1, repo.stickerCounts().values.sum())
    }

    @Test
    fun `daily play time resets on new day`() {
        var day = LocalDate.of(2026, 9, 1)
        val repo = ProgressRepository(InMemoryStore(), today = { day })
        repo.addPlaySeconds(120)
        repo.addPlaySeconds(30)
        assertEquals(150, repo.playSecondsToday())
        day = day.plusDays(1)
        assertEquals(0, repo.playSecondsToday())
        repo.addPlaySeconds(60)
        assertEquals(60, repo.playSecondsToday())
        assertEquals(210, repo.totalPlaySeconds())
    }

    @Test
    fun `reset keeps settings`() {
        val repo = ProgressRepository(InMemoryStore())
        repo.saveSettings(Settings(age = 4, restMinutes = 10))
        repo.completeGame(Game.HANGUL, 5, 5)
        repo.addPlaySeconds(100)
        repo.resetProgress()
        assertEquals(0, repo.totalStars())
        assertEquals(GameRecord(), repo.record(Game.HANGUL))
        assertTrue(repo.stickerCounts().isEmpty())
        assertEquals(0, repo.totalPlaySeconds())
        assertEquals(4, repo.loadSettings().age)
        assertEquals(10, repo.loadSettings().restMinutes)
    }

    @Test
    fun `sticker encoding ignores bad values`() {
        assertEquals(mapOf("cat" to 2, "dog" to 1), ProgressRepository.decodeCounts("cat:2,dog:1,,bad,x:y"))
        assertEquals("cat:2", ProgressRepository.encodeCounts(mapOf("cat" to 2, "dog" to 0)))
    }
}
