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
    /** 이어서 놀면 쉬어 가는 간격(분). 0이면 끔 */
    val restMinutes: Int = 20,
    /** 하루에 놀 수 있는 시간(분). 0이면 제한 없음. WHO·AAP 권고(하루 1시간 이내)에 맞춰 기본 60분 */
    val dailyLimitMinutes: Int = 60,
    /** 아이 이름(부르는 이름). 기기 안에만 저장합니다. 비어 있으면 이름 없이 인사합니다. */
    val childName: String = "",
) {
    companion object {
        val REST_OPTIONS = listOf(0, 10, 15, 20, 30)
        val DAILY_OPTIONS = listOf(0, 30, 45, 60, 90)
        const val NAME_MAX_LENGTH = 6

        /** 이름 입력값 정리: 앞뒤 공백을 지우고 길이를 제한합니다. */
        fun cleanName(raw: String): String = raw.trim().replace(Regex("\\s+"), " ").take(NAME_MAX_LENGTH)
    }
}

/**
 * 놀이별 기록.
 * [firstTryCorrect]/[questions]는 첫 시도에 맞힌 비율로, 보호자 화면에서만 보여 줍니다.
 * 아이에게는 점수나 등급을 보여 주지 않습니다.
 */
data class GameRecord(
    val plays: Int = 0,
    val firstTryCorrect: Int = 0,
    val questions: Int = 0,
) {
    val accuracy: Float?
        get() = if (questions == 0) null else firstTryCorrect.toFloat() / questions

    /** 보호자 성장 기록에서 이 놀이를 어디에 보여 줄지 */
    val status: SkillStatus
        get() = when {
            plays == 0 -> SkillStatus.NOT_STARTED
            questions == 0 -> SkillStatus.ENJOYED
            questions >= CAN_DO_MIN_QUESTIONS && (accuracy ?: 0f) >= CAN_DO_ACCURACY -> SkillStatus.CAN_DO
            else -> SkillStatus.PRACTICING
        }

    companion object {
        const val CAN_DO_MIN_QUESTIONS = 10
        const val CAN_DO_ACCURACY = 0.75f
    }
}

enum class SkillStatus { NOT_STARTED, PRACTICING, CAN_DO, ENJOYED }

/**
 * 놀이를 마친 결과.
 *
 * @property giftChoices 깜짝 선물로 고를 수 있는 스티커(3개). 비어 있으면 선물이 없는 판입니다.
 * @property stage 다음 판에 쓸 난이도 단계
 */
data class Completion(
    val giftChoices: List<Sticker>,
    val stage: Int,
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
        dailyLimitMinutes = store.getInt(KEY_DAILY, 60),
        childName = store.getString(KEY_NAME, ""),
    )

    fun saveSettings(settings: Settings) {
        store.putInt(KEY_AGE, settings.age)
        store.putBoolean(KEY_VOICE, settings.voiceOn)
        store.putBoolean(KEY_SOUND, settings.soundOn)
        store.putInt(KEY_REST, settings.restMinutes)
        store.putInt(KEY_DAILY, settings.dailyLimitMinutes)
        store.putString(KEY_NAME, Settings.cleanName(settings.childName))
    }

    fun record(game: Game): GameRecord = GameRecord(
        plays = store.getInt(key(game, "plays"), 0),
        firstTryCorrect = store.getInt(key(game, "correct"), 0),
        questions = store.getInt(key(game, "questions"), 0),
    )

    fun records(): Map<Game, GameRecord> = Game.entries.associateWith { record(it) }

    /** 이 놀이의 현재 난이도 단계. 아직 놀아 본 적이 없으면 나이로 정합니다. */
    fun stage(game: Game, age: Int): Int {
        val saved = store.getInt(key(game, "stage"), 0)
        return if (saved in Difficulty.MIN_STAGE..Difficulty.MAX_STAGE) saved else Difficulty.startingStage(age)
    }

    fun difficulty(game: Game, age: Int): Difficulty = Difficulty(stage(game, age))

    /** 나이를 바꾸면 놀이별 단계를 새 나이의 출발점으로 되돌립니다. */
    fun resetStages() {
        store.remove(Game.entries.map { key(it, "stage") })
    }

    /** 스티커 id → 받은 횟수 */
    fun stickerCounts(): Map<String, Int> = decodeCounts(store.getString(KEY_STICKERS, ""))

    /**
     * 놀이를 끝까지 마쳤을 때 호출합니다.
     *
     * - 기록을 더하고, 첫 시도 정답률로 다음 판의 단계를 정합니다.
     * - 깜짝 선물은 문제가 있는 놀이를 그날 처음 마쳤을 때만 줍니다. 매번 주면 선물을 위해 놀게 되고
     *   (Deci·Koestner·Ryan 1999), 무작위로 주면 도박처럼 끌어당기는 설계가 되기 때문입니다.
     *   자유 놀이(그림·실로폰·체조)는 그 자체가 즐거움이므로 선물이 없습니다.
     *
     * @param score 첫 시도에 맞힌 문제 수 (자유 놀이는 0)
     * @param total 문제 수 (자유 놀이는 0)
     */
    fun completeGame(game: Game, score: Int, total: Int, age: Int, random: Random = Random.Default): Completion {
        val safeTotal = total.coerceAtLeast(0)
        val safeScore = score.coerceIn(0, safeTotal)
        val record = record(game)
        store.putInt(key(game, "plays"), record.plays + 1)
        store.putInt(key(game, "correct"), record.firstTryCorrect + safeScore)
        store.putInt(key(game, "questions"), record.questions + safeTotal)

        val next = Difficulty.nextStage(stage(game, age), safeScore, safeTotal)
        store.putInt(key(game, "stage"), next)

        val day = today().toEpochDay().toInt()
        val giftToday = safeTotal > 0 && store.getInt(key(game, "gift_day"), -1) != day
        if (giftToday) store.putInt(key(game, "gift_day"), day)
        return Completion(giftChoices = if (giftToday) giftChoices(random) else emptyList(), stage = next)
    }

    /** 아직 없는 스티커를 먼저 섞어 [count]개 고릅니다. */
    fun giftChoices(random: Random, count: Int = GIFT_CHOICES): List<Sticker> {
        val counts = stickerCounts()
        val (fresh, owned) = Stickers.all.partition { (counts[it.id] ?: 0) == 0 }
        return (fresh.shuffled(random) + owned.shuffled(random)).take(count)
    }

    /** 아이가 고른 스티커를 스티커 책에 붙입니다. 처음 받은 스티커면 true */
    fun claimSticker(sticker: Sticker): Boolean {
        val counts = stickerCounts().toMutableMap()
        val isNew = (counts[sticker.id] ?: 0) == 0
        counts[sticker.id] = (counts[sticker.id] ?: 0) + 1
        store.putString(KEY_STICKERS, encodeCounts(counts))
        return isNew
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

    /** 보호자가 오늘 한도를 넘겨 더 놀도록 허락한 추가 시간(분) */
    fun extraMinutesToday(): Int {
        val day = today().toEpochDay().toInt()
        return if (store.getInt(KEY_EXTRA_DAY, -1) == day) store.getInt(KEY_EXTRA_MINUTES, 0) else 0
    }

    fun addExtraMinutesToday(minutes: Int) {
        val day = today().toEpochDay().toInt()
        store.putInt(KEY_EXTRA_MINUTES, extraMinutesToday() + minutes)
        store.putInt(KEY_EXTRA_DAY, day)
    }

    /** 놀이 기록·스티커·단계를 모두 지웁니다. 설정(나이, 이름, 소리 등)은 유지합니다. */
    fun resetProgress() {
        val keep = setOf(KEY_AGE, KEY_VOICE, KEY_SOUND, KEY_REST, KEY_DAILY, KEY_NAME)
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
        const val GIFT_CHOICES = 3

        private const val KEY_AGE = "settings_age"
        private const val KEY_VOICE = "settings_voice"
        private const val KEY_SOUND = "settings_sound"
        private const val KEY_REST = "settings_rest_minutes"
        private const val KEY_DAILY = "settings_daily_minutes"
        private const val KEY_NAME = "settings_child_name"
        private const val KEY_STICKERS = "stickers"
        private const val KEY_DAY = "play_day"
        private const val KEY_TODAY_SECONDS = "play_seconds_today"
        private const val KEY_TOTAL_SECONDS = "play_seconds_total"
        private const val KEY_EXTRA_DAY = "extra_day"
        private const val KEY_EXTRA_MINUTES = "extra_minutes"

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
