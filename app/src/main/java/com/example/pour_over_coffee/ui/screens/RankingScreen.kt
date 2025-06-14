package com.example.pour_over_coffee.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
// weight from RowScope is used implicitly inside HeaderCell
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HeaderCell("Recipe", RankColumn.NAME, sortColumn, ascending)
                HeaderCell("Water", RankColumn.WATER, sortColumn, ascending)
                HeaderCell("Score", RankColumn.SCORE, sortColumn, ascending)
                HeaderCell("Count", RankColumn.COUNT, sortColumn, ascending)
            }
        }
        itemsIndexed(sorted) { index, stat ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${index + 1}", modifier = Modifier.width(24.dp))
                    Text(stat.name, modifier = Modifier.weight(1f))
                    Text("${stat.waterAmount}", modifier = Modifier.weight(1f))
                    val scoreStr = String.format("%.1f", stat.averageScore)
                    Text(scoreStr, modifier = Modifier.weight(1f))
                    Text("${stat.count}", modifier = Modifier.weight(1f))
                }
            }
            Divider()
        }
        // Back button removed
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

@Composable
private fun RowScope.HeaderCell(
    title: String,
    column: RankColumn,
    sortColumn: androidx.compose.runtime.MutableState<RankColumn>,
    ascending: androidx.compose.runtime.MutableState<Boolean>
) {
    Row(
        modifier = Modifier
            .weight(1f)
            .clickable { toggleSort(sortColumn, ascending, column) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title)
        if (sortColumn.value == column) {
            val icon = if (ascending.value) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown
            Icon(icon, contentDescription = null)
        }
    }
}
