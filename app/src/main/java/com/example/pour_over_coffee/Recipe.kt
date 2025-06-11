package com.example.pour_over_coffee

data class PourOverStep(
    val waterGrams: Int,
    val waitSeconds: Int
)

data class PourOverRecipe(
    val beansGrams: Int,
    val totalWater: Int,
    val steps: List<PourOverStep>
)

val sampleRecipe = PourOverRecipe(
    beansGrams = 20,
    totalWater = 300,
    steps = listOf(
        PourOverStep(waterGrams = 60, waitSeconds = 30),
        PourOverStep(waterGrams = 70, waitSeconds = 30),
        PourOverStep(waterGrams = 90, waitSeconds = 30),
        PourOverStep(waterGrams = 80, waitSeconds = 0)
    )
)
