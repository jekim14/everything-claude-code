package com.ssukssuk.playground.core

import com.ssukssuk.playground.content.Sticker
import com.ssukssuk.playground.content.Stickers
import java.time.LocalDate
import kotlin.random.Random

/** 기기에 저장되는 간단한 키-값 저장소. 네트워크로 전송되는 데이터는 없습니다. */
interface KeyValueStore {
    fun getInt(key: String, default: Int): Int
    fun putInt(key: String, value: Int)
    fun getBoolean(key: String, default: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
    fun getString(key: String, default: String): String
    fun putString(key: String, value: String)
    fun remove(keys: Collection<String>)
    fun keys(): Set<String>
}

/** 테스트와 미리보기에서 사용하는 메모리 저장소 */
class InMemoryStore : KeyValueStore {
    private val values = HashMap<String, Any>()

    override fun getInt(key: String, default: Int): Int = values[key] as? Int ?: default
    override fun putInt(key: String, value: Int) { values[key] = value }
    override fun getBoolean(key: String, default: Boolean): Boolean = values[key] as? Boolean ?: default
    override fun putBoolean(key: String, value: Boolean) { values[key] = value }
    override fun getString(key: String, default: String): String = values[key] as? String ?: default
    override fun putString(key: String, value: String) { values[key] = value }
    override fun remove(keys: Collection<String>) { keys.forEach { values.remove(it) } }
    override fun keys(): Set<String> = values.keys.toSet()
}

data class Settings(
    /** 만 나이. 0이면 아직 선택하지 않은 상태(첫 실행) */
    val age: Int = 0,
    val voiceOn: Boolean = true,
    val soundOn: Boolean = true,
    /** 쉬는 시간 알림(분). 0이면 끔 */
    val restMinutes: Int = 20,
) {
    val difficulty: Difficulty get() = Difficulty(if (age == 0) 4 else age)

    companion object {
        val REST_OPTIONS = listOf(0, 10, 15, 20, 30)
    }
}

/**
 * 놀이별 기록.
 * [firstTryCorrect]/[questions]는 첫 시도에 맞힌 비율로, 보호자 화면에서만 보여 줍니다.
 * 아이에게는 점수나 등급 대신 참여에 대한 보상(스티커)만 제공합니다.
 */
data class GameRecord(
    val plays: Int = 0,
    val firstTryCorrect: Int = 0,
    val questions: Int = 0,
) {
    val accuracy: Float?
        get() = if (questions == 0) null else firstTryCorrect.toFloat() / questions
}

data class Reward(
    val sticker: Sticker,
    val isNew: Boolean,
    val totalStars: Int,
)

class ProgressRepository(
    private val store: KeyValueStore,
    private val today: () -> LocalDate = { LocalDate.now() },
) {
    fun loadSettings(): Settings = Settings(
        age = store.getInt(KEY_AGE, 0),
        voiceOn = store.getBoolean(KEY_VOICE, true),
        soundOn = store.getBoolean(KEY_SOUND, true),
        restMinutes = store.getInt(KEY_REST, 20),
    )

    fun saveSettings(settings: Settings) {
        store.putInt(KEY_AGE, settings.age)
        store.putBoolean(KEY_VOICE, settings.voiceOn)
        store.putBoolean(KEY_SOUND, settings.soundOn)
        store.putInt(KEY_REST, settings.restMinutes)
    }

    fun record(game: Game): GameRecord = GameRecord(
        plays = store.getInt(key(game, "plays"), 0),
        firstTryCorrect = store.getInt(key(game, "correct"), 0),
        questions = store.getInt(key(game, "questions"), 0),
    )

    fun records(): Map<Game, GameRecord> = Game.entries.associateWith { record(it) }

    fun totalStars(): Int = store.getInt(KEY_STARS, 0)

    /** 스티커 id → 받은 횟수 */
    fun stickerCounts(): Map<String, Int> = decodeCounts(store.getString(KEY_STICKERS, ""))

    /**
     * 놀이를 끝까지 마쳤을 때 호출합니다.
     * 아직 받지 않은 스티커를 우선으로 하나를 주고, 별을 하나 더합니다.
     *
     * @param score 첫 시도에 맞힌 문제 수 (자유 놀이는 0)
     * @param total 문제 수 (자유 놀이는 0)
     */
    fun completeGame(game: Game, score: Int, total: Int, random: Random = Random.Default): Reward {
        val record = record(game)
        store.putInt(key(game, "plays"), record.plays + 1)
        store.putInt(key(game, "correct"), record.firstTryCorrect + score.coerceIn(0, total.coerceAtLeast(0)))
        store.putInt(key(game, "questions"), record.questions + total.coerceAtLeast(0))

        val stars = totalStars() + 1
        store.putInt(KEY_STARS, stars)

        val counts = stickerCounts().toMutableMap()
        val notCollected = Stickers.all.filter { (counts[it.id] ?: 0) == 0 }
        val sticker = if (notCollected.isNotEmpty()) notCollected.random(random) else Stickers.all.random(random)
        val isNew = (counts[sticker.id] ?: 0) == 0
        counts[sticker.id] = (counts[sticker.id] ?: 0) + 1
        store.putString(KEY_STICKERS, encodeCounts(counts))

        return Reward(sticker = sticker, isNew = isNew, totalStars = stars)
    }

    fun addPlaySeconds(seconds: Int) {
        if (seconds <= 0) return
        rollDayIfNeeded()
        store.putInt(KEY_TODAY_SECONDS, store.getInt(KEY_TODAY_SECONDS, 0) + seconds)
        store.putInt(KEY_TOTAL_SECONDS, store.getInt(KEY_TOTAL_SECONDS, 0) + seconds)
    }

    fun playSecondsToday(): Int {
        val day = today().toEpochDay().toInt()
        return if (store.getInt(KEY_DAY, -1) == day) store.getInt(KEY_TODAY_SECONDS, 0) else 0
    }

    fun totalPlaySeconds(): Int = store.getInt(KEY_TOTAL_SECONDS, 0)

    /** 놀이 기록·스티커·별을 모두 지웁니다. 설정(나이, 소리 등)은 유지합니다. */
    fun resetProgress() {
        val keep = setOf(KEY_AGE, KEY_VOICE, KEY_SOUND, KEY_REST)
        store.remove(store.keys().filter { it !in keep })
    }

    private fun rollDayIfNeeded() {
        val day = today().toEpochDay().toInt()
        if (store.getInt(KEY_DAY, -1) != day) {
            store.putInt(KEY_DAY, day)
            store.putInt(KEY_TODAY_SECONDS, 0)
        }
    }

    private fun key(game: Game, field: String) = "rec_${game.id}_$field"

    companion object {
        private const val KEY_AGE = "settings_age"
        private const val KEY_VOICE = "settings_voice"
        private const val KEY_SOUND = "settings_sound"
        private const val KEY_REST = "settings_rest_minutes"
        private const val KEY_STARS = "stars"
        private const val KEY_STICKERS = "stickers"
        private const val KEY_DAY = "play_day"
        private const val KEY_TODAY_SECONDS = "play_seconds_today"
        private const val KEY_TOTAL_SECONDS = "play_seconds_total"

        internal fun encodeCounts(counts: Map<String, Int>): String =
            counts.entries.filter { it.value > 0 }.joinToString(",") { "${it.key}:${it.value}" }

        internal fun decodeCounts(raw: String): Map<String, Int> =
            raw.split(',')
                .mapNotNull { entry ->
                    val parts = entry.split(':')
                    val count = parts.getOrNull(1)?.toIntOrNull()
                    if (parts.size == 2 && parts[0].isNotBlank() && count != null) parts[0] to count else null
                }
                .toMap()
    }
}
