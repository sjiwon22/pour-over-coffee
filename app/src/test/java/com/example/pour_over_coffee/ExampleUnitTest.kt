package com.example.pour_over_coffee

import com.example.pour_over_coffee.data.Recipe
import com.example.pour_over_coffee.data.RecipeRepository
import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun repository_addsRecipe() {
        val sizeBefore = RecipeRepository.getRecipes().size
        RecipeRepository.addRecipe(Recipe(name = "Test", waterTemp = 90, beanAmount = 15))
        val sizeAfter = RecipeRepository.getRecipes().size
        assertEquals(sizeBefore + 1, sizeAfter)
    }
}