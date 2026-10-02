package com.ssukssuk.playground.content

import com.ssukssuk.playground.core.Game

/**
 * "함께 이야기해요" 카드: 놀이를 마친 뒤 보호자에게 보여 주는 대화·생활 속 놀이 제안.
 *
 * 앱 안에 넣은 대화 제안이 보호자의 말걸기와 상호작용을 크게 늘렸고(Mathers 외 2025),
 * 어른과 함께 쓸 때 학습 효과가 더 컸습니다(Taylor 외 2024). 아이가 아니라 보호자가 읽는 글입니다.
 */
object TalkCards {
    private val cards: Map<Game, List<String>> = mapOf(
        Game.HANGUL to listOf(
            "아이 이름의 첫 글자를 함께 찾아보세요. \"네 이름은 무슨 글자로 시작할까?\"",
            "간판이나 과자 봉지에서 오늘 본 자음을 찾아보세요. \"여기 ㄱ이 숨어 있네!\"",
            "\"고양이, 고구마, 고래… 또 '고'로 시작하는 말은?\" 하고 말놀이를 이어 가 보세요.",
        ),
        Game.SYLLABLE to listOf(
            "\"나, 노, 누, 니\"처럼 자음 하나에 모음을 바꿔 가며 함께 소리 내 보세요.",
            "종이에 ㄱ과 ㅏ를 따로 써서 붙였다 떼었다 해 보세요. \"붙이면 무슨 글자가 될까?\"",
        ),
        Game.COUNTING to listOf(
            "간식을 함께 세고 \"모두 몇 개?\" 하고 한 번 더 물어보세요. 마지막에 센 수가 전체 개수라는 걸 알아 가요.",
            "계단을 오르며 함께 세어 보세요. \"하나, 둘, 셋… 몇 칸 올라왔지?\"",
        ),
        Game.NUMBER_PATH to listOf(
            "뱀사다리처럼 한 줄로 된 판 놀이를 함께 해 보세요. 칸을 옮길 때 칸의 수를 소리 내어 세면 좋아요.",
            "\"5에서 두 칸 더 가면?\"처럼 이어 세기를 놀이로 물어보세요.",
        ),
        Game.SHAPES to listOf(
            "집 안에서 동그라미·세모·네모 모양을 찾아보세요. \"어디가 동그랗게 생겼어?\"",
            "\"세모는 뾰족한 곳이 몇 개일까?\" 하고 손가락으로 함께 세어 보세요.",
        ),
        Game.PATTERN to listOf(
            "박수-발 구르기로 몸 규칙을 만들어 보세요. \"짝, 쿵, 짝, 쿵, 다음은?\"",
            "숟가락과 포크를 번갈아 놓으며 규칙을 만들고, 아이가 이어 놓게 해 보세요.",
        ),
        Game.MEMORY to listOf(
            "물건 세 개를 보여 주고 하나를 몰래 숨겨 보세요. \"뭐가 없어졌을까?\"",
            "오늘 있었던 일을 순서대로 이야기해 보세요. \"맨 처음에 뭘 했지?\"",
        ),
        Game.EMOTION to listOf(
            "\"오늘 기뻤던 일은 뭐야? 속상했던 일은?\" 하고 하루의 마음을 나눠 보세요.",
            "그림책 인물의 표정을 보며 \"지금 어떤 마음일까? 왜 그럴까?\" 하고 물어보세요.",
            "화가 날 때 함께 '거북이 숨쉬기'를 해 보세요. 천천히 들이마시고, 후~ 내쉬어요.",
        ),
        Game.STOP_GO to listOf(
            "'무궁화 꽃이 피었습니다' 놀이를 함께 해 보세요. 멈추기 연습에 좋아요.",
            "노래가 멈추면 '얼음!' 하는 놀이를 해 보세요. 다음엔 규칙을 바꿔 박수 소리에 멈춰요.",
        ),
        Game.MOVEMENT to listOf(
            "오늘 해 본 동작을 거울 앞에서 함께 따라 해 보세요.",
            "밖에 나가 한 발로 서기, 콩콩 뛰기를 함께 해 보세요.",
        ),
        Game.BALLOON to listOf(
            "\"우리 집에서 빨간색을 찾아볼까?\" 하고 색깔 찾기 놀이를 해 보세요.",
            "옷을 입을 때 색깔 이름을 함께 말해 보세요.",
        ),
        Game.DRAWING to listOf(
            "그림을 보고 \"여기는 뭐야? 이야기해 줄래?\" 하고 물어봐 주세요. '잘 그렸다'보다 그림 이야기를 들어 주세요.",
            "크레파스와 종이로 같은 그림을 한 번 더 그려 보세요.",
        ),
        Game.XYLOPHONE to listOf(
            "냄비와 숟가락으로 함께 리듬을 만들어 보세요. \"빠르게, 느리게!\"",
            "오늘 친 노래를 함께 불러 보세요.",
        ),
    )

    fun forGame(game: Game): List<String> = cards[game].orEmpty()
}

/**
 * 쉬는 시간·오늘 놀이가 끝났을 때 권하는 화면 밖 놀이.
 * [title]은 아이에게 읽어 주고, [detail]은 보호자가 보는 설명입니다. [game]은 같은 영역 아이콘을 빌려 씁니다.
 */
data class OfflineIdea(val title: String, val detail: String, val game: Game)

object OfflineIdeas {
    val all: List<OfflineIdea> = listOf(
        OfflineIdea("동그라미 찾기", "집 안에서 동그란 물건을 세 개 찾아요", Game.SHAPES),
        OfflineIdea("베개 징검다리", "베개를 늘어놓고 1부터 10까지 세며 건너요", Game.NUMBER_PATH),
        OfflineIdea("그림책 읽기", "좋아하는 책 한 권을 같이 읽어요", Game.HANGUL),
        OfflineIdea("얼음 땡 놀이", "노래가 멈추면 얼음! 하고 멈춰요", Game.STOP_GO),
        OfflineIdea("색깔 찾기", "밖에서 초록색을 세 가지 찾아요", Game.BALLOON),
        OfflineIdea("몸으로 글자 만들기", "몸으로 ㄱ, ㄴ 모양을 만들어요", Game.SYLLABLE),
        OfflineIdea("블록 쌓기", "블록을 높이높이 쌓아 봐요", Game.PATTERN),
        OfflineIdea("콩콩 뛰기", "제자리에서 열 번 콩콩 뛰어요", Game.MOVEMENT),
        OfflineIdea("노래 부르기", "좋아하는 노래를 크게 불러요", Game.XYLOPHONE),
    )
}
