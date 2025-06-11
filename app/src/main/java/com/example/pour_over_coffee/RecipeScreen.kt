package com.example.pour_over_coffee

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun RecipeScreen(recipe: PourOverRecipe, modifier: Modifier = Modifier) {
    var currentStep by remember { mutableStateOf(0) }
    val doneSteps = remember {
        mutableStateListOf<Boolean>().apply {
            repeat(recipe.steps.size) { add(false) }
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Beans: ${recipe.beansGrams}g",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Total Water: ${recipe.totalWater}g",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn {
            itemsIndexed(recipe.steps) { index, step ->
                StepItem(
                    step = step,
                    index = index,
                    enabled = index == currentStep,
                    done = doneSteps[index]
                ) {
                    doneSteps[index] = true
                    if (currentStep == index && currentStep < recipe.steps.size - 1) {
                        currentStep++
                    }
                }
            }
        }
    }
}

@Composable
private fun StepItem(
    step: PourOverStep,
    index: Int,
    enabled: Boolean,
    done: Boolean,
    onDone: () -> Unit
) {
    var remaining by remember { mutableStateOf(step.waitSeconds) }
    var running by remember { mutableStateOf(false) }

    LaunchedEffect(running) {
        if (running && !done) {
            remaining = step.waitSeconds
            while (remaining > 0) {
                delay(1000)
                remaining--
            }
            running = false
            onDone()
        }
    }

    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clickable(enabled = enabled && !running && !done) { running = true }
            ,
        colors = CardDefaults.cardColors(
            containerColor = if (done) Color(0xFFA5D6A7) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Step ${index + 1}")
            Text(text = "Add ${step.waterGrams}g of water")
            val waitText = when {
                done -> "done"
                running -> "Time left: ${remaining} seconds"
                else -> "Wait ${step.waitSeconds} seconds"
            }
            Text(text = waitText)
        }
    }
}
