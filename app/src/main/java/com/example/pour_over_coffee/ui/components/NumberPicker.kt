package com.example.pour_over_coffee.ui.components

import android.widget.NumberPicker
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            NumberPicker(context).apply {
                minValue = range.first
                maxValue = range.last
                this.value = value.coerceIn(range)
                setOnValueChangedListener { _, _, newVal -> onValueChange(newVal) }
            }
        },
        update = { picker ->
            picker.minValue = range.first
            picker.maxValue = range.last
            if (picker.value != value) {
                picker.value = value.coerceIn(range)
            }
        }
    )
}
