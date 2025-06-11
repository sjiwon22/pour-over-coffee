package com.example.pour_over_coffee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pour_over_coffee.data.Recipe
import com.example.pour_over_coffee.data.RecipeRepository
import kotlinx.coroutines.delay

@Composable
fun MakeCoffeeScreen(onDone: () -> Unit) {
    val recipes = RecipeRepository.getRecipes()
    val selected = remember { mutableStateOf<Recipe?>(null) }
    val activeStep = remember { mutableStateOf<Int?>(null) }
    val timeLeft = remember { mutableStateOf(0) }
    val completed = remember { mutableStateListOf<Boolean>() }

    LaunchedEffect(selected.value) {
        selected.value?.let { recipe ->
            completed.clear()
            repeat(recipe.steps.size) { completed.add(false) }
            activeStep.value = null
            timeLeft.value = 0
        }
    }

    LaunchedEffect(activeStep.value) {
        val recipe = selected.value
        val idx = activeStep.value
        if (recipe != null && idx != null) {
            timeLeft.value = recipe.steps[idx].timeSec
            while (timeLeft.value > 0) {
                delay(1000)
                timeLeft.value--
            }
            completed[idx] = true
            activeStep.value = null
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        recipes.forEach { recipe ->
            Button(modifier = Modifier.fillMaxWidth(), onClick = { selected.value = recipe }) {
                Text(recipe.name)
            }
        }
        TextButton(onClick = onDone, modifier = Modifier.fillMaxWidth()) { Text("Back") }

        selected.value?.let { recipe ->
            Text("Brewing: ${recipe.name}")
            Text("Use ${recipe.beanAmount}g beans, water at ${recipe.waterTemp}°C")
            recipe.steps.forEachIndexed { index, step ->
                val done = completed.getOrNull(index) == true
                val running = activeStep.value == index
                val enabled = !done && activeStep.value == null && completed.take(index).all { it }
                val label = when {
                    done -> "Done"
                    running -> "${timeLeft.value}s"
                    else -> "Step ${index + 1}: ${step.waterAmount}ml / ${step.timeSec}s"
                }
                val colors = if (done) {
                    ButtonDefaults.buttonColors(containerColor = Color(0xFF9CCC65))
                } else {
                    ButtonDefaults.buttonColors()
                }
                Button(
                    onClick = { activeStep.value = index },
                    enabled = enabled,
                    colors = colors,
                    modifier = Modifier.fillMaxWidth()
                ) { Text(label) }
            }
        }
    }
}
