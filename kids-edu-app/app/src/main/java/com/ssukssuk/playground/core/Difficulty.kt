package com.ssukssuk.playground.core

/**
 * 놀이 난이도 단계 (1~3).
 *
 * 나이는 출발점일 뿐이고(만 4세 → 1단계, 만 5세 → 2단계), 실제 단계는 놀이마다 아이가 첫 시도에
 * 맞힌 비율에 따라 한 판씩 오르내립니다([nextStage]). 1단계는 선택지와 수 범위를 줄여 성공 경험을
 * 먼저 쌓게 하고, 2단계는 범위를 넓히고 헷갈리기 쉬운 선택지를 섞고, 3단계는 조금 더 빠르고 선택지가 많습니다.
 */
data class Difficulty(val stage: Int) {
    init {
        require(stage in MIN_STAGE..MAX_STAGE) { "stage must be in $MIN_STAGE..$MAX_STAGE" }
    }

    val isYounger: Boolean get() = stage <= 1
    val isStretch: Boolean get() = stage >= MAX_STAGE

    /** 한 번의 놀이에서 푸는 문제 수 */
    val questionsPerRound: Int get() = 5

    /** 숫자 세기 최대 수 (1단계 1~5, 2단계부터 1~10) */
    val countMax: Int get() = if (isYounger) 5 else 10
    val countChoices: Int get() = if (isStretch) 4 else 3

    val hangulChoices: Int get() = if (isStretch) 4 else 3

    /** 2단계부터 ㄱ/ㅋ, ㄷ/ㅌ처럼 모양이 비슷한 자음을 오답 선택지로 섞습니다. */
    val hangulSimilarDistractors: Boolean get() = !isYounger

    /** 글자 만들기에 쓰는 모음. 1단계는 ㅏ 하나로 자음 소리에만 집중합니다. */
    val syllableVowels: List<Char>
        get() = if (isYounger) listOf('ㅏ') else listOf('ㅏ', 'ㅗ', 'ㅜ', 'ㅣ')
    val syllableConsonantChoices: Int get() = if (isStretch) 4 else 3
    val syllableVowelChoices: Int
        get() = when (stage) {
            1 -> 1
            2 -> 2
            else -> 3
        }

    val memoryPairs: Int get() = if (isYounger) 4 else 6

    /** 짝꿍 카드 시작 전에 그림을 미리 보여 주는 시간 */
    val memoryPreviewMillis: Long
        get() = when (stage) {
            1 -> 3000L
            2 -> 2000L
            else -> 1400L
        }

    val shapeCount: Int get() = if (isYounger) 3 else 5
    val shapeRounds: Int get() = if (isYounger) 2 else 3

    val patternKinds: List<PatternKind>
        get() = when (stage) {
            1 -> listOf(PatternKind.AB, PatternKind.AB, PatternKind.AAB)
            2 -> listOf(PatternKind.AB, PatternKind.AAB, PatternKind.ABB, PatternKind.ABC)
            else -> listOf(PatternKind.AAB, PatternKind.ABB, PatternKind.ABC, PatternKind.ABC)
        }

    val balloonColorCount: Int get() = if (isYounger) 4 else 7
    val balloonTargetsPerColor: Int get() = if (isYounger) 3 else 5
    val balloonRounds: Int get() = if (isYounger) 2 else 3

    /** 풍선이 올라가는 속도(화면 높이 비율/초) */
    val balloonSpeed: Float
        get() = when (stage) {
            1 -> 0.11f
            2 -> 0.15f
            else -> 0.18f
        }

    val emotionChoices: Int
        get() = when (stage) {
            1 -> 3
            2 -> 4
            else -> 5
        }

    /** 숫자 징검다리 주사위 최대 눈 */
    val pathDieMax: Int get() = if (isYounger) 2 else 3

    /** 숫자 징검다리에서 뛰고 나서 "몇에 도착했을까?"를 물어볼지 */
    val pathAskArrival: Boolean get() = !isYounger

    val stopGoTrials: Int get() = if (isYounger) 10 else 12

    /** 멈춰! 놀이에서 친구가 나와 있는 시간 */
    val stopGoWindowMillis: Long
        get() = when (stage) {
            1 -> 2200L
            2 -> 1800L
            else -> 1400L
        }

    /** 2단계부터 중간에 "이번엔 반대로!" 규칙이 바뀝니다. */
    val stopGoSwitchRule: Boolean get() = !isYounger

    companion object {
        const val MIN_STAGE = 1
        const val MAX_STAGE = 3
        val SUPPORTED_AGES = listOf(4, 5)

        fun startingStage(age: Int): Int = if (age >= 5) 2 else 1

        fun forAge(age: Int): Difficulty = Difficulty(startingStage(age))

        /**
         * 한 판을 마친 뒤 다음 판의 단계.
         * 첫 시도 정답률 80% 이상이면 한 단계 올리고, 50% 미만이면 한 단계 내립니다.
         * 문제가 없는 자유 놀이는 그대로 둡니다.
         */
        fun nextStage(current: Int, firstTryCorrect: Int, total: Int): Int {
            if (total <= 0) return current.coerceIn(MIN_STAGE, MAX_STAGE)
            val rate = firstTryCorrect.coerceIn(0, total).toFloat() / total
            val next = when {
                rate >= 0.8f -> current + 1
                rate < 0.5f -> current - 1
                else -> current
            }
            return next.coerceIn(MIN_STAGE, MAX_STAGE)
        }
    }
}

enum class PatternKind(val unitSize: Int, val label: String) {
    AB(2, "AB"),
    AAB(3, "AAB"),
    ABB(3, "ABB"),
    ABC(3, "ABC"),
}
