package com.example.pour_over_coffee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
// weight extension from ColumnScope is used directly within layout scopes
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberDismissState
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.pour_over_coffee.ui.components.NumberPicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pour_over_coffee.data.Recipe
import com.example.pour_over_coffee.data.RecipeRepository
import com.example.pour_over_coffee.data.Step

@OptIn(ExperimentalMaterialApi::class)
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
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var editingWater by remember { mutableStateOf(0) }
    var editingTime by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start
    ) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Recipe name") }, modifier = Modifier.fillMaxWidth())
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Water temp")
            NumberPicker(
                value = temp,
                onValueChange = { temp = it },
                range = 60..100
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Bean amount")
            NumberPicker(
                value = beans,
                onValueChange = { beans = it },
                range = 5..60
            )
        }

        Text("Steps")
        LazyColumn(modifier = Modifier.weight(1f, true)) {
            itemsIndexed(steps) { index, step ->
                @OptIn(ExperimentalMaterialApi::class)
                val dismissState = rememberDismissState(
                    confirmStateChange = {
                        if (it == DismissValue.DismissedToEnd) {
                            steps.removeAt(index)
                            false
                        } else {
                            true
                        }
                    }
                )

                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.StartToEnd),
                    background = {
                        val color = if (dismissState.targetValue == DismissValue.Default) Color.Transparent else Color.Red
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(start = 16.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                        }
                    },
                    dismissContent = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    editingIndex = index
                                    editingWater = step.waterAmount
                                    editingTime = step.timeSec
                                },
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Step ${index + 1}")
                            Text(
                                "Water ${step.waterAmount}ml",
                                modifier = Modifier
                                    .weight(1f)
                            )
                            Text(
                                "Time ${step.timeSec}s",
                                modifier = Modifier
                                    .weight(1f)
                            )
                        }
                    }
                )
            }
            item {
                Button(
                    onClick = { steps.add(Step(30, 30)) },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .aspectRatio(2.8f)
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(4.dp)
                ) { Text("Add step") }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            // Back button removed
            if (recipe != null) {
                TextButton(
                    onClick = { onDelete(recipe) },
                    modifier = Modifier.fillMaxWidth(0.5f).aspectRatio(2f),
                    shape = RoundedCornerShape(4.dp)
                ) { Text("Delete") }
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
                modifier = Modifier.fillMaxWidth(0.5f).aspectRatio(2f),
                shape = RoundedCornerShape(4.dp)
            ) { Text("Save") }
        }
        editingIndex?.let { idx ->
            AlertDialog(
                onDismissRequest = { editingIndex = null },
                confirmButton = {
                    TextButton(onClick = {
                        steps[idx] = Step(editingWater, editingTime)
                        editingIndex = null
                    }) { Text("Save") }
                },
                dismissButton = {
                    TextButton(onClick = { editingIndex = null }) { Text("Cancel") }
                },
                title = { Text("Edit Step ${idx + 1}") },
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Water (ml)")
                            NumberPicker(
                                value = editingWater,
                                onValueChange = { editingWater = it },
                                range = 0..500
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Time (s)")
                            NumberPicker(
                                value = editingTime,
                                onValueChange = { editingTime = it },
                                range = 0..120,
                                step = 5
                            )
                        }
                    }
                }
            )
        }
    }
}
