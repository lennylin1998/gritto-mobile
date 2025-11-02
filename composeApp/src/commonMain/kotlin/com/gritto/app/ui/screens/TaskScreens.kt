package com.gritto.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.gritto.app.model.SampleData
import com.gritto.app.model.TaskDetail
import com.gritto.app.navigation.GrittoNavRoutes
import com.gritto.app.ui.components.DetailFieldRow
import com.gritto.app.ui.components.EditActionBar
import com.gritto.app.ui.components.MissingEntityMessage
import com.gritto.app.ui.components.WarningDialog
import moe.tlaster.precompose.navigation.BackHandler
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun TaskScreen(
    taskId: String?,
    navigator: Navigator,
) {
    val detail = remember(taskId) { SampleData.taskDetails[taskId] }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Task") },
                navigationIcon = {
                    IconButton(onClick = navigator::goBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (detail != null) {
                        IconButton(
                            onClick = {
                                navigator.navigate(GrittoNavRoutes.taskEdit(detail.id))
                            },
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit")
                        }
                    }
                },
            )
        },
    ) { padding ->
        if (detail == null) {
            MissingEntityMessage(
                modifier = Modifier
                    .padding(padding)
                    .padding(24.dp),
                label = "Task not found",
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
            TaskHeader(detail = detail)
            DetailFieldRow(
                label = "Date",
                value = detail.date,
                onClick = { navigator.navigate(GrittoNavRoutes.taskEdit(detail.id)) },
            )
            DetailFieldRow(
                label = "Estimated Hours",
                value = "${detail.estimatedHours} h",
                onClick = { navigator.navigate(GrittoNavRoutes.taskEdit(detail.id)) },
            )
            DetailFieldRow(
                label = "Done",
                value = if (detail.done) "Yes" else "No",
                onClick = { navigator.navigate(GrittoNavRoutes.taskEdit(detail.id)) },
            )
            DetailFieldRow(
                label = "Description",
                value = detail.description,
                onClick = { navigator.navigate(GrittoNavRoutes.taskEdit(detail.id)) },
            )
        }
    }
}

@Composable
fun TaskEditScreen(
    taskId: String?,
    navigator: Navigator,
) {
    val original = remember(taskId) { SampleData.taskDetails[taskId] }
    var title by remember(original) { mutableStateOf(original?.title.orEmpty()) }
    var date by remember(original) { mutableStateOf(original?.date.orEmpty()) }
    var estimatedHours by remember(original) { mutableStateOf(original?.estimatedHours?.toString().orEmpty()) }
    var done by remember(original) { mutableStateOf(original?.done == true) }
    var description by remember(original) { mutableStateOf(original?.description.orEmpty()) }
    var showWarning by remember { mutableStateOf(false) }

    val isDirty = remember(title, date, estimatedHours, done, description, original) {
        original == null || title != original.title ||
            date != original.date ||
            estimatedHours != original.estimatedHours.toString() ||
            done != original.done ||
            description != original.description
    }

    fun attemptNavigateBack() {
        if (isDirty) {
            showWarning = true
        } else {
            navigator.goBack()
        }
    }

    BackHandler(onBack = ::attemptNavigateBack)

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Edit Task") },
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
                value = date,
                onValueChange = { date = it },
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            OutlinedTextField(
                value = estimatedHours,
                onValueChange = { estimatedHours = it.filter { char -> char.isDigit() || char == '.' } },
                label = { Text("Estimated Hours") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Checkbox(checked = done, onCheckedChange = { done = it })
                Text(
                    text = "Mark as done",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                maxLines = 6,
            )
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
private fun TaskHeader(detail: TaskDetail) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = detail.title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = if (detail.done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = if (detail.done) "Completed" else "In progress",
                style = MaterialTheme.typography.labelLarge,
                color = if (detail.done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Divider()
    }
}
