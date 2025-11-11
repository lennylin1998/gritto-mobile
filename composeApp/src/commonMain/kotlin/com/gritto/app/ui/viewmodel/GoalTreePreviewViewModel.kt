package com.gritto.app.ui.viewmodel

import com.gritto.app.data.network.ApiResult
import com.gritto.app.data.remote.model.GoalPreviewDataDto
import com.gritto.app.data.remote.model.GoalPreviewDto
import com.gritto.app.data.remote.model.GoalPreviewPayloadDto
import com.gritto.app.data.remote.model.MilestonePreviewDto
import com.gritto.app.data.remote.model.TaskPreviewDto
import com.gritto.app.data.repository.GrittoRepository
import com.gritto.app.model.GoalTreeNode
import com.gritto.app.model.GoalTreeNodeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

data class GoalTreePreviewUiState(
    val isLoading: Boolean = true,
    val node: GoalTreeNode? = null,
    val error: String? = null,
)

class GoalTreePreviewViewModel(
    private val repository: GrittoRepository,
    private val goalPreviewId: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalTreePreviewUiState())
    val uiState: StateFlow<GoalTreePreviewUiState> = _uiState.asStateFlow()

    init {
        loadPreview()
    }

    fun retry() {
        loadPreview()
    }

    private fun loadPreview() {
        viewModelScope.launch {
            _uiState.value = GoalTreePreviewUiState(isLoading = true)
            when (val result = repository.fetchGoalPreview(goalPreviewId)) {
                is ApiResult.Success -> {
                    val node = result.value.data.toGoalTreeNode()
                    if (node != null) {
                        _uiState.value = GoalTreePreviewUiState(
                            isLoading = false,
                            node = node,
                        )
                    } else {
                        _uiState.value = GoalTreePreviewUiState(
                            isLoading = false,
                            error = "Preview is missing goal details.",
                        )
                    }
                }

                is ApiResult.Error -> {
                    _uiState.value = GoalTreePreviewUiState(
                        isLoading = false,
                        error = result.message,
                    )
                }
            }
        }
    }
}

private fun GoalPreviewPayloadDto.toGoalTreeNode(): GoalTreeNode? =
    data?.toGoalTreeNode()

private fun GoalPreviewDataDto.toGoalTreeNode(): GoalTreeNode? =
    goal?.toGoalTreeNode()

private fun GoalPreviewDto.toGoalTreeNode(): GoalTreeNode {
    val subtitle = buildList {
        description?.takeIf { it.isNotBlank() }?.let { add(it) }
        hoursPerWeek?.let { add("$it h/week") }
    }.takeIf { it.isNotEmpty() }?.joinToString(" • ")

    val milestoneNodes = milestones
        .takeIf { it.isNotEmpty() }
        ?.mapIndexed { index, milestone ->
            milestone.toGoalTreeNode(index)
        }
        ?: emptyList()

    return GoalTreeNode(
        id = "preview-root",
        title = title,
        subtitle = subtitle,
        type = GoalTreeNodeType.Goal,
        children = milestoneNodes,
    )
}

private fun MilestonePreviewDto.toGoalTreeNode(index: Int): GoalTreeNode {
    val milestoneSubtitle = description?.takeIf { it.isNotBlank() }
    val taskNodes = tasks.mapIndexed { taskIndex, task ->
        task.toGoalTreeNode(index, taskIndex)
    }
    return GoalTreeNode(
        id = "preview-milestone-$index-${title.hashCode()}",
        title = title,
        subtitle = milestoneSubtitle,
        type = GoalTreeNodeType.Milestone,
        children = taskNodes,
    )
}

private fun TaskPreviewDto.toGoalTreeNode(milestoneIndex: Int, taskIndex: Int): GoalTreeNode {
    val subtitleParts = buildList {
        date?.takeIf { it.isNotBlank() }?.let { add(it) }
        add("${estimatedHours}h")
        description?.takeIf { it.isNotBlank() }?.let { add(it) }
    }
    val subtitle = subtitleParts.takeIf { it.isNotEmpty() }?.joinToString(" • ")

    return GoalTreeNode(
        id = "preview-task-$milestoneIndex-$taskIndex-${title.hashCode()}",
        title = title,
        subtitle = subtitle,
        type = GoalTreeNodeType.Task,
    )
}
