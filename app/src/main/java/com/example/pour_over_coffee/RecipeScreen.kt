package com.example.pour_over_coffee

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
                Card(modifier = Modifier.padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Step ${index + 1}")
                        Text(text = "Add ${step.waterGrams}g of water")
                        Text(text = "Wait ${step.waitSeconds} seconds")
                    }
                }
            }
        }
    }
}
