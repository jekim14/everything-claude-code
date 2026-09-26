package com.ssukssuk.playground.logic

import com.ssukssuk.playground.content.MemoryFace
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class MemoryBoardTest {
    private val dog = MemoryFace("🐶", "강아지")
    private val cat = MemoryFace("🐱", "고양이")

    @Test
    fun `two same faces match`() {
        val board = MemoryBoard(listOf(dog, cat, dog, cat))
        assertEquals(FlipOutcome.First(0), board.flip(0))
        assertEquals(FlipOutcome.Match(0, 2), board.flip(2))
        assertTrue(board.isMatched(0) && board.isMatched(2))
        assertEquals(1, board.moves)
        assertFalse(board.isComplete)
    }

    @Test
    fun `mismatch blocks flips until hidden`() {
        val board = MemoryBoard(listOf(dog, cat, dog, cat))
        board.flip(0)
        assertEquals(FlipOutcome.Mismatch(0, 1), board.flip(1))
        assertTrue(board.isWaitingToHide)
        assertEquals(FlipOutcome.Ignored, board.flip(2))
        board.hideMismatch()
        assertFalse(board.isFaceUp(0))
        assertFalse(board.isFaceUp(1))
        assertEquals(FlipOutcome.First(2), board.flip(2))
    }

    @Test
    fun `ignores open cards and completes`() {
        val board = MemoryBoard(listOf(dog, cat, dog, cat))
        board.flip(0)
        assertEquals(FlipOutcome.Ignored, board.flip(0))
        board.flip(2)
        assertEquals(FlipOutcome.Ignored, board.flip(2))
        board.flip(1)
        board.flip(3)
        assertTrue(board.isComplete)
        assertEquals(2, board.moves)
    }

    @Test
    fun `board has requested pairs`() {
        for (seed in 0 until 50) {
            val board = MemoryBoard.create(Random(seed), pairs = 6)
            assertEquals(12, board.faces.size)
            board.faces.groupingBy { it }.eachCount().values.forEach { assertEquals(2, it) }
        }
    }
}
