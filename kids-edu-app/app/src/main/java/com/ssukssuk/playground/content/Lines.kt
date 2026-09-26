package com.ssukssuk.playground.content

import com.ssukssuk.playground.core.Game

/**
 * 앱이 소리 내어 읽는 모든 문장.
 *
 * 미리 녹음한 고품질 음성 파일 목록([VoiceScript])이 이 파일에서 만들어지므로, 새 안내를 추가할 때는
 * 화면 코드에 글을 직접 쓰지 말고 여기에 함수나 값을 추가한 뒤 [VoiceScript]에도 넣어 주세요.
 * 음성 파일은 문장(. ! ?) 단위로 나뉘어 저장되고, 이어서 재생됩니다.
 *
 * 말투: 쑥쑥이가 친구에게 말하듯 칭찬·인사는 반말, 놀이 안내는 부드러운 해요체를 씁니다.
 * 숫자는 음성 엔진이 '사', '오'처럼 한자어로 읽지 않도록 글자(넷, 다섯)로 씁니다.
 */
object Lines {
    // ── 이름 ────────────────────────────────────────────────
    /** 이름이 들어가는 문장 틀. {A}는 부르는 말(하늘아, 민서야)로 바뀝니다. */
    const val NAME_TOKEN = "{A}"
    const val HELLO_NAME_TEMPLATE = "안녕, {A}!"
    const val CALL_NAME_TEMPLATE = "{A}!"
    val nameTemplates: List<String> = listOf(HELLO_NAME_TEMPLATE, CALL_NAME_TEMPLATE)

    fun helloName(name: String) = HELLO_NAME_TEMPLATE.replace(NAME_TOKEN, Korean.vocative(name))
    fun callName(name: String) = CALL_NAME_TEMPLATE.replace(NAME_TOKEN, Korean.vocative(name))

    /** 이름이 있으면 "하늘아! " 을 앞에 붙입니다. */
    fun withName(name: String, text: String) = if (name.isBlank()) text else "${callName(name)} $text"

    // ── 첫 화면·홈 ────────────────────────────────────────────
    const val ONBOARDING_HELLO =
        "안녕! 나는 새싹 친구 쑥쑥이야. 너는 몇 살이니? 네 살이면 넷, 다섯 살이면 다섯을 눌러 줘!"

    fun onboardingAge(age: Int) = "${Korean.counterNumber(age)} 살이구나! 반가워! 우리 같이 놀자!"

    val homeGreetings: List<String> = listOf(
        "오늘은 뭐 하고 놀까?",
        "하고 싶은 놀이를 눌러 봐!",
        "나는 쑥쑥이야! 같이 놀자!",
        "무엇이든 좋아! 골라 볼까?",
    )

    fun homeHello(name: String, greeting: String) =
        if (name.isBlank()) "안녕! $greeting" else "${helloName(name)} $greeting"

    const val STICKER_BOOK = "스티커 책"
    const val STICKER_BOOK_EMPTY = "놀이를 하다 보면 깜짝 선물이 올 수도 있어요!"
    const val STICKER_BOOK_INTRO = "스티커 책이에요. 눌러서 이름을 들어 봐요."
    const val STICKER_LOCKED = "아직 만나지 못한 친구예요."

    /** 놀이 이름. "멈춰! 놀이"처럼 느낌표가 있으면 한 번에 읽도록 빼고 읽습니다. */
    fun gameTitle(game: Game) = game.title.replace("!", "")

    const val REST_SOON = "이번 놀이가 끝나면 쉬는 시간이에요."

    // ── 칭찬·축하 ─────────────────────────────────────────────
    const val GIFT_PROMPT = "깜짝 선물! 하나 골라 볼까?"

    fun giftPicked(sticker: Sticker) = "${sticker.name} 스티커! 스티커 책에 붙였어요."

    fun celebration(name: String, headline: String) = withName(name, headline)

    // ── 쉬는 시간 ─────────────────────────────────────────────
    const val REST_BREAK = "잠깐 쉬어 갈까? 물 한 모금 마시고, 창밖 먼 곳을 바라봐요. 기지개도 쭉!"
    const val REST_DAY_DONE = "오늘 놀이는 여기까지! 이제 몸으로 놀아 볼까?"

    fun restBreak(name: String) = withName(name, REST_BREAK)
    fun restDayDone(name: String) = withName(name, REST_DAY_DONE)

    fun offlineIdea(idea: OfflineIdea) = "${idea.title}!"

    // ── 한글 놀이 ─────────────────────────────────────────────
    const val HANGUL_MODE = "한글 놀이! 글자 카드를 볼까요, 글자 찾기 놀이를 할까요?"
    const val HANGUL_MODE_CARDS = "글자 카드!"
    const val HANGUL_MODE_QUIZ = "글자 찾기!"
    const val HANGUL_TRACE = "손가락으로 글자를 따라 써 볼까요?"

    /** 이름과 소리를 함께: "기역! 기역은 '그' 소리. 고양이의 '고'!" */
    fun consonantCard(c: Consonant): String {
        val sound = if (c.isSilentInitial) {
            "${Korean.eunNeun(c.name)} 첫소리에서 소리가 없어요."
        } else {
            "${Korean.eunNeun(c.name)} '${c.sound}' 소리."
        }
        return "${c.name}! $sound ${c.word}의 '${c.word.first()}'!"
    }

    fun consonantName(c: Consonant) = c.name
    fun word(word: String) = word

    fun hangulQuizPrompt(c: Consonant) = if (c.isSilentInitial) {
        "${c.name}! ${Korean.euro(c.name)} 시작하는 그림을 찾아볼까요?"
    } else {
        "${c.name}! '${c.sound}' 소리로 시작하는 그림을 찾아볼까요?"
    }

    fun hangulQuizCorrect(praise: String, target: Consonant, choice: Consonant) =
        "$praise ${Korean.eunNeun(choice.word)} ${Korean.euro(target.name)} 시작해요!"

    fun hangulQuizWrong(choice: Consonant) =
        "${Korean.eunNeun(choice.word)} ${Korean.euro(choice.name)} 시작해요. 다시 찾아볼까요?"

    // ── 글자 만들기 ───────────────────────────────────────────
    fun syllablePrompt(w: SyllableWord) = "${w.word}의 '${w.syllable}'를 만들어 볼까?"

    fun syllableConsonantHint(c: Consonant) = if (c.isSilentInitial) {
        "${Korean.eunNeun(c.name)} 첫소리에서 소리가 없어요."
    } else {
        "${Korean.eunNeun(c.name)} '${c.sound}' 소리예요."
    }

    /** 자음 조각을 눌렀을 때: "니은, 느!" */
    fun syllableTapConsonant(c: Consonant) = if (c.isSilentInitial) "${c.name}!" else "${c.name}, ${c.sound}!"

    fun syllableTapVowel(v: Vowel) = "${v.sound}!"

    /** 합치는 소리: "느, 아, 나!" */
    fun syllableBlend(c: Consonant, v: Vowel): String {
        val s = Korean.compose(c.letter.single(), v.letter)
        return if (c.isSilentInitial) "소리 없는 ${Korean.waGwa(c.name)} ${v.sound}, $s!" else "${c.sound}, ${v.sound}, $s!"
    }

    fun syllableCorrect(praise: String, w: SyllableWord) = "$praise ${w.word}의 '${w.syllable}'!"

    fun syllableMade(made: Char) = "'$made'가 됐네!"

    fun syllableNeedConsonant(w: SyllableWord) = if (w.initial.isSilentInitial) {
        "'${w.syllable}'를 만들려면 소리 없는 ${Korean.iGa(w.initial.name)} 필요해."
    } else {
        "'${w.syllable}'를 만들려면 '${w.initial.sound}' 소리가 필요해."
    }

    fun syllableNeedVowel(w: SyllableWord) = "'${w.syllable}'를 만들려면 '${w.vowel.sound}' 소리가 필요해."

    // ── 숫자 세기 ─────────────────────────────────────────────
    fun countingPrompt(item: CountItem) = "${Korean.iGa(item.name)} 몇 ${item.counter}일까요? 하나씩 눌러서 세어 봐요!"

    fun count(n: Int) = Korean.countWord(n)

    /** "하나, 둘, 셋." */
    fun countSequence(n: Int) = (1..n).joinToString(", ") { Korean.countWord(it) } + "."

    /** 기수 원리: 마지막에 센 수가 전체 개수 — "모두 세 개!" */
    fun countTotal(n: Int, counter: String) = "모두 ${Korean.counterNumber(n)} $counter!"

    fun countingAllCounted(n: Int, item: CountItem) =
        "${Korean.countWord(n)}! 다 세었어요. 몇 ${item.counter}인지 숫자를 눌러 볼까요?"

    fun countingCorrect(praise: String, n: Int, item: CountItem) =
        "$praise ${countSequence(n)} ${countTotal(n, item.counter)}"

    const val COUNTING_RETRY = "음~ 하나씩 눌러서 같이 다시 세어 볼까?"

    /** 두 번 틀리면 같이 세어 보여 줍니다(시범). */
    fun countingDemo(n: Int, item: CountItem) =
        "같이 세어 보자. ${countSequence(n)} ${countTotal(n, item.counter)} 알맞은 숫자를 찾아볼까?"

    // ── 숫자 징검다리 ─────────────────────────────────────────
    const val PATH_START = "하나부터 열까지 징검다리를 건너 볼까? 주사위를 톡 눌러 봐!"
    const val PATH_ROLL = "주사위를 톡 눌러 봐!"

    fun pathRolled(n: Int) = "${Korean.countWord(n)}! ${Korean.counterNumber(n)} 칸 가요."

    /** 칸에 내려앉을 때 그 칸의 수를 말합니다(이어 세기). */
    fun pathStone(n: Int) = "${Korean.countWord(n)}!"

    const val PATH_WRONG_STONE = "한 칸씩 차례대로 뛰어요. 반짝이는 돌을 눌러 볼까?"
    const val PATH_ASK = "몇에 도착했을까?"

    fun pathArrived(praise: String, n: Int) = "$praise ${Korean.countWord(n)}에 도착했어!"

    fun pathArrivalRetry(from: Int, steps: Int) = if (from <= 0) {
        "출발점에서 ${Korean.counterNumber(steps)} 칸 갔어. 다시 세어 볼까?"
    } else {
        "${Korean.countWord(from)}에서 ${Korean.counterNumber(steps)} 칸 더 갔어. 다시 세어 볼까?"
    }

    const val PATH_FINISH = "열에 도착! 징검다리를 다 건넜어!"

    // ── 멈춰! 놀이 ────────────────────────────────────────────
    const val STOP_RULE = "초록 친구가 나오면 톡! 빨간 친구가 나오면 멈춰!"
    const val STOP_RULE_REVERSED = "이번엔 반대로! 빨간 친구를 톡, 초록 친구는 멈춰!"
    const val STOP_READY = "준비, 시작!"
    const val STOP_OOPS_RED = "앗, 빨간 친구는 멈춰!"
    const val STOP_OOPS_GREEN = "앗, 이번엔 초록 친구가 멈춰!"
    const val STOP_DONE = "끝까지 규칙을 기억했어!"

    // ── 기분 친구 ─────────────────────────────────────────────
    fun emotionPrompt(s: Situation) = "${s.text} 친구의 기분은 어떨까요?"

    fun emotionName(e: Emotion) = "${e.label}!"

    fun emotionCorrect(praise: String, e: Emotion, s: Situation) = "$praise ${e.adjective} 마음이 들어요. ${s.tip}"

    /** 그렇게 느낄 수도 있는 다른 감정을 골랐을 때 */
    fun emotionAlsoOk(chosen: Emotion, s: Situation) =
        "그럴 수도 있어! ${chosen.adjective} 마음이 들 수도 있지. 많은 친구들은 ${s.emotion.adjective} 마음이 들어요. ${s.tip}"

    fun emotionWrong(chosen: Emotion) = "${chosen.label}? 이 친구는 다른 마음일 것 같아. 얼굴을 잘 보고 다시 골라 볼까?"

    const val BREATHE_INVITE = "거북이처럼 천천히 숨 쉬어 볼까?"
    const val BREATHE_IN = "코로 숨을 들이마시고."
    const val BREATHE_OUT = "입으로 후~ 내쉬어요."
    const val BREATHE_DONE = "마음이 조금 편안해졌지?"

    // ── 규칙 찾기 ─────────────────────────────────────────────
    const val PATTERN_RETRY = "음~ 다시 같이 읽어 볼까요?"

    /** 규칙을 읽어 줍니다: "사과. 바나나. 사과. 바나나. 다음은 뭘까요?" */
    fun patternRead(lead: String, items: List<PatternItem>, withAnswer: Boolean): String {
        val names = if (withAnswer) {
            items.dropLast(1).joinToString(" ") { "${it.name}." } + " ${items.last().name}!"
        } else {
            items.joinToString(" ") { "${it.name}." } + " 다음은 뭘까요?"
        }
        return if (lead.isEmpty()) names.trim() else "$lead ${names.trim()}"
    }

    // ── 모양 맞추기 ───────────────────────────────────────────
    const val SHAPE_PROMPT = "모양 조각을 손가락으로 끌어서 같은 모양 자리에 쏙 넣어 줘요!"

    fun shapeAllDone(praise: String) = "$praise 모두 제자리를 찾았어요!"
    fun shapeFit(kind: ShapeKind) = "${kind.label}! 딱 맞아요!"
    fun shapeWrong(kind: ShapeKind) = "${Korean.eunNeun(kind.label)} 모양이 달라요. ${kind.label} 자리를 찾아볼까요?"
    fun shapeName(kind: ShapeKind) = kind.label

    // ── 짝꿍 카드 ─────────────────────────────────────────────
    const val MEMORY_PROMPT = "똑같은 그림 짝꿍을 찾아봐요!"
    const val MEMORY_PREVIEW = "그림을 잘 보고 기억해요!"

    fun memoryFace(face: MemoryFace) = face.name
    fun memoryAllDone(praise: String) = "$praise 짝꿍을 모두 찾았어요!"
    fun memoryMatch(face: MemoryFace) = "짝꿍을 찾았어요! ${face.name}!"
    fun memoryMismatch(face: MemoryFace) = "${face.name}! 짝꿍이 아니에요. 다시 찾아봐요."

    // ── 색깔 풍선 ─────────────────────────────────────────────
    fun balloonPrompt(target: KidColor) = "${target.adjective} 풍선을 찾아서 톡 터뜨려 볼까요?"
    fun balloonRoundDone(target: KidColor, n: Int) =
        "와! ${target.adjective} 풍선을 ${Korean.counterNumber(n)} 개 다 터뜨렸어요!"

    fun balloonCount(n: Int) = "${Korean.countWord(n)}!"
    fun balloonWrong(popped: KidColor, target: KidColor) =
        "이건 ${popped.name} 풍선이에요. ${target.adjective} 풍선을 찾아봐요!"

    // ── 그림 그리기 ───────────────────────────────────────────
    const val DRAW_INTRO = "손가락으로 마음껏 그려 봐요! 왼쪽 동그라미를 누르면 색깔이 바뀌어요."
    const val DRAW_RAINBOW = "무지개 붓!"
    const val DRAW_STAMP = "도장을 콕콕 찍어 봐요!"
    const val DRAW_ERASER = "지우개!"
    const val DRAW_EMPTY = "먼저 그림을 그려 볼까요?"
    const val DRAW_DONE = "와, 마음껏 그렸구나! 무엇을 그렸는지 이야기해 줄래?"
    val crayonNames: List<String> =
        listOf("빨간색", "주황색", "노란색", "초록색", "하늘색", "파란색", "보라색", "분홍색", "갈색", "검은색")

    // ── 실로폰 ────────────────────────────────────────────────
    const val XYLO_INTRO = "실로폰을 톡톡 쳐 봐요! 노래를 고르면 반짝이는 막대를 따라 칠 수 있어요."
    const val XYLO_FREE = "자유롭게 연주해 봐요!"
    const val XYLO_DONE = "여러 소리를 만들어 봤구나! 어떤 소리가 좋았어?"

    fun xyloSong(song: Song) = "${song.title}! 반짝이는 막대를 따라 쳐 봐요."
    fun xyloSongDone(song: Song) = "와! ${song.title} 연주를 끝까지 했어요!"

    // ── 쑥쑥 체조 ─────────────────────────────────────────────
    const val MOVE_INTRO =
        "쑥쑥이랑 같이 몸을 움직여요! 먼저 주변에 부딪힐 물건이 없는지 살펴봐요. 준비되면 시작을 눌러요!"

    fun move(m: MoveText) = "${m.title} ${m.instruction}"
    fun moveInstruction(m: MoveText) = m.instruction

    val movePraise: List<String> = listOf("몸을 쭉쭉 잘 움직였어!", "끝까지 따라 했구나!", "힘차게 잘했어!")
}
