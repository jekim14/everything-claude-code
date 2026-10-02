// 최상위 빌드 파일: 각 모듈에서 사용할 플러그인 버전만 선언합니다.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
