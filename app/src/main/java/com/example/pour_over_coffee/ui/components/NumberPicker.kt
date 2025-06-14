package com.example.pour_over_coffee.ui.components

import android.widget.NumberPicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    step: Int = 1,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    val values = remember(range, step) {
        (range.first..range.last step step).toList()
    }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            NumberPicker(context).apply {
                minValue = 0
                maxValue = values.lastIndex
                displayedValues = values.map { it.toString() }.toTypedArray()
                this.value = values.indexOf(value.coerceIn(range))
                setOnValueChangedListener { _, _, newVal ->
                    onValueChange(values[newVal])
                }
            }
        },
        update = { picker ->
            picker.minValue = 0
            picker.maxValue = values.lastIndex
            picker.displayedValues = values.map { it.toString() }.toTypedArray()
            val newIndex = values.indexOf(value.coerceIn(range))
            if (picker.value != newIndex) {
                picker.value = newIndex
            }
        }
    )
}
