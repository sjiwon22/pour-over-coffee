package com.example.pour_over_coffee.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import org.json.JSONArray
import org.json.JSONObject

/**
 * Stores recently brewed recipes and optionally ratings.
 */
object HistoryRepository {
    private const val PREFS_NAME = "history"
    private const val KEY_HISTORY = "history"

    private var prefs: SharedPreferences? = null
    private val history = mutableStateListOf<HistoryEntry>()
    private var loaded = false

    fun initialize(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            load()
        }
    }

    fun addEntry(name: String) {
        if (!loaded) load()
        // prepend newest at start
        history.add(0, HistoryEntry(name = name))
        save()
    }

    fun getHistory(): List<HistoryEntry> {
        if (!loaded) load()
        return history
    }

    fun update(entry: HistoryEntry) {
        val index = history.indexOfFirst { it.id == entry.id }
        if (index >= 0) {
            history[index] = entry
            save()
        }
    }

    private fun load() {
        loaded = true
        val json = prefs?.getString(KEY_HISTORY, null)
        history.clear()
        if (json.isNullOrEmpty()) return
        val array = JSONArray(json)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            history += HistoryEntry(
                id = obj.getString("id"),
                name = obj.getString("name"),
                score = if (obj.has("score") && !obj.isNull("score")) obj.getInt("score") else null
            )
        }
    }

    private fun save() {
        val pref = prefs ?: return
        val array = JSONArray()
        history.forEach { entry ->
            val obj = JSONObject()
            obj.put("id", entry.id)
            obj.put("name", entry.name)
            if (entry.score != null) obj.put("score", entry.score)
            array.put(obj)
        }
        pref.edit().putString(KEY_HISTORY, array.toString()).apply()
    }
}

data class HistoryEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    var score: Int? = null
)
