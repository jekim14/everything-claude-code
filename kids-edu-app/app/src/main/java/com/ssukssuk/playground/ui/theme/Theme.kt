package com.ssukssuk.playground.ui.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density

object KidsColors {
    val SkyTop = Color(0xFF7CC8FF)
    val SkyBottom = Color(0xFFD7F0FF)
    val NightTop = Color(0xFF1E2A5A)
    val NightBottom = Color(0xFF4A4E8C)
    val Hill = Color(0xFF8BD66B)
    val HillDark = Color(0xFF6CC553)
    val Sun = Color(0xFFFFD54F)
    val Cloud = Color(0xFFFFFFFF)

    val Ink = Color(0xFF3B3355)
    val InkSoft = Color(0xFF6B6385)
    val Cream = Color(0xFFFFFBF0)
    val Paper = Color(0xFFFFFFFF)
    val Shadow = Color(0x33000000)

    val Correct = Color(0xFF43A047)
    val Warm = Color(0xFFFFB74D)
    val Accent = Color(0xFFFF6F61)

    val Sprout = Color(0xFF9BE07A)
    val SproutLight = Color(0xFFBDEEA2)
    val Leaf = Color(0xFF4CB24C)
    val LeafLight = Color(0xFF6DD06D)
    val Cheek = Color(0xFFFF9EB5)
}

private val KidsColorScheme = lightColorScheme(
    primary = Color(0xFF4CB24C),
    onPrimary = Color.White,
    secondary = Color(0xFFFFB74D),
    background = KidsColors.Cream,
    onBackground = KidsColors.Ink,
    surface = Color.White,
    onSurface = KidsColors.Ink,
)

@Composable
fun SsukSsukTheme(content: @Composable () -> Unit) {
    val density = LocalDensity.current
    // 큰 글꼴 설정에서도 놀이 화면 배치가 깨지지 않도록 글꼴 배율을 제한합니다.
    val limitedDensity = Density(density.density, density.fontScale.coerceAtMost(1.15f))
    CompositionLocalProvider(LocalDensity provides limitedDensity) {
        MaterialTheme(colorScheme = KidsColorScheme) {
            CompositionLocalProvider(
                LocalContentColor provides KidsColors.Ink,
                LocalTextStyle provides LocalTextStyle.current.merge(TextStyle(fontWeight = FontWeight.Bold)),
                content = content,
            )
        }
    }
}
