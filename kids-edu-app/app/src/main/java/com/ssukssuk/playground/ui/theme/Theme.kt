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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import com.ssukssuk.playground.R

/**
 * 색 체계 ("종이 공작 놀이터").
 * 바탕은 평평한 하늘색·종이색, 강조는 누리과정 5개 영역 색(core.Domain)이 맡습니다.
 * 글자색은 흰 바탕에서 대비 4.5:1 이상이 되도록 짙은 남보라를 씁니다.
 */
object KidsColors {
    val Sky = Color(0xFFCFEBFF)
    val SkyShadow = Color(0xFFA9D3EE)
    val Evening = Color(0xFFFFE1C4)
    val EveningShadow = Color(0xFFE8C39E)
    val SettingSun = Color(0xFFFFB36B)
    val NightTop = Color(0xFF1E2A5A)
    val Hill = Color(0xFF86C968)
    val HillBack = Color(0xFFA6DC86)
    val Sun = Color(0xFFFFC93C)
    val SunDeep = Color(0xFFE89B00)
    val Moon = Color(0xFF6B63A8)

    val Ink = Color(0xFF2B2748)
    val InkSoft = Color(0xFF5E5A7A)
    val Paper = Color(0xFFFFF8EC)
    val PaperShadow = Color(0xFFEADFCB)
    val Cream = Paper
    val White = Color(0xFFFFFFFF)
    val Shadow = Color(0x33000000)
    val ParentBackground = Color(0xFFF5F3FA)
    val ParentLine = Color(0xFFECE8F7)
    val TalkCard = Color(0xFFFFF6D8)
    val TalkCardShadow = Color(0xFFD9C88E)
    val TalkCardInk = Color(0xFF6F6340)

    val Correct = Color(0xFF2FA84F)
    val CorrectDeep = Color(0xFF1F7A37)
    val CorrectSoft = Color(0xFFE6F6EA)
    val Hint = Color(0xFFE3A11B)
    val Warm = Color(0xFFFFB74D)
    val Accent = Color(0xFFE0603E)

    val Go = Color(0xFF1E9E5A)
    val Stop = Color(0xFFD63B30)

    val Sprout = Color(0xFF9BE07A)
    val SproutLight = Color(0xFFBDEEA2)
    val Leaf = Color(0xFF4CB24C)
    val LeafLight = Color(0xFF6DD06D)
    val Cheek = Color(0xFFFF9EB5)
}

/** 아이 화면 글꼴: 주아체(둥글고 굵은 손글씨 느낌, SIL OFL 1.1) */
val JuaFamily = FontFamily(Font(R.font.jua_regular, FontWeight.Normal))

/** 보호자 화면의 작은 설명 글: 기기 기본 글꼴이 작은 크기에서 더 또렷합니다. */
val ParentFamily = FontFamily.Default

private val KidsColorScheme = lightColorScheme(
    primary = KidsColors.Correct,
    onPrimary = Color.White,
    secondary = KidsColors.Warm,
    background = KidsColors.Paper,
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
                // 주아체는 굵기가 하나뿐이라 가짜 굵은 글씨를 만들지 않습니다.
                LocalTextStyle provides LocalTextStyle.current.merge(
                    TextStyle(fontFamily = JuaFamily, fontSynthesis = FontSynthesis.None),
                ),
                content = content,
            )
        }
    }
}

/** 보호자 화면 글자 스타일: 기본 글꼴, 필요한 곳만 굵게 */
val ParentTextStyle = TextStyle(fontFamily = ParentFamily, fontSynthesis = FontSynthesis.All)
