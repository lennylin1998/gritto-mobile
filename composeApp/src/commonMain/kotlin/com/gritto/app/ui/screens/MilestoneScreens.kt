package com.gritto.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.gritto.app.model.MilestoneStatus
import com.gritto.app.model.SampleData
import com.gritto.app.navigation.GrittoNavRoutes
import com.gritto.app.ui.components.DetailFieldRow
import com.gritto.app.ui.components.EditActionBar
import com.gritto.app.ui.components.MissingEntityMessage
import com.gritto.app.ui.components.WarningDialog
import moe.tlaster.precompose.navigation.BackHandler
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun MilestoneScreen(
    milestoneId: String?,
    navigator: Navigator,
) {
    val detail = remember(milestoneId) { SampleData.milestoneDetails[milestoneId] }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Milestone") },
                navigationIcon = {
                    IconButton(onClick = navigator::goBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (detail != null) {
                        IconButton(onClick = { navigator.navigate(GrittoNavRoutes.milestoneEdit(detail.id)) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit")
                        }
                    }
                },
            )
        },
    ) { padding ->
        if (detail == null) {
            MissingEntityMessage(
                label = "Milestone not found",
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
                label = "Date",
                value = detail.date,
                onClick = { navigator.navigate(GrittoNavRoutes.milestoneEdit(detail.id)) },
            )
            DetailFieldRow(
                label = "Estimated Hours",
                value = "${detail.estimatedHours} h",
                onClick = { navigator.navigate(GrittoNavRoutes.milestoneEdit(detail.id)) },
            )
            DetailFieldRow(
                label = "Status",
                value = detail.status.label,
                onClick = { navigator.navigate(GrittoNavRoutes.milestoneEdit(detail.id)) },
            )
            DetailFieldRow(
                label = "Description",
                value = detail.description,
                onClick = { navigator.navigate(GrittoNavRoutes.milestoneEdit(detail.id)) },
            )
        }
    }
}

@Composable
fun MilestoneEditScreen(
    milestoneId: String?,
    navigator: Navigator,
) {
    val original = remember(milestoneId) { SampleData.milestoneDetails[milestoneId] }
    var title by remember(original) { mutableStateOf(original?.title.orEmpty()) }
    var date by remember(original) { mutableStateOf(original?.date.orEmpty()) }
    var estimatedHours by remember(original) { mutableStateOf(original?.estimatedHours?.toString().orEmpty()) }
    var status by remember(original) { mutableStateOf(original?.status ?: MilestoneStatus.InProgress) }
    var description by remember(original) { mutableStateOf(original?.description.orEmpty()) }
    var statusMenuExpanded by remember { mutableStateOf(false) }
    var showWarning by remember { mutableStateOf(false) }

    val isDirty = remember(title, date, estimatedHours, status, description, original) {
        original == null || title != original.title ||
            date != original.date ||
            estimatedHours != original.estimatedHours.toString() ||
            status != original.status ||
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
                title = { Text("Edit Milestone") },
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
            Column {
                OutlinedTextField(
                    value = status.label,
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { statusMenuExpanded = true },
                    label = { Text("Status") },
                    enabled = true,
                    readOnly = true,
                )
                DropdownMenu(
                    expanded = statusMenuExpanded,
                    onDismissRequest = { statusMenuExpanded = false },
                ) {
                    MilestoneStatus.values().forEach { candidate ->
                        DropdownMenuItem(
                            text = { Text(candidate.label) },
                            onClick = {
                                status = candidate
                                statusMenuExpanded = false
                            },
                        )
                    }
                }
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
