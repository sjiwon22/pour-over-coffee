package com.example.pour_over_coffee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.background
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import com.example.pour_over_coffee.ui.components.NumberPicker
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberDismissState
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ButtonDefaults
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.pour_over_coffee.data.HistoryEntry
import com.example.pour_over_coffee.data.HistoryRepository

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HistoryScreen(onBack: () -> Unit) {
    val history = HistoryRepository.getHistory()
    val editing: MutableState<HistoryEntry?> = remember { mutableStateOf(null) }
    val scoreText = remember { mutableStateOf("") }
    val format = remember { SimpleDateFormat("yy/MM/dd HH:mm", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start
    ) {
        history.toList().forEach { entry ->
            val dismissState = rememberDismissState(
                confirmStateChange = {
                    if (it == DismissValue.DismissedToStart) {
                        HistoryRepository.remove(entry)
                        false
                    } else {
                        true
                    }
                }
            )

            SwipeToDismiss(
                state = dismissState,
                directions = setOf(DismissDirection.EndToStart),
                background = {
                    val color = if (dismissState.targetValue == DismissValue.Default) Color.Transparent else Color.Red
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color)
                            .padding(end = 16.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                    }
                },
                dismissContent = {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .aspectRatio(2f),
                        onClick = {
                            editing.value = entry
                            scoreText.value = entry.score?.toString() ?: ""
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        val time = format.format(Date(entry.timestamp))
                        val scoreLine = if (entry.score != null) "Score ${entry.score}" else "Score"
                        val label = "${entry.name} (${entry.waterAmount}ml)\n$time\n$scoreLine"
                        Text(label)
                    }
                }
            )
        }
        TextButton(
            onClick = { HistoryRepository.clearHistory() },
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .aspectRatio(2f),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.textButtonColors(containerColor = Color.LightGray)
        ) {
            Text("Clear")
        }
    }

    editing.value?.let { entry ->
        AlertDialog(
            onDismissRequest = { editing.value = null },
            confirmButton = {
                TextButton(onClick = {
                    val score = scoreText.value.toIntOrNull()
                    entry.score = score
                    HistoryRepository.update(entry)
                    editing.value = null
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { editing.value = null }) { Text("Cancel") }
            },
            title = { Text(entry.name) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Score")
                    NumberPicker(
                        value = scoreText.value.toIntOrNull() ?: 0,
                        onValueChange = { scoreText.value = it.toString() },
                        range = 0..10
                    )
                }
            }
        )
    }
}
