package com.gritto.app.ui.viewmodel

import com.gritto.app.data.network.ApiResult
import com.gritto.app.data.remote.model.ActiveGoalDto
import com.gritto.app.data.remote.model.TaskSummaryDto
import com.gritto.app.data.remote.model.TaskUpdateRequestDto
import com.gritto.app.data.repository.GrittoRepository
import com.gritto.app.ui.model.GoalUiModel
import com.gritto.app.ui.model.TaskListUiModel
import com.gritto.app.ui.model.TaskUiModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope


private fun currentDate(): LocalDate =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

data class HomeUiState(
    val isLoading: Boolean = false,
    val taskLists: List<TaskListUiModel> = emptyList(),
    val goals: List<GoalUiModel> = emptyList(),
    val error: String? = null,
    val selectedDate: LocalDate = currentDate(),
)

class HomeViewModel(
    private val repository: GrittoRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refresh(currentDate())
    }

    fun onTaskListsChange(updated: List<TaskListUiModel>) {
        _uiState.value = _uiState.value.copy(taskLists = updated)
    }

    fun onGoalsReordered(updated: List<GoalUiModel>) {
        _uiState.value = _uiState.value.copy(goals = updated)
    }

    fun refresh(date: LocalDate = _uiState.value.selectedDate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, selectedDate = date)
            val tasksDeferred = async { repository.fetchTasksForDay(date) }
            val goalsDeferred = async { repository.fetchActiveGoals() }

            val (tasksResult, goalsResult) = awaitAll(tasksDeferred, goalsDeferred)

            val taskLists = when (tasksResult) {
                is ApiResult.Success -> mapTasksToUi(tasksResult.value.data as List<TaskSummaryDto>)
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = tasksResult.message, isLoading = false)
                    return@launch
                }
            }

            val goals = when (goalsResult) {
                is ApiResult.Success -> mapGoalsToUi(goalsResult.value.data as List<ActiveGoalDto>)
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = goalsResult.message, isLoading = false)
                    return@launch
                }
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                taskLists = taskLists,
                goals = goals,
                error = null,
            )
        }
    }

    fun setTaskCompletion(taskId: String, done: Boolean) {
        viewModelScope.launch {
            when (repository.updateTask(taskId, TaskUpdateRequestDto(done = done))) {
                is ApiResult.Success -> refresh(_uiState.value.selectedDate)
                is ApiResult.Error -> {
                    // no-op; let refresh keep current state
                }
            }
        }
    }

    private fun mapTasksToUi(tasks: List<TaskSummaryDto>): List<TaskListUiModel> {
        val grouped = tasks.groupBy { it.date }
        return grouped.entries
            .sortedBy { it.key }
            .map { (dateString, items) ->
                val taskItems = items.map { dto ->
                    TaskUiModel(
                        id = dto.id,
                        title = dto.title,
                        startTimeLabel = dto.date,
                        endTimeLabel = "",
                        isCompleted = dto.done ?: (dto.status == "done"),
                    )
                }
                TaskListUiModel(
                    id = dateString,
                    dateLabel = dateString,
                    tasks = taskItems,
                )
            }
    }

    private fun mapGoalsToUi(goals: List<ActiveGoalDto>): List<GoalUiModel> =
        goals.sortedBy { it.priority }.mapIndexed { index, dto ->
            val total = dto.totalTaskHours.takeIf { it > 0 } ?: 1.0
            val progress = (dto.doneTaskHours / total).toFloat().coerceIn(0f, 1f)
            GoalUiModel(
                id = dto.id,
                name = dto.title,
                progress = progress,
                priority = dto.priority.takeIf { it > 0 } ?: (index + 1),
                accentColor = dto.color,
            )
        }
}
