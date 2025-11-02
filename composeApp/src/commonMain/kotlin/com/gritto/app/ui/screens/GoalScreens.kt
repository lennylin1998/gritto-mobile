package com.gritto.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.gritto.app.model.GoalDetail
import com.gritto.app.model.SampleData
import com.gritto.app.navigation.GrittoNavRoutes
import com.gritto.app.ui.components.DetailFieldRow
import com.gritto.app.ui.components.EditActionBar
import com.gritto.app.ui.components.MissingEntityMessage
import com.gritto.app.ui.components.WarningDialog
import moe.tlaster.precompose.navigation.BackHandler
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun GoalScreen(
    goalId: String?,
    navigator: Navigator,
) {
    val detail = remember(goalId) { SampleData.goalDetails[goalId] }
    @OptIn(ExperimentalMaterial3Api::class)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Goal") },
                navigationIcon = {
                    IconButton(onClick = navigator::goBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (detail != null) {
                        IconButton(onClick = { navigator.navigate(GrittoNavRoutes.goalEdit(detail.id)) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit")
                        }
                    }
                },
            )
        },
    ) { padding ->
        if (detail == null) {
            MissingEntityMessage(
                label = "Goal not found",
                modifier = Modifier
                    .padding(padding)
                    .padding(24.dp),
            )
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text(
                text = detail.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            )
            DetailFieldRow(
                label = "Start Time",
                value = detail.startTime,
                onClick = { navigator.navigate(GrittoNavRoutes.goalEdit(detail.id)) },
            )
            DetailFieldRow(
                label = "Description",
                value = detail.description,
                onClick = { navigator.navigate(GrittoNavRoutes.goalEdit(detail.id)) },
            )
            DetailFieldRow(
                label = "Context",
                value = detail.context,
                onClick = { navigator.navigate(GrittoNavRoutes.goalEdit(detail.id)) },
            )
            ColorPreview(detail.color)
        }
    }
}

@Composable
fun GoalEditScreen(
    goalId: String?,
    navigator: Navigator,
) {
    val original = remember(goalId) { SampleData.goalDetails[goalId] }
    var title by remember(original) { mutableStateOf(original?.title.orEmpty()) }
    var startTime by remember(original) { mutableStateOf(original?.startTime.orEmpty()) }
    var description by remember(original) { mutableStateOf(original?.description.orEmpty()) }
    var context by remember(original) { mutableStateOf(original?.context.orEmpty()) }
    var color by remember(original) { mutableStateOf(original?.color ?: defaultGoalColors.first()) }
    var showWarning by remember { mutableStateOf(false) }

    val isDirty = remember(title, startTime, description, context, color, original) {
        original == null || title != original.title ||
            startTime != original.startTime ||
            description != original.description ||
            context != original.context ||
            color != original.color
    }

    fun attemptNavigateBack() {
        if (isDirty) {
            showWarning = true
        } else {
            navigator.goBack()
        }
    }

    BackHandler(onBack = ::attemptNavigateBack)
    @OptIn(ExperimentalMaterial3Api::class)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Goal") },
                navigationIcon = {
                    IconButton(onClick = ::attemptNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        bottomBar = {
            EditActionBar(
                onCancel = ::attemptNavigateBack,
                onSave = { navigator.goBack() },
                isSaveEnabled = title.isNotBlank(),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .safeDrawingPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            )
            OutlinedTextField(
                value = startTime,
                onValueChange = { startTime = it },
                label = { Text("Start Time") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                maxLines = 5,
            )
            OutlinedTextField(
                value = context,
                onValueChange = { context = it },
                label = { Text("Context") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                maxLines = 6,
            )
            Text(
                text = "Color",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
            ) {
                items(defaultGoalColors) { swatch ->
                    ColorSwatch(
                        color = swatch,
                        selected = color == swatch,
                        onSelect = { color = swatch },
                    )
                }
            }
        }
    }

    WarningDialog(
        visible = showWarning,
        title = "Discard changes?",
        message = "You have unsaved edits. If you leave now your updates will be lost.",
        onConfirm = {
            showWarning = false
            navigator.goBack()
        },
        confirmLabel = "Discard",
        modifier = Modifier,
    )
}

@Composable
private fun ColorPreview(color: Long) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
                color = color.toColor(),
                shape = CircleShape,
            ) {}
            Column {
                Text(
                    text = "Color Accent",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "#${color.toULong().toString(16).uppercase()}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

private val defaultGoalColors = listOf(
    0xFF7C4DFF,
    0xFF26A69A,
    0xFFFFA000,
    0xFF42A5F5,
    0xFFEF5350,
    0xFF8E24AA,
    0xFF66BB6A,
    0xFFFF7043,
    0xFF5C6BC0,
    0xFFFFC107,
)

@Composable
private fun ColorSwatch(
    color: Long,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable(onClick = onSelect),
        shape = CircleShape,
        color = color.toColor(),
        border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface) else null,
    ) {}
}

private fun Long.toColor(): Color = Color(this.toULong())
