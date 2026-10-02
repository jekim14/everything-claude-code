package com.ssukssuk.playground.content

import com.ssukssuk.playground.core.Difficulty
import com.ssukssuk.playground.core.Game

/**
 * 음성 파일 이름 규칙.
 *
 * 안내 문장을 문장 부호(. ! ?) 뒤에서 나누고, 나눈 문장마다 FNV-1a 64비트 해시로 파일 이름을 정합니다.
 * 같은 규칙이 음성 생성 스크립트(tools/make-voice.mjs)에도 들어 있어 두 쪽이 같은 이름을 씁니다.
 */
object VoiceKey {
    private val boundary = Regex("(?<=[.!?])\\s+")
    private val spaces = Regex("\\s+")

    fun normalize(text: String): String = text.trim().replace(spaces, " ")

    fun segments(text: String): List<String> =
        normalize(text).split(boundary).map { it.trim() }.filter { it.isNotEmpty() }

    fun hash(segment: String): String {
        var h = FNV_OFFSET
        for (b in normalize(segment).toByteArray(Charsets.UTF_8)) {
            h = h xor (b.toLong() and 0xFF)
            h *= FNV_PRIME
        }
        return java.lang.Long.toUnsignedString(h, 16).padStart(16, '0')
    }

    fun fileName(segment: String): String = "v_${hash(segment)}.mp3"

    private const val FNV_OFFSET = -0x340d631b7bdddcdbL // 0xcbf29ce484222325
    private const val FNV_PRIME = 0x100000001b3L
}

/**
 * 앱이 말할 수 있는 모든 문장을 모읍니다. 이 목록으로 고품질 음성 파일을 미리 만들어 두고,
 * 파일이 있으면 그 음성을, 없으면 기기의 음성 합성(TTS)을 씁니다.
 *
 * 목록은 kids-edu-app/voice/lines.txt로 내보내며, 단위 테스트가 코드와 파일이 같은지 확인합니다.
 */
object VoiceScript {
    const val NAME_PREFIX = "@name "

    fun utterances(): List<String> = buildList {
        val praises = Phrases.praiseFirstTry + Phrases.praiseAfterRetry
        addAll(praises)
        addAll(Phrases.retry)
        addAll(Phrases.allRoundHeadlines)

        // 첫 화면·홈·스티커·쉬는 시간
        add(Lines.ONBOARDING_HELLO)
        Difficulty.SUPPORTED_AGES.forEach { add(Lines.onboardingAge(it)) }
        Lines.homeGreetings.forEach { add(Lines.homeHello("", it)) }
        Game.entries.forEach { add(Lines.gameTitle(it)) }
        add(Lines.STICKER_BOOK)
        add(Lines.STICKER_BOOK_EMPTY)
        add(Lines.STICKER_BOOK_INTRO)
        add(Lines.STICKER_LOCKED)
        add(Lines.REST_SOON)
        add(Lines.GIFT_PROMPT)
        Stickers.all.forEach {
            add(it.name)
            add(Lines.giftPicked(it))
        }
        add(Lines.restBreak(""))
        add(Lines.restDayDone(""))
        OfflineIdeas.all.forEach { add(Lines.offlineIdea(it)) }

        // 한글 놀이
        add(Lines.HANGUL_MODE)
        add(Lines.HANGUL_MODE_CARDS)
        add(Lines.HANGUL_MODE_QUIZ)
        add(Lines.HANGUL_TRACE)
        HangulContent.consonants.forEach { c ->
            add(Lines.consonantCard(c))
            add(Lines.consonantName(c))
            add(Lines.word(c.word))
            add(Lines.hangulQuizPrompt(c))
            add(Lines.hangulQuizCorrect("", c, c))
            add(Lines.hangulQuizWrong(c))
        }

        // 글자 만들기
        HangulContent.consonants.forEach { c ->
            add(Lines.syllableConsonantHint(c))
            add(Lines.syllableTapConsonant(c))
            SyllableContent.vowels.forEach { v ->
                add(Lines.syllableBlend(c, v))
                Korean.compose(c.letter.single(), v.letter)?.let { add(Lines.syllableMade(it)) }
            }
        }
        SyllableContent.vowels.forEach { add(Lines.syllableTapVowel(it)) }
        SyllableContent.words.forEach { w ->
            add(Lines.syllablePrompt(w))
            add(Lines.syllableCorrect("", w))
            add(Lines.syllableNeedConsonant(w))
            add(Lines.syllableNeedVowel(w))
            add(Lines.word(w.word))
        }

        // 숫자 세기
        CountingContent.items.forEach { item ->
            add(Lines.countingPrompt(item))
            for (n in 1..MAX_COUNT) {
                add(Lines.countingAllCounted(n, item))
                add(Lines.countingCorrect("", n, item))
                add(Lines.countingDemo(n, item))
            }
        }
        for (n in 1..MAX_COUNT) add(Lines.count(n))
        add(Lines.COUNTING_RETRY)

        // 숫자 징검다리
        add(Lines.PATH_START)
        add(Lines.PATH_ROLL)
        add(Lines.PATH_WRONG_STONE)
        add(Lines.PATH_ASK)
        add(Lines.PATH_FINISH)
        val maxDie = (Difficulty.MIN_STAGE..Difficulty.MAX_STAGE).maxOf { Difficulty(it).pathDieMax }
        for (steps in 1..maxDie) {
            add(Lines.pathRolled(steps))
            for (from in 0 until MAX_COUNT) add(Lines.pathArrivalRetry(from, steps))
        }
        for (n in 1..MAX_COUNT) {
            add(Lines.pathStone(n))
            add(Lines.pathArrived("", n))
        }

        // 멈춰! 놀이
        add(Lines.STOP_RULE)
        add(Lines.STOP_RULE_REVERSED)
        add(Lines.STOP_READY)
        add(Lines.STOP_OOPS_RED)
        add(Lines.STOP_OOPS_GREEN)
        add(Lines.STOP_DONE)

        // 기분 친구
        EmotionContent.situations.forEach { s ->
            add(Lines.emotionPrompt(s))
            add(Lines.emotionCorrect("", s.emotion, s))
            s.alsoOk.forEach { add(Lines.emotionAlsoOk(it, s)) }
        }
        Emotion.entries.forEach {
            add(Lines.emotionName(it))
            add(Lines.emotionWrong(it))
        }
        add(Lines.BREATHE_INVITE)
        add(Lines.BREATHE_IN)
        add(Lines.BREATHE_OUT)
        add(Lines.BREATHE_DONE)

        // 규칙 찾기
        add(Lines.PATTERN_RETRY)
        PatternContent.themes.flatten().forEach { item ->
            add(Lines.patternRead("", listOf(item, item), withAnswer = true))
            add(Lines.patternRead("", listOf(item), withAnswer = false))
        }

        // 모양 맞추기
        add(Lines.SHAPE_PROMPT)
        add(Lines.shapeAllDone(""))
        ShapeKind.entries.forEach {
            add(Lines.shapeFit(it))
            add(Lines.shapeWrong(it))
            add(Lines.shapeName(it))
        }

        // 짝꿍 카드
        add(Lines.MEMORY_PROMPT)
        add(Lines.MEMORY_PREVIEW)
        add(Lines.memoryAllDone(""))
        MemoryContent.faces.forEach {
            add(Lines.memoryFace(it))
            add(Lines.memoryMatch(it))
            add(Lines.memoryMismatch(it))
        }

        // 색깔 풍선
        val balloonTargets = (Difficulty.MIN_STAGE..Difficulty.MAX_STAGE).map { Difficulty(it).balloonTargetsPerColor }.toSet()
        Palette.balloonColors.forEach { target ->
            add(Lines.balloonPrompt(target))
            balloonTargets.forEach { add(Lines.balloonRoundDone(target, it)) }
            Palette.balloonColors.filter { it != target }.forEach { add(Lines.balloonWrong(it, target)) }
        }
        for (n in 1..balloonTargets.max()) add(Lines.balloonCount(n))

        // 그림 그리기
        add(Lines.DRAW_INTRO)
        add(Lines.DRAW_RAINBOW)
        add(Lines.DRAW_STAMP)
        add(Lines.DRAW_ERASER)
        add(Lines.DRAW_EMPTY)
        add(Lines.DRAW_DONE)
        addAll(Lines.crayonNames)

        // 실로폰
        add(Lines.XYLO_INTRO)
        add(Lines.XYLO_FREE)
        add(Lines.XYLO_DONE)
        Songs.all.forEach {
            add(Lines.xyloSong(it))
            add(Lines.xyloSongDone(it))
        }

        // 쑥쑥 체조
        add(Lines.MOVE_INTRO)
        MovementContent.moves.forEach {
            add(Lines.move(it))
            add(Lines.moveInstruction(it))
        }
        addAll(Lines.movePraise)
    }

    /** 중복 없는 문장 목록 (나온 순서대로) */
    fun segments(): List<String> =
        utterances().flatMap { VoiceKey.segments(it) }.distinct().filter { Lines.NAME_TOKEN !in it }

    /** voice/lines.txt 내용 */
    fun exportText(): String = buildString {
        appendLine("# 쑥쑥 놀이터 음성 문장 목록 — 앱 코드(content/Lines.kt)에서 자동으로 만듭니다. 직접 고치지 마세요.")
        appendLine("# 다시 만들기: cd kids-edu-app && ./gradlew :app:testDebugUnitTest --tests '*VoiceScriptTest*' -PupdateVoiceLines=true")
        appendLine("# 음성 만들기: node tools/make-voice.mjs (README의 '고품질 음성' 참고)")
        appendLine("# '$NAME_PREFIX'로 시작하는 줄은 아이 이름이 들어가는 틀로, --name 을 주었을 때만 만듭니다.")
        Lines.nameTemplates.forEach { appendLine(NAME_PREFIX + it) }
        segments().forEach { appendLine(it) }
    }

    private const val MAX_COUNT = 10
}
