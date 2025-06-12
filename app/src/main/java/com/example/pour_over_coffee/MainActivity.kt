package com.example.pour_over_coffee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.activity.compose.BackHandler
import androidx.compose.ui.tooling.preview.Preview
import com.example.pour_over_coffee.data.Recipe
import com.example.pour_over_coffee.data.RecipeRepository
import com.example.pour_over_coffee.data.HistoryRepository
import com.example.pour_over_coffee.ui.screens.MainMenuScreen
import com.example.pour_over_coffee.ui.screens.MakeCoffeeScreen
import com.example.pour_over_coffee.ui.screens.RecipeEditScreen
import com.example.pour_over_coffee.ui.screens.RecipeListScreen
import com.example.pour_over_coffee.ui.screens.HistoryScreen
import com.example.pour_over_coffee.ui.screens.RankingScreen
import com.example.pour_over_coffee.ui.theme.PourovercoffeeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        RecipeRepository.initialize(this)
        HistoryRepository.initialize(this)
        setContent { App() }
    }
}

enum class Screen { MAIN, LIST, EDIT, BREW, HISTORY, RANKING }

@Composable
fun App() {
    PourovercoffeeTheme {
        val screen = remember { mutableStateOf(Screen.MAIN) }
        val editingRecipe = remember { mutableStateOf<Recipe?>(null) }

        fun goBack() {
            when (screen.value) {
                Screen.LIST, Screen.BREW, Screen.HISTORY, Screen.RANKING ->
                    screen.value = Screen.MAIN
                Screen.EDIT -> screen.value = Screen.LIST
                else -> {}
            }
        }

        BackHandler(enabled = screen.value != Screen.MAIN) {
            goBack()
        }

        var drag by remember { mutableStateOf(0f) }

        Box(
            modifier = Modifier.pointerInput(screen.value) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, delta -> drag += delta },
                    onDragEnd = {
                        if (drag > 100f) {
                            goBack()
                        }
                        drag = 0f
                    },
                    onDragCancel = { drag = 0f }
                )
            }
        ) {
            Surface {
                when (screen.value) {
                Screen.MAIN -> MainMenuScreen(
                    onMakeCoffee = { screen.value = Screen.BREW },
                    onEditRecipe = { screen.value = Screen.LIST },
                    onHistory = { screen.value = Screen.HISTORY },
                    onRanking = { screen.value = Screen.RANKING }
                )
                Screen.LIST -> RecipeListScreen(
                    onAdd = {
                        editingRecipe.value = null
                        screen.value = Screen.EDIT
                    },
                    onEdit = { recipe ->
                        editingRecipe.value = recipe
                        screen.value = Screen.EDIT
                    },
                    onBack = { screen.value = Screen.MAIN }
                )
                Screen.EDIT -> RecipeEditScreen(
                    recipe = editingRecipe.value,
                    onDone = { screen.value = Screen.LIST },
                    onDelete = { recipe ->
                        RecipeRepository.removeRecipe(recipe)
                        screen.value = Screen.LIST
                    }
                )
                Screen.BREW -> MakeCoffeeScreen(
                    onDone = { screen.value = Screen.MAIN }
                )
                Screen.HISTORY -> HistoryScreen(
                    onBack = { screen.value = Screen.MAIN }
                )
                Screen.RANKING -> RankingScreen(
                    onBack = { screen.value = Screen.MAIN }
                )
            }
        }
    }
    // Close PourovercoffeeTheme
}

// Close App composable
}


@Preview
@Composable
fun AppPreview() {
    App()
}
