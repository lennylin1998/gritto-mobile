package com.gritto.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EditActionBar(
    onCancel: () -> Unit,
    onSave: () -> Unit,
    isSaveEnabled: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextButton(
            modifier = Modifier.weight(1f),
            onClick = onCancel,
        ) {
            Text("Cancel")
        }
        Button(
            modifier = Modifier.weight(1f),
            enabled = isSaveEnabled,
            onClick = onSave,
        ) {
            Text("Save")
        }
    }
}
