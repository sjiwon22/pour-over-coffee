package com.example.pour_over_coffee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.example.pour_over_coffee.data.Recipe
import com.example.pour_over_coffee.data.RecipeRepository
import com.example.pour_over_coffee.ui.screens.MainMenuScreen
import com.example.pour_over_coffee.ui.screens.MakeCoffeeScreen
import com.example.pour_over_coffee.ui.screens.RecipeEditScreen
import com.example.pour_over_coffee.ui.screens.RecipeListScreen
import com.example.pour_over_coffee.ui.theme.PourovercoffeeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        RecipeRepository.initialize(this)
        setContent { App() }
    }
}

enum class Screen { MAIN, LIST, EDIT, BREW }

@Composable
fun App() {
    PourovercoffeeTheme {
        val screen = remember { mutableStateOf(Screen.MAIN) }
        val editingRecipe = remember { mutableStateOf<Recipe?>(null) }

        Surface {
            when (screen.value) {
                Screen.MAIN -> MainMenuScreen(
                    onMakeCoffee = { screen.value = Screen.BREW },
                    onEditRecipe = { screen.value = Screen.LIST }
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
            }
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    App()
}
