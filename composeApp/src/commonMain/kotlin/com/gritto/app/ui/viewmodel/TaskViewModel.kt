package com.gritto.app.ui.viewmodel

import com.gritto.app.data.network.ApiResult
import com.gritto.app.data.remote.model.TaskUpdateRequestDto
import com.gritto.app.data.remote.model.toTaskDetail
import com.gritto.app.data.repository.GrittoRepository
import com.gritto.app.model.TaskDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

data class TaskDetailUiState(
    val isLoading: Boolean = true,
    val task: TaskDetail? = null,
    val error: String? = null,
)

class TaskViewModel(
    private val repository: GrittoRepository,
    private val taskId: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = TaskDetailUiState(isLoading = true)
            when (val result = repository.fetchTaskDetail(taskId)) {
                is ApiResult.Success -> {
                    _uiState.value = TaskDetailUiState(
                        isLoading = false,
                        task = result.value.data.toTaskDetail(),
                    )
                }

                is ApiResult.Error -> {
                    _uiState.value = TaskDetailUiState(
                        isLoading = false,
                        error = result.message,
                    )
                }
            }
        }
    }

    fun toggleDone(done: Boolean) {
        viewModelScope.launch {
            val response = repository.updateTask(taskId, TaskUpdateRequestDto(done = done))
            if (response is ApiResult.Success) {
                _uiState.value = _uiState.value.copy(task = response.value.data.toTaskDetail(), error = null)
            }
        }
    }
}
