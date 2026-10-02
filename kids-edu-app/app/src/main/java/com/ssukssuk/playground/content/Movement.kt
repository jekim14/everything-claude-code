package com.ssukssuk.playground.content

/** 쑥쑥 체조 동작의 이름과 안내. 쑥쑥이의 움직임은 ui.games.MovementGame에 같은 순서로 있습니다. */
data class MoveText(
    val title: String,
    val icon: String,
    val instruction: String,
    val seconds: Float,
)

object MovementContent {
    val moves: List<MoveText> = listOf(
        MoveText("만세!", "🙌", "두 팔을 하늘 높이 쭉 뻗어요. 만세!", 7f),
        MoveText("박수 짝짝", "👏", "박수를 짝짝짝 쳐요!", 7f),
        MoveText("콩콩 뛰기", "🐰", "토끼처럼 제자리에서 콩콩 뛰어요!", 7f),
        MoveText("한 발 서기", "⚖️", "한 발로 서서 균형을 잡아요. 흔들흔들, 넘어지지 않게!", 8f),
        MoveText("옆으로 쭉", "🌈", "팔을 올리고 몸을 옆으로 쭉~ 기울여요. 이쪽, 저쪽!", 8f),
        MoveText("빙글빙글", "🌀", "제자리에서 빙글 한 바퀴 돌아요!", 7f),
        MoveText("숨쉬기", "🌬️", "코로 숨을 크게 들이마시고, 입으로 후~ 내쉬어요.", 10f),
    )

    /** 1단계(만 4세 출발점)에서 하는 동작 번호 */
    val youngerMoveIndices: List<Int> = listOf(0, 1, 2, 5, 6)
}
