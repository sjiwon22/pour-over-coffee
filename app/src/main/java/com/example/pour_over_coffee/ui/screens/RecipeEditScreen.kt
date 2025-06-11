package com.example.pour_over_coffee.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pour_over_coffee.data.Recipe
import com.example.pour_over_coffee.data.RecipeRepository
import com.example.pour_over_coffee.data.Step

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeEditScreen(
    recipe: Recipe?,
    onDone: () -> Unit
) {
    var name by remember { mutableStateOf(recipe?.name ?: "") }
    var temp by remember { mutableStateOf(recipe?.waterTemp ?: 90) }
    var beans by remember { mutableStateOf(recipe?.beanAmount ?: 15) }
    val steps = remember { mutableStateOf(recipe?.steps?.toMutableList() ?: mutableListOf()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Recipe name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = temp.toString(), onValueChange = { temp = it.toIntOrNull() ?: temp }, label = { Text("Water temp") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = beans.toString(), onValueChange = { beans = it.toIntOrNull() ?: beans }, label = { Text("Bean amount") }, modifier = Modifier.fillMaxWidth())

        Text("Steps")
        LazyColumn(modifier = Modifier.weight(1f, true)) {
            itemsIndexed(steps.value) { index, step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .combinedClickable(onClick = {}, onLongClick = { steps.value.removeAt(index) }),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${step.waterAmount}ml - ${step.timeSec}s")
                }
            }
        }
        Button(onClick = {
            steps.value.add(Step(30,30))
        }) { Text("Add step") }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onDone) { Text("Cancel") }
            TextButton(onClick = {
                val updated = recipe ?: Recipe(name = name, waterTemp = temp, beanAmount = beans)
                updated.name = name
                updated.waterTemp = temp
                updated.beanAmount = beans
                updated.steps.clear(); updated.steps.addAll(steps.value)
                if (recipe == null) RecipeRepository.addRecipe(updated) else RecipeRepository.updateRecipe(updated)
                onDone()
            }) { Text("Save") }
        }
    }
}
