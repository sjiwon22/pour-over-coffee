package com.example.pour_over_coffee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

@Composable
fun MainMenuScreen(onMakeCoffee: () -> Unit, onEditRecipe: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth(0.25f)
                .aspectRatio(2f),
            onClick = onMakeCoffee,
            shape = RoundedCornerShape(4.dp)
        ) {
            Text("Make coffee")
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(0.25f)
                .aspectRatio(2f),
            onClick = onEditRecipe,
            shape = RoundedCornerShape(4.dp)
        ) {
            Text("Edit recipe")
        }
    }
}
