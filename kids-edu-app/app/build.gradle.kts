import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.ssukssuk.playground"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ssukssuk.playground"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "2.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    // 미리 녹음한 음성 파일은 압축하지 않아야 MediaPlayer 가 바로 열 수 있습니다.
    androidResources {
        noCompress += "mp3"
    }

    lint {
        abortOnError = true
        warningsAsErrors = false
    }
}

// 음성 문장 목록(voice/lines.txt)이 코드와 같은지 단위 테스트에서 확인합니다.
// 문장을 바꿨다면: ./gradlew :app:testDebugUnitTest --tests '*VoiceScriptTest*' -PupdateVoiceLines=true
tasks.withType<Test>().configureEach {
    systemProperty("voiceLinesFile", rootProject.file("voice/lines.txt").absolutePath)
    systemProperty("updateVoiceLines", providers.gradleProperty("updateVoiceLines").getOrElse("false"))
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.ext.junit)
}
