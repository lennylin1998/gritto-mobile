package com.gritto.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gritto.app.theme.GrittoTheme
import com.gritto.app.ui.components.GoalList
import com.gritto.app.ui.components.TaskListsCarousel
import com.gritto.app.ui.model.GoalUiModel
import com.gritto.app.ui.model.TaskListUiModel
import com.gritto.app.ui.model.TaskUiModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen(
    taskLists: List<TaskListUiModel>,
    goals: List<GoalUiModel>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRetry: () -> Unit = {},
    onTaskClick: (TaskUiModel) -> Unit = {},
    onTaskChecked: (TaskUiModel) -> Unit = {},
    onTaskCompletionUndo: (TaskUiModel) -> Unit = {},
    onGoalClick: (GoalUiModel) -> Unit = {},
    onGoalReordered: (List<GoalUiModel>) -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun moveGoal(fromIndex: Int, toIndex: Int) {
        if (fromIndex !in goals.indices || toIndex !in goals.indices || fromIndex == toIndex) return
        val updated = goals.toMutableList()
        val moving = updated.removeAt(fromIndex)
        updated.add(toIndex, moving)
        onGoalReordered(updated)
    }

    fun handleTaskCompletion(task: TaskUiModel) {
        scope.launch {
            onTaskChecked(task)
            val result = snackbarHostState.showSnackbar(
                message = "Marked \"${task.title}\" complete",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short,
            )
            if (result == SnackbarResult.ActionPerformed) {
                onTaskCompletionUndo(task)
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "We couldn’t load your dashboard.",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Button(onClick = onRetry) {
                        Text("Retry")
                    }
                }
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = contentPadding.calculateTopPadding() + 16.dp,
                        bottom = contentPadding.calculateBottomPadding() + 16.dp,
                    ),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "Task List",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)  // ← Constrain TaskList height
                    ) {
                        TaskListsCarousel(
                            taskLists = taskLists,
                            modifier = Modifier.fillMaxWidth(),
                            onTaskChecked = { task -> handleTaskCompletion(task) },
                            onTaskClick = onTaskClick,
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = "Goals",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    ) {
                        if (goals.isEmpty()) {
                            GoalEmptyState(
                                modifier = Modifier.align(Alignment.TopStart),
                            )
                        } else {
                            GoalList(
                                goals = goals,
                                modifier = Modifier.fillMaxSize(),
                                onGoalClick = onGoalClick,
                                onGoalReorder = ::moveGoal,
                            )
                        }
                    }
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = contentPadding.calculateBottomPadding() + 24.dp),
        )
    }
}

@Composable
private fun GoalEmptyState(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardDefaults.elevatedShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "No goals yet",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Create your first goal and it will show up here for quick tracking.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    // --- Sample Tasks ---
    val sampleTasksDay1 = listOf(
        TaskUiModel(
            id = "t1",
            title = "Design Home Layout",
            startTimeLabel = "09:00",
            endTimeLabel = "11:00",
            isCompleted = false
        ),
        TaskUiModel(
            id = "t2",
            title = "Implement Navigation",
            startTimeLabel = "11:30",
            endTimeLabel = "13:00",
            isCompleted = true
        ),
        TaskUiModel(
            id = "t3",
            title = "Write API Layer",
            startTimeLabel = "14:00",
            endTimeLabel = "15:30",
            isCompleted = false
        )
    )

    val sampleTasksDay2 = listOf(
        TaskUiModel(
            id = "t4",
            title = "Refactor Goal Logic",
            startTimeLabel = "10:00",
            endTimeLabel = "12:00",
            isCompleted = false
        )
    )

    // --- Sample Task Lists (grouped by date) ---
    val sampleTaskLists = listOf(
        TaskListUiModel(
            id = "tl1",
            dateLabel = "Nov 2, 2025",
            tasks = sampleTasksDay1
        ),
        TaskListUiModel(
            id = "tl2",
            dateLabel = "Nov 3, 2025",
            tasks = sampleTasksDay2
        )
    )

    // --- Sample Goals ---
    val sampleGoals = listOf(
        GoalUiModel(
            id = "g1",
            name = "Build MVP",
            progress = 0.45f,
            priority = 1,
            accentColor = 0xFFB39DDB, // purple
            description = "Finish all core app features."
        ),
        GoalUiModel(
            id = "g2",
            name = "Launch on Cloud Run",
            progress = 0.8f,
            priority = 2,
            accentColor = 0xFF81C784, // green
            description = "Deploy backend service with Cloud Run."
        )
    )

    // --- Render ---
    GrittoTheme {
        HomeScreen(
            taskLists = sampleTaskLists,
            goals = sampleGoals,
            contentPadding = PaddingValues(),
            onTaskClick = {},
            onTaskChecked = {},
            onTaskCompletionUndo = {},
            onGoalClick = {},
            onGoalReordered = {},
        )
    }
}
