package com.ssukssuk.playground.logic

import com.ssukssuk.playground.content.Consonant
import com.ssukssuk.playground.content.CountItem
import com.ssukssuk.playground.content.CountingContent
import com.ssukssuk.playground.content.Emotion
import com.ssukssuk.playground.content.EmotionContent
import com.ssukssuk.playground.content.HangulContent
import com.ssukssuk.playground.content.PatternContent
import com.ssukssuk.playground.content.PatternItem
import com.ssukssuk.playground.content.Situation
import com.ssukssuk.playground.core.PatternKind
import kotlin.math.abs
import kotlin.random.Random

data class HangulQuestion(
    val target: Consonant,
    val choices: List<Consonant>,
)

object HangulQuiz {
    fun generate(
        random: Random,
        count: Int,
        choiceCount: Int,
        similarDistractors: Boolean,
        pool: List<Consonant> = HangulContent.consonants,
    ): List<HangulQuestion> {
        require(choiceCount in 2..pool.size) { "choiceCount must be between 2 and ${pool.size}" }
        return pool.shuffled(random).take(count).map { target ->
            val distractors = mutableListOf<Consonant>()
            if (similarDistractors) {
                HangulContent.similarTo(target)?.takeIf { it in pool }?.let { distractors += it }
            }
            distractors += pool
                .filter { it != target && it !in distractors }
                .shuffled(random)
                .take(choiceCount - 1 - distractors.size)
            HangulQuestion(target, (distractors + target).shuffled(random))
        }
    }
}

data class CountingQuestion(
    val item: CountItem,
    val answer: Int,
    /** 작은 수부터 큰 수 순서로 정렬된 선택지 */
    val choices: List<Int>,
)

object CountingQuiz {
    fun generate(random: Random, count: Int, max: Int, choiceCount: Int = 3): List<CountingQuestion> {
        require(max >= choiceCount) { "max must be at least choiceCount" }
        val items = CountingContent.items.shuffled(random)
        var previous = -1
        return List(count) { index ->
            var answer: Int
            do {
                answer = random.nextInt(1, max + 1)
            } while (answer == previous)
            previous = answer
            CountingQuestion(items[index % items.size], answer, choicesFor(answer, max, random, choiceCount))
        }
    }

    /** 정답과 가까운 수를 오답으로 골라 수의 크기를 비교해 보도록 합니다. */
    fun choicesFor(answer: Int, max: Int, random: Random, choiceCount: Int = 3): List<Int> {
        val distractors = (1..max)
            .filter { it != answer }
            .map { it to abs(it - answer) + random.nextFloat() * 0.9f }
            .sortedBy { it.second }
            .take(choiceCount - 1)
            .map { it.first }
        return (distractors + answer).sorted()
    }
}

data class PatternQuestion(
    val kind: PatternKind,
    /** 화면에 보이는 앞부분. 그 다음 칸이 물음표입니다. */
    val shown: List<PatternItem>,
    val answer: PatternItem,
    val choices: List<PatternItem>,
)

object PatternQuiz {
    fun unitFor(kind: PatternKind, picks: List<PatternItem>): List<PatternItem> {
        val (a, b) = picks
        return when (kind) {
            PatternKind.AB -> listOf(a, b)
            PatternKind.AAB -> listOf(a, a, b)
            PatternKind.ABB -> listOf(a, b, b)
            PatternKind.ABC -> listOf(a, b, picks[2])
        }
    }

    fun generate(random: Random, count: Int, kinds: List<PatternKind>, choiceCount: Int = 3): List<PatternQuestion> {
        require(kinds.isNotEmpty()) { "kinds must not be empty" }
        return List(count) { index ->
            // 첫 문제는 가장 쉬운 AB 규칙으로 시작합니다.
            val kind = if (index == 0) PatternKind.AB else kinds[random.nextInt(kinds.size)]
            val theme = PatternContent.themes[random.nextInt(PatternContent.themes.size)]
            val picks = theme.shuffled(random)
            val unit = unitFor(kind, picks)
            val shownLength = unit.size * 2 + random.nextInt(unit.size)
            val shown = List(shownLength) { unit[it % unit.size] }
            val answer = unit[shownLength % unit.size]

            val choices = mutableListOf(answer)
            unit.distinct().filter { it != answer }.forEach { if (choices.size < choiceCount) choices += it }
            picks.filter { it !in unit }.forEach { if (choices.size < choiceCount) choices += it }
            PatternQuestion(kind, shown, answer, choices.shuffled(random))
        }
    }
}

data class EmotionQuestion(
    val situation: Situation,
    val choices: List<Emotion>,
)

object EmotionQuiz {
    /**
     * 1단계는 기쁨·슬픔·화남·무서움, 2단계부터 놀람까지 다룹니다.
     * 유아는 기쁨을 가장 먼저, 그다음 슬픔·화남, 무서움, 놀람 순서로 구별합니다(Widen & Russell 2003).
     */
    fun emotionsFor(stage: Int): List<Emotion> =
        if (stage >= 2) Emotion.entries.toList() else Emotion.entries.filter { it != Emotion.SURPRISED }

    fun generate(random: Random, count: Int, choiceCount: Int, stage: Int): List<EmotionQuestion> {
        val allowed = emotionsFor(stage)
        require(choiceCount in 2..allowed.size) { "choiceCount must be between 2 and ${allowed.size}" }
        val byEmotion = EmotionContent.situations
            .filter { it.emotion in allowed }
            .shuffled(random)
            .groupBy { it.emotion }
        // 여러 감정이 골고루 나오도록 감정별로 번갈아 뽑습니다.
        val order = allowed.shuffled(random)
        val picked = mutableListOf<Situation>()
        var round = 0
        while (picked.size < count) {
            var added = false
            for (emotion in order) {
                val situation = byEmotion[emotion]?.getOrNull(round) ?: continue
                if (picked.size < count) {
                    picked += situation
                    added = true
                }
            }
            if (!added) break
            round++
        }
        return picked.shuffled(random).map { situation ->
            val others = allowed.filter { it != situation.emotion }.shuffled(random).take(choiceCount - 1)
            EmotionQuestion(situation, (others + situation.emotion).shuffled(random))
        }
    }
}

data class GateQuestion(val a: Int, val b: Int) {
    val answer: Int get() = a + b
}

/** 보호자 확인 문제. 유아가 우연히 풀기 어려운 두 자리 합을 사용합니다. */
object ParentGate {
    fun generate(random: Random): GateQuestion = GateQuestion(random.nextInt(6, 10), random.nextInt(5, 10))
}
