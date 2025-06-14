package com.example.pour_over_coffee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.pour_over_coffee.data.Recipe
import com.example.pour_over_coffee.data.RecipeRepository
import com.example.pour_over_coffee.data.HistoryRepository
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

    if (selected.value == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recipes) { recipe ->
                    val totalWater = recipe.steps.sumOf { it.waterAmount }
                    val score = HistoryRepository.getStats()
                        .firstOrNull { it.name == recipe.name }?.averageScore
                    val scoreStr = String.format("%.1f", score ?: 0.0)
                    val label = "${recipe.name}\n${recipe.beanAmount}g / ${totalWater}ml\nScore $scoreStr"
                    Button(
                        onClick = {
                            selected.value = recipe
                            HistoryRepository.addEntry(recipe)
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .aspectRatio(2f),
                        shape = RoundedCornerShape(4.dp)
                    ) { Text(label) }
                }
            }
            // Back button removed
        }
    } else {
        val recipe = selected.value!!
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Brewing: ${recipe.name}")
            Text("Use ${recipe.beanAmount}g beans, water at ${recipe.waterTemp}°C")
            recipe.steps.forEachIndexed { index, step ->
                val done = completed.getOrNull(index) == true
                val running = activeStep.value == index
                val enabled = !done && activeStep.value == null && completed.take(index).all { it }
                val label = when {
                    done -> "Step #${index + 1}\nWater ${step.waterAmount}ml\nDone"
                    running -> "Step #${index + 1}\nWater ${step.waterAmount}ml\n${timeLeft.value}s"
                    else -> "Step #${index + 1}\nWater ${step.waterAmount}ml\nwait for ${step.timeSec}s"
                }
                val colors = when {
                    running -> ButtonDefaults.buttonColors(containerColor = Color(0xFF87CEEB))
                    done -> ButtonDefaults.buttonColors(containerColor = Color(0xFF9CCC65))
                    else -> ButtonDefaults.buttonColors()
                }
                Button(
                    onClick = { activeStep.value = index },
                    enabled = enabled,
                    colors = colors,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .aspectRatio(2f),
                    shape = RoundedCornerShape(4.dp)
                ) { Text(label) }
            }
            if (completed.size > 0 && completed.all { it }) {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .aspectRatio(2f),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9CCC65))
                ) { Text("Have a good coffee") }
            }
            // Back button removed
        }
    }
}
