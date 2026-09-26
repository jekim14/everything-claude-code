package com.ssukssuk.playground.logic

import com.ssukssuk.playground.content.Consonant
import com.ssukssuk.playground.content.HangulContent
import com.ssukssuk.playground.content.Korean
import com.ssukssuk.playground.content.SyllableContent
import com.ssukssuk.playground.content.SyllableWord
import com.ssukssuk.playground.content.Vowel
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

// ── 글자 만들기 ────────────────────────────────────────────────

data class SyllableQuestion(
    val target: SyllableWord,
    val consonants: List<Consonant>,
    val vowels: List<Vowel>,
)

enum class SyllableResult { CORRECT, WRONG_CONSONANT, WRONG_VOWEL, WRONG_BOTH }

object SyllableQuiz {
    fun generate(
        random: Random,
        count: Int,
        vowels: List<Char>,
        consonantChoices: Int,
        vowelChoices: Int,
        similarDistractors: Boolean,
    ): List<SyllableQuestion> {
        val allowed = SyllableContent.vowels.filter { it.letter in vowels }
        require(allowed.isNotEmpty()) { "no vowels" }
        val pool = SyllableContent.words.filter { it.vowel in allowed }.shuffled(random)
        // 같은 자음이 연달아 나오지 않도록 자음이 다른 낱말부터 고릅니다.
        val targets = (pool.distinctBy { it.initial } + pool).distinct().take(count)
        return targets.map { target ->
            val distractors = mutableListOf<Consonant>()
            if (similarDistractors) {
                HangulContent.similarTo(target.initial)?.let { distractors += it }
            }
            distractors += HangulContent.consonants
                .filter { it != target.initial && it !in distractors }
                .shuffled(random)
            val consonants = (listOf(target.initial) + distractors.take(consonantChoices - 1)).shuffled(random)
            val otherVowels = allowed.filter { it != target.vowel }.shuffled(random)
            val vowelList = (listOf(target.vowel) + otherVowels.take((vowelChoices - 1).coerceAtLeast(0))).shuffled(random)
            SyllableQuestion(target, consonants, vowelList)
        }
    }

    fun check(target: SyllableWord, consonant: Consonant, vowel: Vowel): SyllableResult {
        val consonantOk = consonant == target.initial
        val vowelOk = vowel == target.vowel
        return when {
            consonantOk && vowelOk -> SyllableResult.CORRECT
            vowelOk -> SyllableResult.WRONG_CONSONANT
            consonantOk -> SyllableResult.WRONG_VOWEL
            else -> SyllableResult.WRONG_BOTH
        }
    }

    fun made(consonant: Consonant, vowel: Vowel): Char? = Korean.compose(consonant.letter.single(), vowel.letter)
}

// ── 숫자 징검다리 ──────────────────────────────────────────────

sealed interface HopResult {
    /** 지금은 주사위를 굴릴 차례 */
    data object NotNow : HopResult

    /** 차례가 아닌 돌을 눌렀어요 */
    data object Wrong : HopResult

    /** 한 칸 뛰었고 아직 더 가야 해요 */
    data class Hopped(val stone: Int) : HopResult

    /** 주사위만큼 다 뛰었어요 */
    data class Landed(val stone: Int) : HopResult
}

/**
 * 1부터 [length]까지 한 줄로 놓인 징검다리 판.
 *
 * 수가 같은 간격으로 한 줄에 놓인 판 놀이는 유아의 수 크기 감각을 키웠습니다(Siegler & Ramani 2008, 2009).
 * 칸을 옮길 때 '하나, 둘'이 아니라 칸의 수('넷, 다섯')를 말하도록 [HopResult]마다 그 칸의 수를 돌려줍니다.
 */
class NumberPath(val length: Int = 10) {
    var position: Int = 0
        private set
    var stepsLeft: Int = 0
        private set

    /** 이번 주사위를 굴리기 전 위치 */
    var rollStart: Int = 0
        private set
    var lastRoll: Int = 0
        private set

    val isFinished: Boolean get() = position >= length
    val canRoll: Boolean get() = stepsLeft == 0 && !isFinished

    /** 다음에 눌러야 할 돌. 굴릴 차례면 null */
    val nextStone: Int? get() = if (stepsLeft > 0) position + 1 else null

    /** 주사위 값을 받아 이번에 뛸 칸 수를 정합니다. 끝을 넘지 않게 줄입니다. */
    fun roll(value: Int): Int {
        check(canRoll) { "not rolling time" }
        val steps = value.coerceIn(1, length - position)
        rollStart = position
        lastRoll = steps
        stepsLeft = steps
        return steps
    }

    fun tap(stone: Int): HopResult {
        if (stepsLeft == 0) return HopResult.NotNow
        if (stone != position + 1) return HopResult.Wrong
        position++
        stepsLeft--
        return if (stepsLeft == 0) HopResult.Landed(position) else HopResult.Hopped(position)
    }

    /** "몇에 도착했을까?" 선택지: 도착한 수와 가까운 수들 (작은 수부터) */
    fun arrivalChoices(random: Random, count: Int = 3): List<Int> {
        val others = (1..length)
            .filter { it != position }
            .sortedBy { abs(it - position) + random.nextFloat() * 0.9f }
            .take(count - 1)
        return (others + position).sorted()
    }

    companion object {
        fun rollDie(random: Random, max: Int): Int = random.nextInt(1, max + 1)
    }
}

// ── 멈춰! 놀이 ────────────────────────────────────────────────

/**
 * 한 번 나타나는 친구.
 * [green]은 초록 친구인지, [reversed]는 "이번엔 반대로!" 규칙인지입니다.
 */
data class StopGoTrial(val green: Boolean, val hole: Int, val reversed: Boolean) {
    /** 눌러야 하는 친구인지 */
    val shouldTap: Boolean get() = green != reversed
}

/**
 * 멈춰! 놀이(Go/No-Go) 계획.
 *
 * 누르는 친구가 더 자주 나와(약 70%) 누르려는 습관이 생긴 뒤 멈추는 친구가 나와야 억제 연습이 됩니다.
 * 2단계부터는 중간에 규칙이 바뀌어(DCCS와 같은 규칙 전환) 새 규칙을 기억하는 연습을 합니다.
 */
object StopGoPlan {
    const val HOLES = 6

    fun generate(random: Random, trials: Int, switchRule: Boolean, tapRatio: Float = 0.7f): List<StopGoTrial> {
        require(trials >= 2) { "trials must be at least 2" }
        val phases = if (switchRule) listOf(trials / 2, trials - trials / 2) else listOf(trials)
        val plan = mutableListOf<StopGoTrial>()
        var previousHole = -1
        phases.forEachIndexed { phaseIndex, size ->
            val reversed = phaseIndex == 1
            val taps = (size * tapRatio).roundToInt().coerceIn(1, size - 1)
            // 각 단계의 첫 친구는 누르는 친구로 시작해 규칙을 먼저 성공해 보게 합니다.
            val rest = (List(taps - 1) { true } + List(size - taps) { false }).shuffled(random)
            (listOf(true) + rest).forEach { tap ->
                var hole: Int
                do {
                    hole = random.nextInt(HOLES)
                } while (hole == previousHole)
                previousHole = hole
                plan += StopGoTrial(green = tap != reversed, hole = hole, reversed = reversed)
            }
        }
        return plan
    }

    /** 규칙이 바뀌는 첫 번째 친구의 번호. 바뀌지 않으면 null */
    fun switchIndex(plan: List<StopGoTrial>): Int? = plan.indexOfFirst { it.reversed }.takeIf { it >= 0 }

    /** 눌러야 할 때 누르고, 멈춰야 할 때 멈춘 수 */
    fun score(plan: List<StopGoTrial>, tapped: List<Boolean>): Int =
        plan.zip(tapped).count { (trial, didTap) -> trial.shouldTap == didTap }
}
