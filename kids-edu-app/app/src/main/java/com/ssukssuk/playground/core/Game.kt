package com.ssukssuk.playground.core

/**
 * 2019 개정 누리과정의 5개 영역.
 * [categories]는 각 영역의 내용 범주입니다.
 */
enum class Domain(
    val title: String,
    val colorArgb: Long,
    val categories: List<String>,
) {
    PHYSICAL(
        title = "신체운동·건강",
        colorArgb = 0xFF66BB6A,
        categories = listOf("신체활동 즐기기", "건강하게 생활하기", "안전하게 생활하기"),
    ),
    COMMUNICATION(
        title = "의사소통",
        colorArgb = 0xFFFFA726,
        categories = listOf("듣기와 말하기", "읽기와 쓰기에 관심 가지기", "책과 이야기 즐기기"),
    ),
    SOCIAL(
        title = "사회관계",
        colorArgb = 0xFFEF5350,
        categories = listOf("나를 알고 존중하기", "더불어 생활하기", "사회에 관심 가지기"),
    ),
    ART(
        title = "예술경험",
        colorArgb = 0xFFAB47BC,
        categories = listOf("아름다움 찾아보기", "창의적으로 표현하기", "예술 감상하기"),
    ),
    NATURE(
        title = "자연탐구",
        colorArgb = 0xFF29B6F6,
        categories = listOf("탐구과정 즐기기", "생활 속에서 탐구하기", "자연과 더불어 살기"),
    ),
}

/**
 * 앱에 들어 있는 놀이 목록.
 *
 * @property icon 홈 화면 타일에 보이는 그림 문자
 * @property spokenIntro 놀이를 시작할 때 들려주는 안내 음성
 * @property curriculum 관련 누리과정 내용(보호자·교사용 설명)
 */
enum class Game(
    val id: String,
    val title: String,
    val icon: String,
    val domain: Domain,
    val colorArgb: Long,
    val spokenIntro: String,
    val curriculum: String,
) {
    HANGUL(
        id = "hangul",
        title = "한글 놀이",
        icon = "가",
        domain = Domain.COMMUNICATION,
        colorArgb = 0xFFFFB74D,
        spokenIntro = "한글 놀이! 글자와 그림 친구를 만나 볼까요?",
        curriculum = "주변의 상징, 글자 등의 읽기에 관심을 가진다 · 말과 글의 관계에 관심을 가진다",
    ),
    COUNTING(
        id = "counting",
        title = "숫자 세기",
        icon = "🍎",
        domain = Domain.NATURE,
        colorArgb = 0xFFFF8A80,
        spokenIntro = "숫자 세기! 하나, 둘, 셋, 같이 세어 봐요!",
        curriculum = "물체를 세어 수량을 알아본다",
    ),
    BALLOON(
        id = "balloon",
        title = "색깔 풍선",
        icon = "🎈",
        domain = Domain.ART,
        colorArgb = 0xFFF48FB1,
        spokenIntro = "색깔 풍선! 말하는 색깔 풍선을 톡 터뜨려요!",
        curriculum = "예술적 요소(색)에 관심을 가지고 찾아본다",
    ),
    SHAPES(
        id = "shapes",
        title = "모양 맞추기",
        icon = "🔺",
        domain = Domain.NATURE,
        colorArgb = 0xFF81D4FA,
        spokenIntro = "모양 맞추기! 같은 모양 집을 찾아 쏙 넣어 줘요!",
        curriculum = "물체의 위치와 방향, 모양을 알고 구별한다 · 신체 움직임을 조절한다(소근육)",
    ),
    MEMORY(
        id = "memory",
        title = "짝꿍 카드",
        icon = "🃏",
        domain = Domain.NATURE,
        colorArgb = 0xFFB39DDB,
        spokenIntro = "짝꿍 카드! 똑같은 그림 짝꿍을 찾아봐요!",
        curriculum = "궁금한 것을 탐구하는 과정에 즐겁게 참여한다 · 기억하고 비교하기",
    ),
    PATTERN(
        id = "pattern",
        title = "규칙 찾기",
        icon = "🐞",
        domain = Domain.NATURE,
        colorArgb = 0xFFA5D6A7,
        spokenIntro = "규칙 찾기! 다음에는 누가 올까요?",
        curriculum = "주변에서 반복되는 규칙을 찾는다",
    ),
    EMOTION(
        id = "emotion",
        title = "기분 친구",
        icon = "😊",
        domain = Domain.SOCIAL,
        colorArgb = 0xFFFFE082,
        spokenIntro = "기분 친구! 친구의 마음을 알아볼까요?",
        curriculum = "나의 감정을 알고 상황에 맞게 표현한다 · 서로 다른 감정, 생각, 행동을 존중한다",
    ),
    DRAWING(
        id = "drawing",
        title = "그림 그리기",
        icon = "🖍️",
        domain = Domain.ART,
        colorArgb = 0xFF80CBC4,
        spokenIntro = "그림 그리기! 마음껏 그려 봐요!",
        curriculum = "다양한 미술 재료와 도구로 자신의 생각과 느낌을 표현한다",
    ),
    XYLOPHONE(
        id = "xylophone",
        title = "실로폰",
        icon = "🎵",
        domain = Domain.ART,
        colorArgb = 0xFFCE93D8,
        spokenIntro = "알록달록 실로폰! 도레미 소리를 만들어 봐요!",
        curriculum = "신체, 사물, 악기로 간단한 소리와 리듬을 만들어 본다 · 노래를 즐겨 부른다",
    ),
    MOVEMENT(
        id = "movement",
        title = "쑥쑥 체조",
        icon = "🤸",
        domain = Domain.PHYSICAL,
        colorArgb = 0xFFC5E1A5,
        spokenIntro = "쑥쑥 체조! 쑥쑥이랑 같이 몸을 움직여요!",
        curriculum = "신체 움직임을 조절한다 · 기본 운동 능력을 발휘한다 · 실내외 신체활동에 자발적으로 참여한다",
    ),
    ;

    companion object {
        fun fromId(id: String): Game? = entries.firstOrNull { it.id == id }
    }
}
