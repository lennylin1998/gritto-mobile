package com.gritto.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gritto.app.model.ProfileInfo
import com.gritto.app.ui.components.DetailFieldRow
import com.gritto.app.ui.components.EditActionBar
import com.gritto.app.ui.components.WarningDialog
import com.gritto.app.ui.viewmodel.ProfileUiState
import moe.tlaster.precompose.navigation.BackHandler

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
    onEditHours: () -> Unit = {},
    onSignOut: () -> Unit = {},
) {
    when {
        uiState.isLoading -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.error != null -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "We couldn’t load your profile.",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = uiState.error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }

        uiState.profile != null -> {
            val profile = uiState.profile
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = contentPadding.calculateTopPadding() + 24.dp,
                        bottom = contentPadding.calculateBottomPadding() + 24.dp,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Surface(
                    modifier = Modifier.size(108.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    tonalElevation = 4.dp,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(28.dp),
                    )
                }
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = profile.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    DetailFieldRow(label = "Name", value = profile.name)
                    DetailFieldRow(label = "Email", value = profile.email)
                    DetailFieldRow(
                        label = "Available Hours per Week",
                        value = profile.availableHoursPerWeek.toString(),
                        onClick = onEditHours,
                    )
                }
                Button(
                    onClick = onSignOut,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(imageVector = Icons.Filled.Logout, contentDescription = "Sign out")
                    Text(text = "Sign out", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileEditScreen(
    profile: ProfileInfo,
    isSaving: Boolean,
    errorMessage: String?,
    onSave: (Int) -> Unit,
    onCancel: () -> Unit,
) {
    var hoursText by remember(profile) { mutableStateOf(profile.availableHoursPerWeek.toString()) }
    var showWarning by remember { mutableStateOf(false) }

    val isDirty = remember(hoursText, profile) { hoursText != profile.availableHoursPerWeek.toString() }

    fun attemptNavigateBack() {
        if (isDirty) {
            showWarning = true
        } else {
            onCancel()
        }
    }

    BackHandler(onBack = ::attemptNavigateBack)

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Edit Profile") },
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
                onSave = {
                    val parsed = hoursText.toIntOrNull()
                    if (parsed != null) {
                        onSave(parsed)
                    }
                },
                isSaveEnabled = !isSaving && hoursText.toIntOrNull() != null,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .safeDrawingPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            DetailFieldRow(label = "Name", value = profile.name)
            DetailFieldRow(label = "Email", value = profile.email)
            OutlinedTextField(
                value = hoursText,
                onValueChange = { value -> hoursText = value.filter { it.isDigit() } },
                label = { Text("Available Hours per Week") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }

    WarningDialog(
        visible = showWarning,
        title = "Discard changes?",
        message = "You have unsaved edits. If you leave now your updates will be lost.",
        onConfirm = {
            showWarning = false
            onCancel()
        },
        confirmLabel = "Discard",
        modifier = Modifier,
    )
}
