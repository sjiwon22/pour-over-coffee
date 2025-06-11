package com.example.pour_over_coffee

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun RecipeScreen(recipe: PourOverRecipe, modifier: Modifier = Modifier) {
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
                StepItem(step = step, index = index)
            }
        }
    }
}

@Composable
private fun StepItem(step: PourOverStep, index: Int) {
    var remaining by remember { mutableStateOf(step.waitSeconds) }
    var running by remember { mutableStateOf(false) }

    LaunchedEffect(running) {
        if (running) {
            remaining = step.waitSeconds
            while (remaining > 0) {
                delay(1000)
                remaining--
            }
            running = false
        }
    }

    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clickable { running = true }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Step ${index + 1}")
            Text(text = "Add ${step.waterGrams}g of water")
            val waitText = if (running) {
                "Time left: ${remaining} seconds"
            } else {
                "Wait ${step.waitSeconds} seconds"
            }
            Text(text = waitText)
        }
    }
}
