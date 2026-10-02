package com.ssukssuk.playground.content

/** 글자 만들기에 쓰는 모음과 그 소리 */
data class Vowel(val letter: Char, val sound: String)

/** 자음+모음으로 만든 첫 음절과, 그 음절로 시작하는 낱말 */
data class SyllableWord(
    val initial: Consonant,
    val vowel: Vowel,
    val word: String,
    val emoji: String,
) {
    val syllable: Char = Korean.compose(initial.letter.single(), vowel.letter)
        ?: error("cannot compose ${initial.letter}+${vowel.letter}")
}

/**
 * 글자 만들기(자음+모음 조립) 놀이 자료.
 * 낱말은 첫 음절이 받침 없는 CV 음절이고, 그림 문자는 Android 8에서도 보이는 것(Unicode 9 이하)만 골랐습니다.
 */
object SyllableContent {
    val vowels: List<Vowel> = listOf(
        Vowel('ㅏ', "아"),
        Vowel('ㅗ', "오"),
        Vowel('ㅜ', "우"),
        Vowel('ㅣ', "이"),
    )

    fun vowel(letter: Char): Vowel = vowels.first { it.letter == letter }

    private fun w(consonant: String, vowel: Char, word: String, emoji: String) =
        SyllableWord(HangulContent.byLetter(consonant) ?: error(consonant), vowel(vowel), word, emoji)

    val words: List<SyllableWord> = listOf(
        w("ㄱ", 'ㅏ', "가방", "🎒"),
        w("ㄴ", 'ㅏ', "나비", "🦋"),
        w("ㄷ", 'ㅏ', "다람쥐", "🐿️"),
        w("ㄹ", 'ㅏ', "라면", "🍜"),
        w("ㅁ", 'ㅏ', "마이크", "🎤"),
        w("ㅂ", 'ㅏ', "바나나", "🍌"),
        w("ㅅ", 'ㅏ', "사과", "🍎"),
        w("ㅇ", 'ㅏ', "아기", "👶"),
        w("ㅈ", 'ㅏ', "자동차", "🚗"),
        w("ㅋ", 'ㅏ', "카메라", "📷"),
        w("ㅍ", 'ㅏ', "파인애플", "🍍"),
        w("ㅎ", 'ㅏ', "하트", "❤️"),

        w("ㄱ", 'ㅗ', "고양이", "🐱"),
        w("ㄴ", 'ㅗ', "노래", "🎵"),
        w("ㄷ", 'ㅗ', "도넛", "🍩"),
        w("ㄹ", 'ㅗ', "로켓", "🚀"),
        w("ㅁ", 'ㅗ', "모자", "🎩"),
        w("ㅂ", 'ㅗ', "보석", "💎"),
        w("ㅅ", 'ㅗ', "소", "🐮"),
        w("ㅇ", 'ㅗ', "오리", "🦆"),
        w("ㅈ", 'ㅗ', "조개", "🐚"),
        w("ㅊ", 'ㅗ', "초콜릿", "🍫"),
        w("ㅋ", 'ㅗ', "코끼리", "🐘"),
        w("ㅌ", 'ㅗ', "토끼", "🐰"),
        w("ㅍ", 'ㅗ', "포도", "🍇"),
        w("ㅎ", 'ㅗ', "호랑이", "🐯"),

        w("ㄱ", 'ㅜ', "구름", "☁️"),
        w("ㅁ", 'ㅜ', "무지개", "🌈"),
        w("ㅂ", 'ㅜ', "부엉이", "🦉"),
        w("ㅅ", 'ㅜ', "수박", "🍉"),
        w("ㅇ", 'ㅜ', "우유", "🥛"),
        w("ㅈ", 'ㅜ', "주사위", "🎲"),
        w("ㅋ", 'ㅜ', "쿠키", "🍪"),

        w("ㄱ", 'ㅣ', "기차", "🚂"),
        w("ㄹ", 'ㅣ', "리본", "🎀"),
        w("ㅂ", 'ㅣ', "비행기", "✈️"),
        w("ㅅ", 'ㅣ', "시계", "⏰"),
        w("ㅈ", 'ㅣ', "지구", "🌍"),
        w("ㅊ", 'ㅣ', "치즈", "🧀"),
        w("ㅋ", 'ㅣ', "키위", "🥝"),
        w("ㅍ", 'ㅣ', "피아노", "🎹"),
    )
}
