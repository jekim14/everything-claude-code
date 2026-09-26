package com.ssukssuk.playground.content

// 놀이에 쓰이는 그림 문자는 대부분 Android 8.0 이상 기본 글꼴에서 표시되는 이모지(Unicode 9 이하)만 사용합니다.

/**
 * 한글 자음과 그 자음으로 시작하는 낱말.
 *
 * 낱말은 첫 음절이 받침 없는 '자음+단모음'(고, 나, 도 …)인 것으로 골랐습니다. 한글을 처음 읽는 아이에게는
 * 낱자보다 CV 음절이 먼저 잡히기 때문입니다(Cho 2009). [sound]는 자음의 소리('그', '느' …)로,
 * 이름('기역')만으로는 소리를 알기 어렵다는 연구(Kim 2009; 최나야·이순형 2007)에 따라 함께 들려줍니다.
 * ㅇ은 첫소리에서 소리가 없으므로 [sound]가 비어 있습니다.
 */
data class Consonant(
    val letter: String,
    val name: String,
    val sound: String,
    val word: String,
    val emoji: String,
) {
    val isSilentInitial: Boolean get() = sound.isEmpty()
}

object HangulContent {
    val consonants: List<Consonant> = listOf(
        Consonant("ㄱ", "기역", "그", "고양이", "🐱"),
        Consonant("ㄴ", "니은", "느", "나비", "🦋"),
        Consonant("ㄷ", "디귿", "드", "도넛", "🍩"),
        Consonant("ㄹ", "리을", "르", "로켓", "🚀"),
        Consonant("ㅁ", "미음", "므", "무지개", "🌈"),
        Consonant("ㅂ", "비읍", "브", "바나나", "🍌"),
        Consonant("ㅅ", "시옷", "스", "사과", "🍎"),
        Consonant("ㅇ", "이응", "", "오리", "🦆"),
        Consonant("ㅈ", "지읒", "즈", "자동차", "🚗"),
        Consonant("ㅊ", "치읓", "츠", "치즈", "🧀"),
        Consonant("ㅋ", "키읔", "크", "코끼리", "🐘"),
        Consonant("ㅌ", "티읕", "트", "토끼", "🐰"),
        Consonant("ㅍ", "피읖", "프", "포도", "🍇"),
        Consonant("ㅎ", "히읗", "흐", "호랑이", "🐯"),
    )

    fun byLetter(letter: String): Consonant? = consonants.firstOrNull { it.letter == letter }

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

/**
 * 감정. [label]은 아이에게 들려주는 말, [adjective]는 '기쁜 마음'처럼 쓰는 꾸밈말입니다.
 * 순서는 유아가 감정 낱말을 익히는 순서(기쁨 → 슬픔·화남 → 무서움 → 놀람; Widen & Russell 2003)를 따릅니다.
 */
enum class Emotion(val label: String, val adjective: String) {
    HAPPY("기뻐요", "기쁜"),
    SAD("슬퍼요", "슬픈"),
    ANGRY("화나요", "화난"),
    SCARED("무서워요", "무서운"),
    SURPRISED("놀랐어요", "놀란"),
}

/**
 * 감정 상황 이야기.
 * [tip]은 정답 뒤에 들려주는 공감·감정 조절 안내로, 보호자와 대화를 이어 가도록 돕습니다.
 * [alsoOk]는 그렇게 느낄 수도 있는 다른 감정입니다. 감정에는 정답이 하나만 있지 않으므로
 * 이 감정을 고르면 틀렸다고 하지 않고 "그럴 수도 있어"라고 인정합니다.
 */
data class Situation(
    val emoji: String,
    val text: String,
    val emotion: Emotion,
    val tip: String,
    val alsoOk: Set<Emotion> = emptySet(),
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
            "속상하고 슬플 수 있어요. 슬플 때는 '속상해' 하고 말해도 괜찮아요.", setOf(Emotion.ANGRY)),
        Situation("🎈", "풍선이 하늘로 날아가 버렸어요.", Emotion.SAD,
            "아끼던 것을 잃으면 슬퍼요. 슬플 때는 어른에게 안아 달라고 말해 봐요."),
        Situation("🌧️", "비가 와서 놀이터에 못 가요.", Emotion.SAD,
            "하고 싶은 걸 못 하면 슬퍼요. 대신 집에서 할 수 있는 놀이를 찾아볼까요?", setOf(Emotion.ANGRY)),
        Situation("🏰", "친구가 내가 만든 모래성을 망가뜨렸어요.", Emotion.ANGRY,
            "화가 날 때는 숨을 크게 쉬어요. 후~ 그리고 '하지 마!' 하고 말로 이야기해요.", setOf(Emotion.SAD)),
        Situation("✏️", "친구가 내 그림에 마음대로 낙서했어요.", Emotion.ANGRY,
            "화가 나도 때리지 않아요. '내 그림이야, 속상해' 하고 말로 이야기해요.", setOf(Emotion.SAD)),
        Situation("🚂", "차례를 기다리는데 친구가 새치기했어요.", Emotion.ANGRY,
            "화가 날 때는 어른에게 도와 달라고 말해요. 차례를 지키면 모두 즐거워요."),
        Situation("⛈️", "천둥이 '우르릉 쾅!' 하고 쳤어요.", Emotion.SCARED,
            "무서울 때는 어른 곁으로 가요. '무서워요' 하고 말해도 괜찮아요.", setOf(Emotion.SURPRISED)),
        Situation("🌙", "밤에 불이 꺼져서 깜깜해요.", Emotion.SCARED,
            "깜깜하면 무서울 수 있어요. 좋아하는 인형을 꼭 안아 봐요."),
        Situation("🐕", "커다란 개가 '멍멍!' 하고 크게 짖었어요.", Emotion.SCARED,
            "무서울 때는 뛰지 말고 천천히 어른 곁으로 가요.", setOf(Emotion.SURPRISED)),
        Situation("🎉", "문을 열었더니 친구들이 '짠!' 하고 나타났어요.", Emotion.SURPRISED,
            "갑자기 일이 생기면 깜짝 놀라요. 놀라면서 기쁠 때도 있어요!", setOf(Emotion.HAPPY)),
        Situation("🎩", "마술 모자에서 토끼가 쏙 나왔어요.", Emotion.SURPRISED,
            "생각하지 못한 일이 생기면 놀라요. 와, 신기하다!", setOf(Emotion.HAPPY)),
        Situation("🎆", "갑자기 '펑!' 하고 불꽃이 터졌어요.", Emotion.SURPRISED,
            "큰 소리에 깜짝 놀랄 수 있어요. 가슴에 손을 얹고 천천히 숨 쉬어 봐요.", setOf(Emotion.SCARED)),
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

/**
 * 칭찬과 격려.
 *
 * "최고예요"처럼 아이를 평가하는 말 대신, 아이가 한 일(잘 보기, 차근차근 세기, 다시 생각하기)을 짚어 주는
 * 과정 칭찬을 씁니다. 과정 칭찬을 들은 4세 아이가 실수 뒤에도 더 끈기 있게 도전했습니다(Cimpian 외 2007).
 */
object Phrases {
    /** 첫 시도에 맞혔을 때 */
    val praiseFirstTry: List<String> = listOf(
        "딩동댕! 잘 보고 골랐구나.",
        "딩동댕! 차근차근 찾았네.",
        "맞았어! 끝까지 잘 들었구나.",
        "딩동댕! 꼼꼼하게 살펴봤네.",
    )

    /** 한 번 이상 틀린 뒤 다시 생각해서 맞혔을 때 */
    val praiseAfterRetry: List<String> = listOf(
        "딩동댕! 다시 생각해서 찾아냈구나.",
        "딩동댕! 포기하지 않고 찾았네.",
        "맞았어! 한 번 더 해 보니 됐지?",
    )

    val retry: List<String> = listOf("괜찮아, 다시 해 볼까?", "음~ 한 번 더 생각해 볼까?", "아깝다! 다시 찾아볼까?")

    /** 판을 마쳤을 때 큰 글씨로 보여 주고 읽어 주는 말 */
    fun roundHeadline(hadRetry: Boolean, freePlay: Boolean): String = when {
        freePlay -> "마음껏 해 봤구나!"
        hadRetry -> "다시 생각하면서 끝까지 해냈구나!"
        else -> "차근차근 끝까지 해냈구나!"
    }

    val allRoundHeadlines: List<String> = listOf(
        roundHeadline(hadRetry = false, freePlay = true),
        roundHeadline(hadRetry = true, freePlay = false),
        roundHeadline(hadRetry = false, freePlay = false),
    )
}
