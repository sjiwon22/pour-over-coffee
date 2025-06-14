package com.example.pour_over_coffee.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import org.json.JSONArray
import org.json.JSONObject

/**
 * Repository for storing recipes. Persists data using SharedPreferences so
 * saved recipes remain after the app restarts.
 */
object RecipeRepository {
    private const val PREFS_NAME = "recipes"
    private const val KEY_RECIPES = "recipes"

    private var prefs: SharedPreferences? = null
    private val recipes = mutableStateListOf<Recipe>()
    private var loaded = false

    fun initialize(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            loadRecipes()
        }
    }

    fun getRecipes(): List<Recipe> {
        if (!loaded) {
            loadRecipes()
        }
        return recipes
    }

    fun addRecipe(recipe: Recipe) {
        recipes += recipe
        saveRecipes()
    }

    fun updateRecipe(recipe: Recipe) {
        val index = recipes.indexOfFirst { it.id == recipe.id }
        if (index >= 0) {
            recipes[index] = recipe
            saveRecipes()
        }
    }

    fun removeRecipe(recipe: Recipe) {
        if (recipes.removeIf { it.id == recipe.id }) {
            saveRecipes()
        }
    }

    /** Loads saved recipes or creates a default one if none exist. */
    private fun loadRecipes() {
        loaded = true
        val json = prefs?.getString(KEY_RECIPES, null)
        if (json.isNullOrEmpty()) {
            // insert a simple default recipe with a single 30s step
            recipes.clear()
            recipes += Recipe(
                name = "Default", waterTemp = 90, beanAmount = 15,
                steps = mutableListOf(Step(30, 30))
            )
            saveRecipes()
            return
        }

        recipes.clear()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val stepsArray = obj.getJSONArray("steps")
                val steps = mutableListOf<Step>()
                for (j in 0 until stepsArray.length()) {
                    val sObj = stepsArray.getJSONObject(j)
                    steps += Step(
                        waterAmount = sObj.getInt("waterAmount"),
                        timeSec = sObj.getInt("timeSec")
                    )
                }
                recipes += Recipe(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    waterTemp = obj.getInt("waterTemp"),
                    beanAmount = obj.getInt("beanAmount"),
                    steps = steps
                )
            }
        } catch (_: Exception) {
            // Ignore corrupted data
        }
    }

    private fun saveRecipes() {
        val pref = prefs ?: return
        val array = JSONArray()
        recipes.forEach { recipe ->
            val obj = JSONObject()
            obj.put("id", recipe.id)
            obj.put("name", recipe.name)
            obj.put("waterTemp", recipe.waterTemp)
            obj.put("beanAmount", recipe.beanAmount)
            val stepsArray = JSONArray()
            recipe.steps.forEach { step ->
                val sObj = JSONObject()
                sObj.put("waterAmount", step.waterAmount)
                sObj.put("timeSec", step.timeSec)
                stepsArray.put(sObj)
            }
            obj.put("steps", stepsArray)
            array.put(obj)
        }
        pref.edit().putString(KEY_RECIPES, array.toString()).apply()
    }
}
