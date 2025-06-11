package com.example.pour_over_coffee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pour_over_coffee.data.Recipe
import com.example.pour_over_coffee.data.RecipeRepository
import kotlinx.coroutines.delay

@Composable
fun MakeCoffeeScreen(onDone: () -> Unit) {
    val recipes = RecipeRepository.getRecipes()
    val selected = remember { mutableStateOf<Recipe?>(null) }
    val currentStep = remember { mutableStateOf(0) }
    val timeLeft = remember { mutableStateOf(0) }

    LaunchedEffect(currentStep.value) {
        val recipe = selected.value
        if (recipe != null && currentStep.value in recipe.steps.indices) {
            timeLeft.value = recipe.steps[currentStep.value].timeSec
            while (timeLeft.value > 0) {
                delay(1000)
                timeLeft.value--
            }
            currentStep.value++
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        recipes.forEach { recipe ->
            Button(modifier = Modifier.fillMaxWidth(), onClick = { selected.value = recipe; currentStep.value = 0 }) {
                Text(recipe.name)
            }
        }
        TextButton(onClick = onDone, modifier = Modifier.fillMaxWidth()) { Text("Back") }

        selected.value?.let { recipe ->
            Text("Brewing: ${recipe.name}")
            if (currentStep.value < recipe.steps.size) {
                val step = recipe.steps[currentStep.value]
                Text("Step ${currentStep.value + 1}: ${step.waterAmount}ml - ${timeLeft.value}s left")
            } else {
                Text("Done!")
            }
        }
    }
}
