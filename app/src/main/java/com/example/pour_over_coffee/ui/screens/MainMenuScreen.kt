package com.example.pour_over_coffee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(0.8f),
            onClick = onMakeCoffee,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Make coffee")
        }
        Button(
            modifier = Modifier.fillMaxWidth(0.8f).padding(top = 8.dp),
            onClick = onEditRecipe,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Edit recipe")
        }
    }
}
