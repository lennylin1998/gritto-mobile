package com.gritto.app.ui.viewmodel

import com.gritto.app.data.network.ApiResult
import com.gritto.app.data.remote.model.GoalDetailDto
import com.gritto.app.data.remote.model.MilestoneDetailDto
import com.gritto.app.data.remote.model.TaskDetailDto
import com.gritto.app.data.repository.GrittoRepository
import com.gritto.app.model.GoalTreeNode
import com.gritto.app.model.GoalTreeNodeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

data class GoalTreeUiState(
    val isLoading: Boolean = true,
    val node: GoalTreeNode? = null,
    val error: String? = null,
)

class GoalTreeViewModel(
    private val repository: GrittoRepository,
    private val goalId: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalTreeUiState())
    val uiState: StateFlow<GoalTreeUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun retry() {
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.value = GoalTreeUiState(isLoading = true)
            when (val result = buildGoalTree()) {
                is ApiResult.Success -> {
                    _uiState.value = GoalTreeUiState(
                        isLoading = false,
                        node = result.value,
                    )
                }

                is ApiResult.Error -> {
                    _uiState.value = GoalTreeUiState(
                        isLoading = false,
                        error = result.message,
                    )
                }
            }
        }
    }

    private suspend fun buildGoalTree(): ApiResult<GoalTreeNode> {
        val goalDetail = when (val goalResult = repository.fetchGoalDetail(goalId)) {
            is ApiResult.Success -> goalResult.value.data
            is ApiResult.Error -> return goalResult
        }
        val milestoneSummaries = when (val milestoneResult = repository.fetchGoalMilestones(goalId)) {
            is ApiResult.Success -> milestoneResult.value.data
            is ApiResult.Error -> return milestoneResult
        }

        val milestoneNodes = mutableListOf<GoalTreeNode>()
        for (summary in milestoneSummaries) {
            when (val milestoneNode = buildMilestoneNode(summary.id)) {
                is ApiResult.Success -> milestoneNodes += milestoneNode.value
                is ApiResult.Error -> return milestoneNode
            }
        }

        return ApiResult.Success(goalDetail.toGoalTreeNode(milestoneNodes))
    }

    private suspend fun buildMilestoneNode(milestoneId: String): ApiResult<GoalTreeNode> {
        val milestoneDetail = when (val result = repository.fetchMilestoneDetail(milestoneId)) {
            is ApiResult.Success -> result.value.data
            is ApiResult.Error -> return result
        }

        val taskNodes = mutableListOf<GoalTreeNode>()
        for (taskId in milestoneDetail.tasks) {
            when (val taskResult = repository.fetchTaskDetail(taskId)) {
                is ApiResult.Success -> taskNodes += taskResult.value.data.toGoalTreeNode()
                is ApiResult.Error -> return taskResult
            }
        }

        return ApiResult.Success(milestoneDetail.toGoalTreeNode(taskNodes))
    }
}

private fun GoalDetailDto.toGoalTreeNode(children: List<GoalTreeNode>): GoalTreeNode {
    val subtitle = buildList {
        description?.takeIf { it.isNotBlank() }?.let { add(it) }
        context?.takeIf { it.isNotBlank() }?.let { add(it) }
        startDate?.takeIf { it.isNotBlank() }?.let { add("Start: $it") }
    }.takeIf { it.isNotEmpty() }?.joinToString(" • ")

    return GoalTreeNode(
        id = id,
        title = title,
        subtitle = subtitle,
        type = GoalTreeNodeType.Goal,
        children = children,
    )
}

private fun MilestoneDetailDto.toGoalTreeNode(tasks: List<GoalTreeNode>): GoalTreeNode {
    val subtitle = buildList {
        status.takeIf { it.isNotBlank() }?.let { add(it.replaceFirstChar { c -> c.uppercase() }) }
        description?.takeIf { it.isNotBlank() }?.let { add(it) }
    }.takeIf { it.isNotEmpty() }?.joinToString(" • ")

    return GoalTreeNode(
        id = id,
        title = title,
        subtitle = subtitle,
        type = GoalTreeNodeType.Milestone,
        children = tasks,
    )
}

private fun TaskDetailDto.toGoalTreeNode(): GoalTreeNode {
    val subtitle = buildList {
        date?.takeIf { it.isNotBlank() }?.let { add(it) }
        add("${estimatedHours}h")
        add(if (done) "Done" else "Pending")
    }.joinToString(" • ")

    return GoalTreeNode(
        id = id,
        title = title,
        subtitle = subtitle,
        type = GoalTreeNodeType.Task,
    )
}
