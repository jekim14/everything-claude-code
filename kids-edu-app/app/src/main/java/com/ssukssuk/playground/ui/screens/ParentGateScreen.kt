package com.ssukssuk.playground.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssukssuk.playground.logic.ParentGate
import com.ssukssuk.playground.ui.components.rememberShakeState
import com.ssukssuk.playground.ui.components.shake
import com.ssukssuk.playground.ui.theme.KidsColors
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * 보호자 확인. 유아가 우연히 설정을 바꾸거나 쉬는 시간을 건너뛰지 않도록
 * 두 자리 덧셈 답을 입력해야 들어갈 수 있습니다.
 */
@Composable
fun ParentGateScreen(onSolved: () -> Unit, onCancel: () -> Unit) {
    val random = remember { Random(System.nanoTime()) }
    var question by remember { mutableStateOf(ParentGate.generate(random)) }
    var input by remember { mutableStateOf("") }
    var failures by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf("보호자 메뉴로 들어가려면 문제의 답을 입력해 주세요.") }
    val shakeState = rememberShakeState()
    val scope = rememberCoroutineScope()

    fun press(key: String) {
        when (key) {
            "⌫" -> input = input.dropLast(1)
            else -> if (input.length < 2) input += key
        }
        if (input.length == 2) {
            if (input.toIntOrNull() == question.answer) {
                onSolved()
            } else {
                failures++
                input = ""
                question = ParentGate.generate(random)
                message = "답이 달라요. 새 문제를 풀어 주세요."
                scope.launch { shakeState.shake() }
                if (failures >= 3) onCancel()
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFECEFF4)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔒 보호자 확인", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = KidsColors.Ink)
                Spacer(Modifier.height(8.dp))
                Text(message, fontSize = 16.sp, color = KidsColors.InkSoft, fontWeight = FontWeight.Normal)
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "${question.a} + ${question.b} = ?",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = KidsColors.Ink,
                    modifier = Modifier.shake(shakeState),
                )
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(64.dp)
                        .background(Color.White, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(input.ifEmpty { "__" }, fontSize = 36.sp, color = KidsColors.Ink)
                }
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = onCancel) {
                    Text("돌아가기", fontSize = 18.sp)
                }
            }
            Spacer(Modifier.width(24.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("", "0", "⌫"),
                ).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { key ->
                            if (key.isEmpty()) {
                                Spacer(Modifier.size(width = 76.dp, height = 58.dp))
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(width = 76.dp, height = 58.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.White)
                                        .clickable { press(key) },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(key, fontSize = 26.sp, color = KidsColors.Ink)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
