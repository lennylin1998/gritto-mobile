package com.gritto.app.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class GoalUiModel(
    val id: String,
    val name: String,
    val progress: Float,
    val priority: Int,
    val accentColor: Long,
    val description: String? = null,
)

@Immutable
data class TaskUiModel(
    val id: String,
    val title: String,
    val startTimeLabel: String,
    val endTimeLabel: String,
    val isCompleted: Boolean,
)

@Immutable
data class TaskListUiModel(
    val id: String,
    val dateLabel: String,
    val tasks: List<TaskUiModel>,
)
