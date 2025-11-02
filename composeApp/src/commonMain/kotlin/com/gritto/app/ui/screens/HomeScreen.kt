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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gritto.app.theme.GrittoTheme
import com.gritto.app.ui.components.GoalList
import com.gritto.app.ui.components.TaskListsCarousel
import com.gritto.app.ui.components.rememberGoalListState
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
    onTaskClick: (TaskUiModel) -> Unit = {},
    onTaskChecked: (TaskUiModel) -> Unit = {},
    onTaskCompletionUndo: (TaskUiModel) -> Unit = {},
    onTaskListsChange: (List<TaskListUiModel>) -> Unit = {},
    onGoalClick: (GoalUiModel) -> Unit = {},
    onGoalReordered: (List<GoalUiModel>) -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var currentTaskLists by remember { mutableStateOf(taskLists) }
    LaunchedEffect(taskLists) {
        currentTaskLists = taskLists
    }

    val goalListState = rememberGoalListState(
        goals = goals,
        onReordered = onGoalReordered,
    )

    fun commitTaskLists(updated: List<TaskListUiModel>) {
        currentTaskLists = updated
        onTaskListsChange(updated)
    }

    fun handleTaskCompletion(list: TaskListUiModel, task: TaskUiModel) {
        val removal = removeTaskFromLists(
            lists = currentTaskLists,
            listId = list.id,
            taskId = task.id,
        ) ?: return
        commitTaskLists(removal.updatedLists)
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "Marked \"${removal.task.title}\" complete",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short,
            )
            if (result == SnackbarResult.ActionPerformed) {
                val restored = restoreTaskToLists(
                    lists = currentTaskLists,
                    removal = removal,
                )
                commitTaskLists(restored)
                onTaskCompletionUndo(removal.task)
            } else {
                onTaskChecked(removal.task)
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
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
                        taskLists = currentTaskLists,
                        modifier = Modifier.fillMaxWidth(),
                        onTaskChecked = { list, task ->
                            handleTaskCompletion(list, task)
                        },
                        onTaskClick = { _, task ->
                            onTaskClick(task)
                        },
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
                    if (goalListState.items.isEmpty()) {
                        GoalEmptyState(
                            modifier = Modifier.align(Alignment.TopStart),
                        )
                    } else {
                        GoalList(
                            state = goalListState,
                            modifier = Modifier.fillMaxSize(),
                            onGoalClick = onGoalClick,
                        )
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

private data class TaskRemoval(
    val updatedLists: List<TaskListUiModel>,
    val task: TaskUiModel,
    val listId: String,
    val listIndex: Int,
    val taskIndex: Int,
    val originalList: TaskListUiModel,
)

private fun removeTaskFromLists(
    lists: List<TaskListUiModel>,
    listId: String,
    taskId: String,
): TaskRemoval? {
    val listIndex = lists.indexOfFirst { it.id == listId }
    if (listIndex == -1) return null
    val list = lists[listIndex]
    val taskIndex = list.tasks.indexOfFirst { it.id == taskId }
    if (taskIndex == -1) return null

    val task = list.tasks[taskIndex]
    val updatedTasks = list.tasks.toMutableList().apply { removeAt(taskIndex) }
    val updatedLists = lists.toMutableList().apply {
        if (updatedTasks.isEmpty()) {
            removeAt(listIndex)
        } else {
            this[listIndex] = list.copy(tasks = updatedTasks)
        }
    }
    return TaskRemoval(
        updatedLists = updatedLists,
        task = task,
        listId = listId,
        listIndex = listIndex,
        taskIndex = taskIndex,
        originalList = list,
    )
}

private fun restoreTaskToLists(
    lists: List<TaskListUiModel>,
    removal: TaskRemoval,
): List<TaskListUiModel> {
    val mutableLists = lists.toMutableList()
    val existingIndex = mutableLists.indexOfFirst { it.id == removal.listId }
    if (existingIndex == -1) {
        val tasks = removal.originalList.tasks.toMutableList()
        val insertIndex = removal.taskIndex.coerceIn(0, tasks.size)
        tasks.add(insertIndex, removal.task)
        val restoredList = removal.originalList.copy(tasks = tasks)
        val listInsertIndex = removal.listIndex.coerceIn(0, mutableLists.size)
        mutableLists.add(listInsertIndex, restoredList)
    } else {
        val existingList = mutableLists[existingIndex]
        val tasks = existingList.tasks.toMutableList()
        val insertIndex = removal.taskIndex.coerceIn(0, tasks.size)
        tasks.add(insertIndex, removal.task)
        mutableLists[existingIndex] = existingList.copy(tasks = tasks)
    }
    return mutableLists
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
            onTaskListsChange = {},
            onGoalClick = {},
            onGoalReordered = {},
        )
    }
}