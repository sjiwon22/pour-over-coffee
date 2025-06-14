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
import androidx.compose.ui.unit.dp
import com.example.pour_over_coffee.data.Recipe
import com.example.pour_over_coffee.data.RecipeRepository
import com.example.pour_over_coffee.data.Step

enum class StepField { WATER, TIME }
data class EditInfo(val index: Int, val field: StepField)

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
    var editing by remember { mutableStateOf<EditInfo?>(null) }

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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Step ${index + 1}")
                    Text(
                        "Water ${step.waterAmount}ml",
                        modifier = Modifier
                            .weight(1f)
                            .clickable { editing = EditInfo(index, StepField.WATER) }
                    )
                    Text(
                        "Time ${step.timeSec}s",
                        modifier = Modifier
                            .weight(1f)
                            .clickable { editing = EditInfo(index, StepField.TIME) }
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
                        .fillMaxWidth(0.5f)
                        .aspectRatio(2f)
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
        editing?.let { info ->
            val current = when (info.field) {
                StepField.WATER -> steps[info.index].waterAmount
                StepField.TIME -> steps[info.index].timeSec
            }
            AlertDialog(
                onDismissRequest = { editing = null },
                confirmButton = { TextButton(onClick = { editing = null }) { Text("OK") } },
                title = { Text(if (info.field == StepField.WATER) "Water (ml)" else "Time (s)") },
                text = {
                    NumberPicker(
                        value = current,
                        onValueChange = { newVal ->
                            steps[info.index] = when (info.field) {
                                StepField.WATER -> steps[info.index].copy(waterAmount = newVal)
                                StepField.TIME -> steps[info.index].copy(timeSec = newVal)
                            }
                        },
                        range = if (info.field == StepField.WATER) 0..500 else 0..120
                    )
                }
            )
        }
    }
}
