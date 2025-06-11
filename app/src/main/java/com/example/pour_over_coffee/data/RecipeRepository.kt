package com.example.pour_over_coffee.data

import androidx.compose.runtime.mutableStateListOf

/**
 * Simple in-memory repository for recipes.
 */
object RecipeRepository {
    private val recipes = mutableStateListOf<Recipe>()

    fun getRecipes(): List<Recipe> = recipes

    fun addRecipe(recipe: Recipe) {
        recipes += recipe
    }

    fun updateRecipe(recipe: Recipe) {
        val index = recipes.indexOfFirst { it.id == recipe.id }
        if (index >= 0) {
            recipes[index] = recipe
        }
    }

    fun removeRecipe(recipe: Recipe) {
        recipes.removeIf { it.id == recipe.id }
    }
}
