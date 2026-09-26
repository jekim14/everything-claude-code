package com.ssukssuk.playground.logic

import com.ssukssuk.playground.content.MemoryContent
import com.ssukssuk.playground.content.MemoryFace
import kotlin.random.Random

sealed interface FlipOutcome {
    /** 이미 열렸거나 짝을 맞춘 카드, 또는 틀린 두 장을 덮는 중이라 무시됨 */
    data object Ignored : FlipOutcome
    data class First(val index: Int) : FlipOutcome
    data class Match(val first: Int, val second: Int) : FlipOutcome
    data class Mismatch(val first: Int, val second: Int) : FlipOutcome
}

/** 짝꿍 카드 놀이의 규칙. 화면과 분리해 단위 테스트로 검증합니다. */
class MemoryBoard(val faces: List<MemoryFace>) {
    private val faceUp = BooleanArray(faces.size)
    private val matched = BooleanArray(faces.size)
    private var firstIndex: Int? = null
    private var pendingMismatch: Pair<Int, Int>? = null

    /** 두 장씩 뒤집은 횟수 */
    var moves: Int = 0
        private set

    val pairCount: Int get() = faces.size / 2
    val isComplete: Boolean get() = matched.all { it }
    val isWaitingToHide: Boolean get() = pendingMismatch != null

    fun isFaceUp(index: Int): Boolean = faceUp[index]
    fun isMatched(index: Int): Boolean = matched[index]

    fun flip(index: Int): FlipOutcome {
        if (index !in faces.indices || pendingMismatch != null || faceUp[index] || matched[index]) {
            return FlipOutcome.Ignored
        }
        faceUp[index] = true
        val first = firstIndex
        if (first == null) {
            firstIndex = index
            return FlipOutcome.First(index)
        }
        firstIndex = null
        moves++
        return if (faces[first] == faces[index]) {
            matched[first] = true
            matched[index] = true
            FlipOutcome.Match(first, index)
        } else {
            pendingMismatch = first to index
            FlipOutcome.Mismatch(first, index)
        }
    }

    /** 틀린 두 장을 다시 덮습니다. */
    fun hideMismatch() {
        pendingMismatch?.let { (a, b) ->
            faceUp[a] = false
            faceUp[b] = false
        }
        pendingMismatch = null
    }

    companion object {
        fun create(random: Random, pairs: Int, pool: List<MemoryFace> = MemoryContent.faces): MemoryBoard {
            require(pairs in 1..pool.size) { "pairs must be between 1 and ${pool.size}" }
            val chosen = pool.shuffled(random).take(pairs)
            return MemoryBoard((chosen + chosen).shuffled(random))
        }
    }
}
