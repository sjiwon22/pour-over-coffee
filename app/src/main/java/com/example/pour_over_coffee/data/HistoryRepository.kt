package com.example.pour_over_coffee.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import com.example.pour_over_coffee.data.Recipe
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

    fun addEntry(recipe: Recipe) {
        if (!loaded) load()
        val totalWater = recipe.steps.sumOf { it.waterAmount }
        history.add(
            0,
            HistoryEntry(
                name = recipe.name,
                waterAmount = totalWater,
                timestamp = System.currentTimeMillis()
            )
        )
        save()
    }

    fun getStats(): List<RankingStat> {
        if (!loaded) load()
        val grouped = history.groupBy { it.name }
        return grouped.map { (name, entries) ->
            val water = entries.first().waterAmount
            val withScores = entries.filter { it.score != null }
            val avg = if (withScores.isNotEmpty()) {
                withScores.map { it.score!!.toDouble() }.average()
            } else 0.0
            RankingStat(name, water, avg, entries.size)
        }
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

    fun clearScoresForRecipe(name: String) {
        if (!loaded) load()
        var changed = false
        val updated = history.map { entry ->
            if (entry.name == name && entry.score != null) {
                changed = true
                entry.copy(score = null)
            } else entry
        }
        if (changed) {
            history.clear()
            history.addAll(updated)
            save()
        }
    }

    private fun load() {
        loaded = true
        val json = prefs?.getString(KEY_HISTORY, null)
        history.clear()
        if (json.isNullOrEmpty()) return
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                history += HistoryEntry(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    waterAmount = obj.getInt("waterAmount"),
                    timestamp = obj.getLong("timestamp"),
                    score = if (obj.has("score") && !obj.isNull("score")) obj.getInt("score") else null
                )
            }
        } catch (_: Exception) {
            // Corrupt data, ignore
        }
    }

    private fun save() {
        val pref = prefs ?: return
        val array = JSONArray()
        history.forEach { entry ->
            val obj = JSONObject()
            obj.put("id", entry.id)
            obj.put("name", entry.name)
            obj.put("waterAmount", entry.waterAmount)
            obj.put("timestamp", entry.timestamp)
            if (entry.score != null) obj.put("score", entry.score)
            array.put(obj)
        }
        pref.edit().putString(KEY_HISTORY, array.toString()).apply()
    }
}

data class HistoryEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val waterAmount: Int,
    val timestamp: Long,
    var score: Int? = null
)

data class RankingStat(
    val name: String,
    val waterAmount: Int,
    val averageScore: Double,
    val count: Int
)
