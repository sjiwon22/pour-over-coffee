package com.example.pour_over_coffee.ui.screens

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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.example.pour_over_coffee.data.Recipe
import com.example.pour_over_coffee.data.RecipeRepository
import com.example.pour_over_coffee.data.Step

@Composable
fun RecipeEditScreen(
    recipe: Recipe?,
    onDone: () -> Unit,
    onDelete: (Recipe) -> Unit
) {
    var name by remember { mutableStateOf(recipe?.name ?: "") }
    var temp by remember { mutableStateOf(recipe?.waterTemp ?: 90) }
    var beans by remember { mutableStateOf(recipe?.beanAmount ?: 15) }
    val steps = remember {
        mutableStateListOf<Step>().apply {
            recipe?.steps?.forEach { add(it.copy()) }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Recipe name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = temp.toString(), onValueChange = { temp = it.toIntOrNull() ?: temp }, label = { Text("Water temp") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = beans.toString(), onValueChange = { beans = it.toIntOrNull() ?: beans }, label = { Text("Bean amount") }, modifier = Modifier.fillMaxWidth())

        Text("Steps")
        LazyColumn(modifier = Modifier.weight(1f, true)) {
            itemsIndexed(steps) { index, step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Step ${index + 1}")
                    OutlinedTextField(
                        value = step.waterAmount.toString(),
                        onValueChange = { input ->
                            val amt = input.toIntOrNull() ?: step.waterAmount
                            steps[index] = step.copy(waterAmount = amt)
                        },
                        label = { Text("Water (ml)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = step.timeSec.toString(),
                        onValueChange = { input ->
                            val t = input.toIntOrNull() ?: step.timeSec
                            steps[index] = step.copy(timeSec = t)
                        },
                        label = { Text("Time (s)") },
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { steps.removeAt(index) }) {
                        Text("Delete")
                    }
                }
            }
            item {
                Button(
                    onClick = { steps.add(Step(30, 30)) },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Add step") }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onDone, shape = RoundedCornerShape(8.dp)) { Text("Back") }
            if (recipe != null) {
                TextButton(onClick = { onDelete(recipe) }, shape = RoundedCornerShape(8.dp)) { Text("Delete") }
            }
            TextButton(
                enabled = name.isNotBlank(),
                onClick = {
                val updated = recipe ?: Recipe(name = name, waterTemp = temp, beanAmount = beans)
                updated.name = name
                updated.waterTemp = temp
                updated.beanAmount = beans
                updated.steps.clear(); updated.steps.addAll(steps)
                if (recipe == null) RecipeRepository.addRecipe(updated) else RecipeRepository.updateRecipe(updated)
                onDone()
            },
                shape = RoundedCornerShape(8.dp)
            ) { Text("Save") }
        }
    }
}
