package com.ssukssuk.playground.content

/**
 * 안내 문장을 자연스럽게 만들기 위한 한국어 도우미.
 * 받침 유무에 따라 조사를 고르고, 고유어 수사를 제공합니다.
 */
object Korean {
    private const val HANGUL_START = '가'
    private const val HANGUL_END = '힣'
    private const val JONG_COUNT = 28
    private const val JONG_RIEUL = 8

    private fun lastSyllable(word: String): Char? =
        word.lastOrNull { !it.isWhitespace() }?.takeIf { it in HANGUL_START..HANGUL_END }

    fun hasBatchim(word: String): Boolean {
        val c = lastSyllable(word) ?: return false
        return (c - HANGUL_START) % JONG_COUNT != 0
    }

    private fun hasRieulBatchim(word: String): Boolean {
        val c = lastSyllable(word) ?: return false
        return (c - HANGUL_START) % JONG_COUNT == JONG_RIEUL
    }

    /** 사과가 / 별이 */
    fun iGa(word: String) = word + if (hasBatchim(word)) "이" else "가"

    /** 사과는 / 별은 */
    fun eunNeun(word: String) = word + if (hasBatchim(word)) "은" else "는"

    /** 사과를 / 별을 */
    fun eulReul(word: String) = word + if (hasBatchim(word)) "을" else "를"

    /** 사과와 / 별과 */
    fun waGwa(word: String) = word + if (hasBatchim(word)) "과" else "와"

    /** 나비로 / 기역으로 / 리을로 (ㄹ 받침은 '로') */
    fun euro(word: String) = word + if (hasBatchim(word) && !hasRieulBatchim(word)) "으로" else "로"

    /** 사과예요 / 별이에요 */
    fun ieyo(word: String) = word + if (hasBatchim(word)) "이에요" else "예요"

    private val nativeNumbers = listOf("하나", "둘", "셋", "넷", "다섯", "여섯", "일곱", "여덟", "아홉", "열")
    private val nativeCounterNumbers = listOf("한", "두", "세", "네", "다섯", "여섯", "일곱", "여덟", "아홉", "열")

    /** 수를 셀 때: 하나, 둘, 셋 … 열 */
    fun countWord(n: Int): String = nativeNumbers.getOrElse(n - 1) { n.toString() }

    /** 단위 앞에서: 한 개, 두 마리, 세 송이 … */
    fun counterNumber(n: Int): String = nativeCounterNumbers.getOrElse(n - 1) { n.toString() }
}
