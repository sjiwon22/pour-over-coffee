package com.example.pour_over_coffee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.pour_over_coffee.data.HistoryEntry
import com.example.pour_over_coffee.data.HistoryRepository

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
        history.forEach { entry ->
            Button(
                modifier = Modifier
                    .fillMaxWidth(0.25f)
                    .aspectRatio(2f),
                onClick = {
                    editing.value = entry
                    scoreText.value = entry.score?.toString() ?: ""
                },
                shape = RoundedCornerShape(4.dp)
            ) {
                val time = format.format(Date(entry.timestamp))
                val base = "${entry.name} ${entry.waterAmount}ml $time"
                val label = if (entry.score != null) "$base (score ${entry.score})" else base
                Text(label)
            }
        }
        TextButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth(0.25f)
                .aspectRatio(2f),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text("Back")
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
                OutlinedTextField(
                    value = scoreText.value,
                    onValueChange = { scoreText.value = it },
                    label = { Text("Score") }
                )
            }
        )
    }
}
