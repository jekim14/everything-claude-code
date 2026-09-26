package com.ssukssuk.playground.core

/**
 * 2019 개정 누리과정의 5개 영역.
 * [categories]는 각 영역의 내용 범주입니다.
 *
 * 색은 영역마다 네 가지를 씁니다: [colorArgb] 아이콘·강조, [softArgb] 타일 바탕,
 * [shadowArgb] 입체 그림자, [deepArgb] 흰 글씨를 올리는 진한 색(대비 4.5:1 이상).
 */
enum class Domain(
    val title: String,
    val colorArgb: Long,
    val softArgb: Long,
    val shadowArgb: Long,
    val deepArgb: Long,
    val categories: List<String>,
) {
    PHYSICAL(
        title = "신체운동·건강",
        colorArgb = 0xFFC98200,
        softArgb = 0xFFFFF0CC,
        shadowArgb = 0xFFF2CE7E,
        deepArgb = 0xFF9A6300,
        categories = listOf("신체활동 즐기기", "건강하게 생활하기", "안전하게 생활하기"),
    ),
    COMMUNICATION(
        title = "의사소통",
        colorArgb = 0xFFE0603E,
        softArgb = 0xFFFFE4DA,
        shadowArgb = 0xFFF4B5A2,
        deepArgb = 0xFFC8472A,
        categories = listOf("듣기와 말하기", "읽기와 쓰기에 관심 가지기", "책과 이야기 즐기기"),
    ),
    SOCIAL(
        title = "사회관계",
        colorArgb = 0xFFD9558A,
        softArgb = 0xFFFDE1EC,
        shadowArgb = 0xFFF3AFC8,
        deepArgb = 0xFFB83A6A,
        categories = listOf("나를 알고 존중하기", "더불어 생활하기", "사회에 관심 가지기"),
    ),
    ART(
        title = "예술경험",
        colorArgb = 0xFF7656D6,
        softArgb = 0xFFECE5FF,
        shadowArgb = 0xFFC6B6F0,
        deepArgb = 0xFF5A3FB0,
        categories = listOf("아름다움 찾아보기", "창의적으로 표현하기", "예술 감상하기"),
    ),
    NATURE(
        title = "자연탐구",
        colorArgb = 0xFF13978B,
        softArgb = 0xFFD6F3EF,
        shadowArgb = 0xFF9FD9D1,
        deepArgb = 0xFF117A70,
        categories = listOf("탐구과정 즐기기", "생활 속에서 탐구하기", "자연과 더불어 살기"),
    ),
}

/**
 * 앱에 들어 있는 놀이 목록. 홈 화면에는 이 순서(영역별)로 나옵니다.
 *
 * @property icon 보호자 화면 목록에 쓰는 그림 문자 (아이 화면 타일은 [com.ssukssuk.playground.ui.components.GameIcon]이 그립니다)
 * @property curriculum 관련 누리과정 내용(보호자·교사용 설명)
 * @property skill 보호자 성장 기록에 쓰는 '할 수 있게 된 것' 문구
 */
enum class Game(
    val id: String,
    val title: String,
    val icon: String,
    val domain: Domain,
    val curriculum: String,
    val skill: String,
) {
    HANGUL(
        id = "hangul",
        title = "한글 놀이",
        icon = "가",
        domain = Domain.COMMUNICATION,
        curriculum = "주변의 상징, 글자 등의 읽기에 관심을 가진다 · 말과 글의 관계에 관심을 가진다",
        skill = "자음 이름과 첫소리 알기",
    ),
    SYLLABLE(
        id = "syllable",
        title = "글자 만들기",
        icon = "ㄱ+ㅏ",
        domain = Domain.COMMUNICATION,
        curriculum = "말과 글의 관계에 관심을 가진다 · 자신의 생각을 글자와 비슷한 형태로 표현한다",
        skill = "자음과 모음을 합쳐 글자 만들기",
    ),
    COUNTING(
        id = "counting",
        title = "숫자 세기",
        icon = "🍎",
        domain = Domain.NATURE,
        curriculum = "물체를 세어 수량을 알아본다",
        skill = "물건을 세고 '모두 몇 개' 말하기",
    ),
    NUMBER_PATH(
        id = "numberpath",
        title = "숫자 징검다리",
        icon = "🐸",
        domain = Domain.NATURE,
        curriculum = "물체를 세어 수량을 알아본다 · 수의 순서와 크기를 비교한다",
        skill = "수의 순서 알고 이어 세기",
    ),
    SHAPES(
        id = "shapes",
        title = "모양 맞추기",
        icon = "🔺",
        domain = Domain.NATURE,
        curriculum = "물체의 위치와 방향, 모양을 알고 구별한다 · 신체 움직임을 조절한다(소근육)",
        skill = "모양을 보고 같은 모양 찾기",
    ),
    PATTERN(
        id = "pattern",
        title = "규칙 찾기",
        icon = "🐞",
        domain = Domain.NATURE,
        curriculum = "주변에서 반복되는 규칙을 찾는다",
        skill = "반복되는 규칙 이어 가기",
    ),
    MEMORY(
        id = "memory",
        title = "짝꿍 카드",
        icon = "🃏",
        domain = Domain.NATURE,
        curriculum = "궁금한 것을 탐구하는 과정에 즐겁게 참여한다 · 기억하고 비교하기",
        skill = "본 것을 기억하고 짝 찾기",
    ),
    EMOTION(
        id = "emotion",
        title = "기분 친구",
        icon = "😊",
        domain = Domain.SOCIAL,
        curriculum = "나의 감정을 알고 상황에 맞게 표현한다 · 서로 다른 감정, 생각, 행동을 존중한다",
        skill = "상황에 맞는 마음 알아차리기",
    ),
    STOP_GO(
        id = "stopgo",
        title = "멈춰! 놀이",
        icon = "🚦",
        domain = Domain.PHYSICAL,
        curriculum = "신체 움직임을 조절한다 · 규칙을 기억하고 스스로 멈춰 본다(자기조절)",
        skill = "규칙을 기억하고 멈추기",
    ),
    MOVEMENT(
        id = "movement",
        title = "쑥쑥 체조",
        icon = "🤸",
        domain = Domain.PHYSICAL,
        curriculum = "신체 움직임을 조절한다 · 기본 운동 능력을 발휘한다 · 실내외 신체활동에 자발적으로 참여한다",
        skill = "동작을 보고 따라 하기",
    ),
    BALLOON(
        id = "balloon",
        title = "색깔 풍선",
        icon = "🎈",
        domain = Domain.ART,
        curriculum = "예술적 요소(색)에 관심을 가지고 찾아본다",
        skill = "색 이름 알고 찾기",
    ),
    DRAWING(
        id = "drawing",
        title = "그림 그리기",
        icon = "🖍️",
        domain = Domain.ART,
        curriculum = "다양한 미술 재료와 도구로 자신의 생각과 느낌을 표현한다",
        skill = "생각을 그림으로 표현하기",
    ),
    XYLOPHONE(
        id = "xylophone",
        title = "실로폰",
        icon = "🎵",
        domain = Domain.ART,
        curriculum = "신체, 사물, 악기로 간단한 소리와 리듬을 만들어 본다 · 노래를 즐겨 부른다",
        skill = "소리와 리듬 만들기",
    ),
    ;

    /** 놀이 화면 제목 띠에 쓰는 색 (영역의 진한 색) */
    val colorArgb: Long get() = domain.deepArgb

    companion object {
        fun fromId(id: String): Game? = entries.firstOrNull { it.id == id }
    }
}
