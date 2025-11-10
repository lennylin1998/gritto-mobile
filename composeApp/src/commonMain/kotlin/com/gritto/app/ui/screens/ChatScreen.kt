package com.gritto.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gritto.app.model.ChatMessage
import com.gritto.app.ui.viewmodel.ChatUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    uiState: ChatUiState,
    onSendMessage: (String) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onShowGoalPreview: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.lastIndex)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Chat") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        val innerPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = contentPadding.calculateTopPadding() + padding.calculateTopPadding() + 16.dp,
            bottom = contentPadding.calculateBottomPadding() + padding.calculateBottomPadding() + 16.dp,
        )
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null && uiState.messages.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "We couldn’t start the chat.",
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

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        itemsIndexed(uiState.messages, key = { _, message -> message.id }) { index, message ->
                            val isAgent = message is ChatMessage.Agent
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = if (isAgent) Alignment.Start else Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                ChatBubble(message = message)
                                if (
                                    isAgent &&
                                    uiState.goalPreviewId != null &&
                                    index == uiState.messages.lastIndex
                                ) {
                                    Button(
                                        onClick = onShowGoalPreview,
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    ) {
                                        Text("Goal Preview")
                                    }
                                }
                            }
                        }
                    }
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Share a quick reflection...") },
                        maxLines = 4,
                        enabled = uiState.isSessionActive,
                        trailingIcon = {
                            IconButton(
                                enabled = input.isNotBlank() && uiState.isSessionActive,
                                onClick = {
                                    if (input.isNotBlank()) {
                                        onSendMessage(input)
                                        input = ""
                                    }
                                },
                            ) {
                                Icon(Icons.Filled.Send, contentDescription = "Send")
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send, keyboardType = KeyboardType.Text),
                        keyboardActions = KeyboardActions(onSend = {
                            if (input.isNotBlank() && uiState.isSessionActive) {
                                onSendMessage(input)
                                input = ""
                            }
                        }),
                    )
                    if (!uiState.isSessionActive) {
                        Text(
                            text = "Session finalized. Start a new reflection to continue.",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    uiState.error?.takeIf { uiState.messages.isNotEmpty() }?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isAgent = message is ChatMessage.Agent
    val background = if (isAgent) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary
    val foreground = if (isAgent) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary

    Surface(
        color = background,
        contentColor = foreground,
        shape = RoundedCornerShape(18.dp),
    ) {
        Text(
            text = message.text,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .widthIn(max = 280.dp),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
