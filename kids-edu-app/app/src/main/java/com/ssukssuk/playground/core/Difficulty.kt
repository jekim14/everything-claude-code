package com.ssukssuk.playground.core

/**
 * 만 나이에 따른 놀이 난이도.
 * 만 4세는 선택지와 수 범위를 줄여 성공 경험을 먼저 쌓도록 하고,
 * 만 5세는 범위를 넓히고 헷갈리기 쉬운 선택지를 섞어 도전감을 줍니다.
 */
data class Difficulty(val age: Int) {
    val isYounger: Boolean get() = age <= 4

    /** 한 번의 놀이에서 푸는 문제 수 */
    val questionsPerRound: Int get() = 5

    /** 숫자 세기 최대 수 (만 4세 1~5, 만 5세 1~10) */
    val countMax: Int get() = if (isYounger) 5 else 10

    val hangulChoices: Int get() = 3

    /** 만 5세는 ㄱ/ㅋ, ㄷ/ㅌ처럼 모양이 비슷한 자음을 오답 선택지로 섞습니다. */
    val hangulSimilarDistractors: Boolean get() = !isYounger

    val memoryPairs: Int get() = if (isYounger) 4 else 6

    /** 짝꿍 카드 시작 전에 그림을 미리 보여 주는 시간 */
    val memoryPreviewMillis: Long get() = if (isYounger) 3000L else 2000L

    val shapeCount: Int get() = if (isYounger) 3 else 5
    val shapeRounds: Int get() = if (isYounger) 2 else 3

    val patternKinds: List<PatternKind>
        get() = if (isYounger) {
            listOf(PatternKind.AB, PatternKind.AB, PatternKind.AAB)
        } else {
            listOf(PatternKind.AB, PatternKind.AAB, PatternKind.ABB, PatternKind.ABC)
        }

    val balloonColorCount: Int get() = if (isYounger) 4 else 7
    val balloonTargetsPerColor: Int get() = if (isYounger) 3 else 5
    val balloonRounds: Int get() = if (isYounger) 2 else 3

    /** 풍선이 올라가는 속도(화면 높이 비율/초) */
    val balloonSpeed: Float get() = if (isYounger) 0.11f else 0.15f

    val emotionChoices: Int get() = if (isYounger) 3 else 4

    companion object {
        val SUPPORTED_AGES = listOf(4, 5)
    }
}

enum class PatternKind(val unitSize: Int, val label: String) {
    AB(2, "AB"),
    AAB(3, "AAB"),
    ABB(3, "ABB"),
    ABC(3, "ABC"),
}
