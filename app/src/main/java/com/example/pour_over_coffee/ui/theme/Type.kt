package com.example.pour_over_coffee.ui.theme

import android.graphics.Typeface
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Consolas = FontFamily(Typeface.create("Consolas", Typeface.NORMAL))

// Set of Material typography styles to start with, all using Consolas
val Typography = androidx.compose.material3.Typography().run {
    copy(
        displayLarge = displayLarge.copy(fontFamily = Consolas),
        displayMedium = displayMedium.copy(fontFamily = Consolas),
        displaySmall = displaySmall.copy(fontFamily = Consolas),
        headlineLarge = headlineLarge.copy(fontFamily = Consolas),
        headlineMedium = headlineMedium.copy(fontFamily = Consolas),
        headlineSmall = headlineSmall.copy(fontFamily = Consolas),
        titleLarge = titleLarge.copy(fontFamily = Consolas),
        titleMedium = titleMedium.copy(fontFamily = Consolas),
        titleSmall = titleSmall.copy(fontFamily = Consolas),
        bodyLarge = bodyLarge.copy(fontFamily = Consolas),
        bodyMedium = bodyMedium.copy(fontFamily = Consolas),
        bodySmall = bodySmall.copy(fontFamily = Consolas),
        labelLarge = labelLarge.copy(fontFamily = Consolas),
        labelMedium = labelMedium.copy(fontFamily = Consolas),
        labelSmall = labelSmall.copy(fontFamily = Consolas),
    )
}
