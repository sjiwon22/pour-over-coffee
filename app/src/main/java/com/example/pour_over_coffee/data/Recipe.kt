package com.example.pour_over_coffee.data

import java.util.UUID

/**
 * Represents a coffee recipe.
 */
data class Recipe(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var waterTemp: Int,
    var beanAmount: Int,
    val steps: MutableList<Step> = mutableListOf()
)

/**
 * Represents a single brew step.
 */
data class Step(
    var waterAmount: Int,
    var timeSec: Int
)
