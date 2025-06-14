package com.example.pour_over_coffee.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.example.pour_over_coffee.data.Recipe
import com.example.pour_over_coffee.data.RecipeRepository

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeListScreen(
    onAdd: () -> Unit,
    onEdit: (Recipe) -> Unit,
    onBack: () -> Unit
) {
    val recipes = RecipeRepository.getRecipes()
    val toDelete: MutableState<Recipe?> = remember { mutableStateOf(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start
    ) {
        recipes.forEach { recipe ->
            Button(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .aspectRatio(2f)
                    .padding(vertical = 4.dp)
                    .combinedClickable(
                        onClick = { onEdit(recipe) },
                        onLongClick = { toDelete.value = recipe }
                    ),
                onClick = { onEdit(recipe) },
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(recipe.name)
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .aspectRatio(2f)
                .padding(vertical = 4.dp),
            onClick = onAdd,
            shape = RoundedCornerShape(4.dp)
        ) {
            Text("Add")
        }
        // Back button removed
    }

    toDelete.value?.let { recipe ->
        AlertDialog(
            onDismissRequest = { toDelete.value = null },
            confirmButton = {
                TextButton(onClick = {
                    RecipeRepository.removeRecipe(recipe)
                    toDelete.value = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = {
                    toDelete.value = null
                    onEdit(recipe)
                }) { Text("Edit") }
            },
            title = { Text(recipe.name) },
            text = { Text("Delete or edit this recipe?") }
        )
    }
}
