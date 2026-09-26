package com.ssukssuk.playground.content

// 놀이에 쓰이는 그림 문자는 대부분 Android 8.0 이상 기본 글꼴에서 표시되는 이모지(Unicode 9 이하)만 사용합니다.

/** 한글 자음과 그 자음으로 시작하는 낱말 */
data class Consonant(
    val letter: String,
    val name: String,
    val word: String,
    val emoji: String,
)

object HangulContent {
    val consonants: List<Consonant> = listOf(
        Consonant("ㄱ", "기역", "고양이", "🐱"),
        Consonant("ㄴ", "니은", "나비", "🦋"),
        Consonant("ㄷ", "디귿", "돼지", "🐷"),
        Consonant("ㄹ", "리을", "로켓", "🚀"),
        Consonant("ㅁ", "미음", "물고기", "🐟"),
        Consonant("ㅂ", "비읍", "바나나", "🍌"),
        Consonant("ㅅ", "시옷", "사과", "🍎"),
        Consonant("ㅇ", "이응", "원숭이", "🐵"),
        Consonant("ㅈ", "지읒", "자동차", "🚗"),
        Consonant("ㅊ", "치읓", "책", "📚"),
        Consonant("ㅋ", "키읔", "코끼리", "🐘"),
        Consonant("ㅌ", "티읕", "토끼", "🐰"),
        Consonant("ㅍ", "피읖", "포도", "🍇"),
        Consonant("ㅎ", "히읗", "호랑이", "🐯"),
    )

    /** 모양이나 소리가 비슷해 헷갈리기 쉬운 자음 짝 (만 5세 오답 선택지) */
    private val similar: Map<String, String> = mapOf(
        "ㄱ" to "ㅋ", "ㅋ" to "ㄱ",
        "ㄷ" to "ㅌ", "ㅌ" to "ㄷ",
        "ㅂ" to "ㅍ", "ㅍ" to "ㅂ",
        "ㅈ" to "ㅊ", "ㅊ" to "ㅈ",
        "ㅇ" to "ㅎ", "ㅎ" to "ㅇ",
        "ㄴ" to "ㄷ", "ㄹ" to "ㄷ",
        "ㅁ" to "ㅂ", "ㅅ" to "ㅈ",
    )

    fun similarTo(consonant: Consonant): Consonant? =
        similar[consonant.letter]?.let { letter -> consonants.firstOrNull { it.letter == letter } }
}

/** 숫자 세기에 나오는 물건과 알맞은 단위(개, 마리, 송이, 대) */
data class CountItem(
    val emoji: String,
    val name: String,
    val counter: String,
)

object CountingContent {
    val items: List<CountItem> = listOf(
        CountItem("🍎", "사과", "개"),
        CountItem("🍓", "딸기", "개"),
        CountItem("🐥", "병아리", "마리"),
        CountItem("🐟", "물고기", "마리"),
        CountItem("⭐", "별", "개"),
        CountItem("🎈", "풍선", "개"),
        CountItem("🐞", "무당벌레", "마리"),
        CountItem("🌷", "꽃", "송이"),
        CountItem("🚗", "자동차", "대"),
        CountItem("🍪", "쿠키", "개"),
    )
}

/** 풍선·그림 그리기에 쓰는 색 */
data class KidColor(
    /** 빨간색 */
    val name: String,
    /** 빨간 (풍선 앞에 붙는 꾸밈말) */
    val adjective: String,
    val argb: Long,
)

object Palette {
    val red = KidColor("빨간색", "빨간", 0xFFF44336)
    val yellow = KidColor("노란색", "노란", 0xFFFFD600)
    val blue = KidColor("파란색", "파란", 0xFF2979FF)
    val green = KidColor("초록색", "초록", 0xFF43A047)
    val orange = KidColor("주황색", "주황", 0xFFFF9100)
    val purple = KidColor("보라색", "보라", 0xFF8E24AA)
    val pink = KidColor("분홍색", "분홍", 0xFFFF6FAE)

    /** 만 4세는 앞의 4가지(빨강·노랑·파랑·초록), 만 5세는 7가지 */
    val balloonColors: List<KidColor> = listOf(red, yellow, blue, green, orange, purple, pink)
}

data class PatternItem(
    val emoji: String,
    val name: String,
)

object PatternContent {
    val themes: List<List<PatternItem>> = listOf(
        listOf(
            PatternItem("🍎", "사과"),
            PatternItem("🍌", "바나나"),
            PatternItem("🍇", "포도"),
            PatternItem("🍓", "딸기"),
            PatternItem("🍊", "귤"),
            PatternItem("🍉", "수박"),
        ),
        listOf(
            PatternItem("🐶", "강아지"),
            PatternItem("🐱", "고양이"),
            PatternItem("🐰", "토끼"),
            PatternItem("🐻", "곰"),
            PatternItem("🐸", "개구리"),
            PatternItem("🐷", "돼지"),
        ),
        listOf(
            PatternItem("⭐", "별"),
            PatternItem("🌙", "달"),
            PatternItem("🌞", "해"),
            PatternItem("🌸", "꽃"),
            PatternItem("🍄", "버섯"),
            PatternItem("🎈", "풍선"),
        ),
    )
}

data class MemoryFace(
    val emoji: String,
    val name: String,
)

object MemoryContent {
    val faces: List<MemoryFace> = listOf(
        MemoryFace("🐶", "강아지"),
        MemoryFace("🐱", "고양이"),
        MemoryFace("🐰", "토끼"),
        MemoryFace("🐻", "곰"),
        MemoryFace("🐼", "판다"),
        MemoryFace("🐸", "개구리"),
        MemoryFace("🐵", "원숭이"),
        MemoryFace("🐧", "펭귄"),
        MemoryFace("🐯", "호랑이"),
        MemoryFace("🐷", "돼지"),
        MemoryFace("🐮", "소"),
        MemoryFace("🐤", "병아리"),
    )
}

/** 감정. [label]은 아이에게 들려주는 말, [adjective]는 '기쁜 마음'처럼 쓰는 꾸밈말입니다. */
enum class Emotion(val label: String, val adjective: String) {
    HAPPY("기뻐요", "기쁜"),
    SAD("슬퍼요", "슬픈"),
    ANGRY("화나요", "화난"),
    SURPRISED("놀랐어요", "놀란"),
    SCARED("무서워요", "무서운"),
}

/**
 * 감정 상황 이야기.
 * [tip]은 정답 뒤에 들려주는 공감·감정 조절 안내로, 보호자와 대화를 이어 가도록 돕습니다.
 */
data class Situation(
    val emoji: String,
    val text: String,
    val emotion: Emotion,
    val tip: String,
)

object EmotionContent {
    val situations: List<Situation> = listOf(
        Situation("🎁", "생일에 선물을 받았어요.", Emotion.HAPPY,
            "선물을 받으면 기뻐요. '고마워!' 하고 마음을 전해 볼까요?"),
        Situation("🐶", "강아지와 공놀이를 신나게 했어요.", Emotion.HAPPY,
            "신나게 놀면 기분이 좋아요. 나는 무엇을 할 때 기쁜가요?"),
        Situation("🤗", "가족이 나를 꼭 안아 주었어요.", Emotion.HAPPY,
            "꼭 안아 주면 마음이 따뜻하고 기뻐요."),
        Situation("🍦", "아이스크림을 바닥에 떨어뜨렸어요.", Emotion.SAD,
            "속상하고 슬플 수 있어요. 슬플 때는 '속상해' 하고 말해도 괜찮아요."),
        Situation("🎈", "풍선이 하늘로 날아가 버렸어요.", Emotion.SAD,
            "아끼던 것을 잃으면 슬퍼요. 슬플 때는 어른에게 안아 달라고 말해 봐요."),
        Situation("🌧️", "비가 와서 놀이터에 못 가요.", Emotion.SAD,
            "하고 싶은 걸 못 하면 슬퍼요. 대신 집에서 할 수 있는 놀이를 찾아볼까요?"),
        Situation("🏰", "친구가 내가 만든 모래성을 망가뜨렸어요.", Emotion.ANGRY,
            "화가 날 때는 숨을 크게 쉬어요. 후~ 그리고 '하지 마!' 하고 말로 이야기해요."),
        Situation("✏️", "친구가 내 그림에 마음대로 낙서했어요.", Emotion.ANGRY,
            "화가 나도 때리지 않아요. '내 그림이야, 속상해' 하고 말로 이야기해요."),
        Situation("🚂", "차례를 기다리는데 친구가 새치기했어요.", Emotion.ANGRY,
            "화가 날 때는 어른에게 도와 달라고 말해요. 차례를 지키면 모두 즐거워요."),
        Situation("🎉", "문을 열었더니 친구들이 '짠!' 하고 나타났어요.", Emotion.SURPRISED,
            "갑자기 일이 생기면 깜짝 놀라요. 놀라면서 기쁠 때도 있어요!"),
        Situation("🎩", "마술 모자에서 토끼가 쏙 나왔어요.", Emotion.SURPRISED,
            "생각하지 못한 일이 생기면 놀라요. 와, 신기하다!"),
        Situation("🎆", "갑자기 '펑!' 하고 불꽃이 터졌어요.", Emotion.SURPRISED,
            "큰 소리에 깜짝 놀랄 수 있어요. 가슴에 손을 얹고 천천히 숨 쉬어 봐요."),
        Situation("⛈️", "천둥이 '우르릉 쾅!' 하고 쳤어요.", Emotion.SCARED,
            "무서울 때는 어른 곁으로 가요. '무서워요' 하고 말해도 괜찮아요."),
        Situation("🌙", "밤에 불이 꺼져서 깜깜해요.", Emotion.SCARED,
            "깜깜하면 무서울 수 있어요. 좋아하는 인형을 꼭 안아 봐요."),
        Situation("🐕", "커다란 개가 '멍멍!' 하고 크게 짖었어요.", Emotion.SCARED,
            "무서울 때는 뛰지 말고 천천히 어른 곁으로 가요."),
    )
}

data class Sticker(
    val id: String,
    val emoji: String,
    val name: String,
)

object Stickers {
    val all: List<Sticker> = listOf(
        Sticker("lion", "🦁", "사자"),
        Sticker("tiger", "🐯", "호랑이"),
        Sticker("dog", "🐶", "강아지"),
        Sticker("cat", "🐱", "고양이"),
        Sticker("rabbit", "🐰", "토끼"),
        Sticker("bear", "🐻", "곰"),
        Sticker("panda", "🐼", "판다"),
        Sticker("koala", "🐨", "코알라"),
        Sticker("frog", "🐸", "개구리"),
        Sticker("monkey", "🐵", "원숭이"),
        Sticker("penguin", "🐧", "펭귄"),
        Sticker("chick", "🐤", "병아리"),
        Sticker("octopus", "🐙", "문어"),
        Sticker("whale", "🐳", "고래"),
        Sticker("dolphin", "🐬", "돌고래"),
        Sticker("butterfly", "🦋", "나비"),
        Sticker("ladybug", "🐞", "무당벌레"),
        Sticker("turtle", "🐢", "거북이"),
        Sticker("sunflower", "🌻", "해바라기"),
        Sticker("rainbow", "🌈", "무지개"),
        Sticker("star", "⭐", "별"),
        Sticker("moon", "🌙", "달"),
        Sticker("rocket", "🚀", "로켓"),
        Sticker("train", "🚂", "기차"),
        Sticker("strawberry", "🍓", "딸기"),
        Sticker("watermelon", "🍉", "수박"),
        Sticker("icecream", "🍦", "아이스크림"),
        Sticker("balloon", "🎈", "풍선"),
        Sticker("crown", "👑", "왕관"),
        Sticker("unicorn", "🦄", "유니콘"),
    )

    fun byId(id: String): Sticker? = all.firstOrNull { it.id == id }
}

/** 실로폰 따라 치기 노래. 음 번호 0 = 낮은 도 … 7 = 높은 도 */
data class Song(
    val title: String,
    val notes: List<Int>,
    /** 만 4세에게 들려주는 앞부분 길이 */
    val shortLength: Int,
)

object Songs {
    val noteNames: List<String> = listOf("도", "레", "미", "파", "솔", "라", "시", "도")

    val all: List<Song> = listOf(
        Song(
            title = "도레미 계단",
            notes = listOf(0, 1, 2, 3, 4, 5, 6, 7, 7, 6, 5, 4, 3, 2, 1, 0),
            shortLength = 8,
        ),
        Song(
            title = "반짝반짝 작은 별",
            notes = listOf(
                0, 0, 4, 4, 5, 5, 4, 3, 3, 2, 2, 1, 1, 0,
                4, 4, 3, 3, 2, 2, 1, 4, 4, 3, 3, 2, 2, 1,
                0, 0, 4, 4, 5, 5, 4, 3, 3, 2, 2, 1, 1, 0,
            ),
            shortLength = 14,
        ),
        Song(
            title = "비행기",
            notes = listOf(
                2, 1, 0, 1, 2, 2, 2, 1, 1, 1, 2, 4, 4,
                2, 1, 0, 1, 2, 2, 2, 1, 1, 2, 1, 0,
            ),
            shortLength = 13,
        ),
    )
}

/** 칭찬과 격려. 틀렸을 때도 부정적인 말 대신 다시 도전하도록 격려합니다. */
object Phrases {
    val praise: List<String> = listOf("잘했어요!", "맞았어요!", "최고예요!", "멋져요!", "대단해요!", "딩동댕!")
    val retry: List<String> = listOf("괜찮아요, 다시 해 볼까요?", "음~ 한 번 더 생각해 볼까요?", "아깝다! 다시 찾아봐요!")
}
