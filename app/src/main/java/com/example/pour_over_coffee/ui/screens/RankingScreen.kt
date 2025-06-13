package com.example.pour_over_coffee.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.pour_over_coffee.data.HistoryRepository
import com.example.pour_over_coffee.data.RankingStat

enum class RankColumn { NAME, WATER, SCORE, COUNT }

@Composable
fun RankingScreen(onBack: () -> Unit) {
    // Recompose whenever history changes
    val stats = HistoryRepository.getStats()
    val sortColumn = remember { mutableStateOf(RankColumn.SCORE) }
    val ascending = remember { mutableStateOf(false) }

    val sorted = remember(stats, sortColumn.value, ascending.value) {
        val comparator = when (sortColumn.value) {
            RankColumn.NAME -> compareBy<RankingStat> { it.name.lowercase() }
            RankColumn.WATER -> compareBy<RankingStat> { it.waterAmount }
            RankColumn.SCORE -> compareBy<RankingStat> { it.averageScore }
            RankColumn.COUNT -> compareBy<RankingStat> { it.count }
        }
        val list = stats.sortedWith(comparator)
        if (ascending.value) list else list.reversed()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .border(1.dp, Color.Gray),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Recipe", modifier = Modifier.weight(1f).clickable {
                    toggleSort(sortColumn, ascending, RankColumn.NAME)
                })
                Text("Water", modifier = Modifier.weight(1f).clickable {
                    toggleSort(sortColumn, ascending, RankColumn.WATER)
                })
                Text("Score", modifier = Modifier.weight(1f).clickable {
                    toggleSort(sortColumn, ascending, RankColumn.SCORE)
                })
                Text("Count", modifier = Modifier.weight(1f).clickable {
                    toggleSort(sortColumn, ascending, RankColumn.COUNT)
                })
            }
        }
        items(sorted) { stat ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray)
                    .clickable { HistoryRepository.clearScoresForRecipe(stat.name) },
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(stat.name, modifier = Modifier.weight(1f))
                Text("${stat.waterAmount}", modifier = Modifier.weight(1f))
                val scoreStr = String.format("%.1f", stat.averageScore)
                Text(scoreStr, modifier = Modifier.weight(1f))
                Text("${stat.count}", modifier = Modifier.weight(1f))
            }
        }
        item {
            TextButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .aspectRatio(2f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("Back")
            }
        }
    }
}

private fun toggleSort(colState: androidx.compose.runtime.MutableState<RankColumn>, ascState: androidx.compose.runtime.MutableState<Boolean>, column: RankColumn) {
    if (colState.value == column) {
        ascState.value = !ascState.value
    } else {
        colState.value = column
        ascState.value = true
    }
}
