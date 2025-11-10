package com.gritto.app.ui.viewmodel

import com.gritto.app.data.network.ApiResult
import com.gritto.app.data.remote.model.ChatContextDto
import com.gritto.app.data.remote.model.ChatEntryDto
import com.gritto.app.data.remote.model.ChatGoalPreviewGoalDto
import com.gritto.app.data.remote.model.ChatGoalPreviewRequestDto
import com.gritto.app.data.remote.model.ChatMessageRequestDto
import com.gritto.app.data.remote.model.GoalPreviewPayloadDto
import com.gritto.app.data.repository.GrittoRepository
import com.gritto.app.model.ChatMessage
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

data class ChatUiState(
    val isLoading: Boolean = true,
    val messages: List<ChatMessage> = emptyList(),
    val sessionId: String? = null,
    val goalPreviewId: String? = null,
    val isSessionActive: Boolean = true,
    val error: String? = null,
)

class ChatViewModel(
    private val repository: GrittoRepository,
    private val userIdProvider: () -> String?,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var sessionContext: ChatContextDto? = null
    private var loadingSession = false
    private var latestGoalPreview: ChatGoalPreviewRequestDto? = null

    fun ensureSession() {
        if (_uiState.value.sessionId != null || loadingSession) return
        viewModelScope.launch {
            loadLatestSession()
        }
    }

    fun retry() {
        viewModelScope.launch {
            loadLatestSession(force = true)
        }
    }

    private suspend fun loadLatestSession(force: Boolean = false) {
        if (loadingSession && !force) return
        loadingSession = true
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        when (val result = repository.fetchLatestGoalSession()) {
            is ApiResult.Success -> {
                val session = result.value.data
                sessionContext = session.context
                latestGoalPreview = null
                val sessionActive = session.sessionActive ?: true
                val history = if (sessionActive) {
                    when (val historyResult = repository.fetchGoalSessionHistory(session.sessionId)) {
                        is ApiResult.Success -> mapChatEntries(historyResult.value.data.entries)
                        is ApiResult.Error -> {
                            _uiState.value = ChatUiState(
                                isLoading = false,
                                error = historyResult.message,
                            )
                            loadingSession = false
                            return
                        }
                    }
                } else {
                    mapChatEntries(null)
                }
                _uiState.value = ChatUiState(
                    isLoading = false,
                    sessionId = session.sessionId,
                    goalPreviewId = session.goalPreviewId,
                    isSessionActive = sessionActive,
                    messages = history,
                )
            }
            is ApiResult.Error -> {
                _uiState.value = ChatUiState(
                    isLoading = false,
                    error = result.message,
                )
            }
        }
        loadingSession = false
    }

    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        val sessionId = _uiState.value.sessionId ?: run {
            ensureSession()
            return
        }
        val userId = userIdProvider() ?: run {
            _uiState.update { it.copy(error = "Profile not loaded yet") }
            return
        }
        val localUserMessage = ChatMessage.User(
            id = "user-${getTimeMillis()}",
            text = trimmed,
        )
        _uiState.update { state ->
            state.copy(
                messages = state.messages + localUserMessage,
                error = null,
            )
        }
        viewModelScope.launch {
            val request = ChatMessageRequestDto(
                sessionId = sessionId,
                userId = userId,
                message = trimmed,
                context = sessionContext,
                goalPreview = latestGoalPreview,
            )
            when (val result = repository.sendGoalSessionMessage(request)) {
                is ApiResult.Success -> {
                    result.value.context?.let { updatedContext ->
                        sessionContext = updatedContext
                    }
                    val replyText = result.value.reply
                    val agentMessage = ChatMessage.Agent(
                        id = "agent-${getTimeMillis()}",
                        text = replyText,
                    )
                    val newGoalPreviewId = result.value.state?.goalPreviewId
                        ?: result.value.action?.payload?.goalPreview?.id
                        ?: result.value.action?.payload?.goalPreviewId
                    _uiState.update { state ->
                        state.copy(
                            messages = state.messages + agentMessage,
                            goalPreviewId = newGoalPreviewId ?: state.goalPreviewId,
                            isSessionActive = result.value.state?.sessionActive ?: state.isSessionActive,
                        )
                    }
                }

                is ApiResult.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
            }
        }
    }

    private fun mapChatEntries(entries: List<ChatEntryDto>?): List<ChatMessage> {
        val mapped = entries
            ?.mapIndexed { index, entry ->
                val id = entry.timestamp.ifBlank { "chat-entry-$index" }
                if (entry.sender.equals("user", ignoreCase = true)) {
                    ChatMessage.User(id = id, text = entry.message)
                } else {
                    ChatMessage.Agent(id = id, text = entry.message)
                }
            }
            ?.takeIf { it.isNotEmpty() }

        return mapped ?: listOf(
            ChatMessage.Agent(
                id = "agent-welcome",
                text = "Tell me what you’d like to work on!",
            ),
        )
    }
}
