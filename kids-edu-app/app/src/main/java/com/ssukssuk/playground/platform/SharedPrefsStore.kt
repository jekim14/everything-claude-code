package com.ssukssuk.playground.platform

import android.content.SharedPreferences
import androidx.core.content.edit
import com.ssukssuk.playground.core.KeyValueStore

/** 놀이 기록을 기기 안(SharedPreferences)에만 저장합니다. */
class SharedPrefsStore(private val prefs: SharedPreferences) : KeyValueStore {
    override fun getInt(key: String, default: Int): Int = prefs.getInt(key, default)
    override fun putInt(key: String, value: Int) = prefs.edit { putInt(key, value) }
    override fun getBoolean(key: String, default: Boolean): Boolean = prefs.getBoolean(key, default)
    override fun putBoolean(key: String, value: Boolean) = prefs.edit { putBoolean(key, value) }
    override fun getString(key: String, default: String): String = prefs.getString(key, default) ?: default
    override fun putString(key: String, value: String) = prefs.edit { putString(key, value) }
    override fun remove(keys: Collection<String>) = prefs.edit { keys.forEach { remove(it) } }
    override fun keys(): Set<String> = prefs.all.keys.toSet()
}
