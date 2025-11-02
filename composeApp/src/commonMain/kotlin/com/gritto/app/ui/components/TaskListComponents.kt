package com.gritto.app.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gritto.app.ui.model.TaskListUiModel
import com.gritto.app.ui.model.TaskUiModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskListsCarousel(
    taskLists: List<TaskListUiModel>,
    modifier: Modifier = Modifier,
    onTaskChecked: (TaskListUiModel, TaskUiModel) -> Unit,
    onTaskClick: (TaskListUiModel, TaskUiModel) -> Unit,
) {
    if (taskLists.isEmpty()) {
        EmptyTaskListCard(modifier = modifier)
        return
    }

    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    LazyRow(
        modifier = modifier,
        state = listState,
        flingBehavior = flingBehavior,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        items(
            items = taskLists,
            key = { it.id },
        ) { taskList ->
            TaskListCard(
                list = taskList,
                onTaskChecked = { onTaskChecked(taskList, it) },
                onTaskClick = { onTaskClick(taskList, it) },
            )
        }
    }
}

@Composable
private fun EmptyTaskListCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = "No tasks scheduled",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "You’re all caught up for now. Add a task to populate this area.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TaskListCard(
    list: TaskListUiModel,
    onTaskChecked: (TaskUiModel) -> Unit,
    onTaskClick: (TaskUiModel) -> Unit,
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(320.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = list.dateLabel,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (list.tasks.isNotEmpty()) {
                    Text(
                        text = "${list.tasks.size} tasks",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                list.tasks.forEach { task ->
                    TaskListRow(
                        task = task,
                        onChecked = { onTaskChecked(task) },
                        onClick = { onTaskClick(task) },
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskListRow(
    task: TaskUiModel,
    onChecked: () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onChecked() },
            modifier = Modifier.size(22.dp),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onClick)
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${task.startTimeLabel} - ${task.endTimeLabel}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        DurationIndicator()
    }
}

@Composable
private fun DurationIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(6.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    ),
                ),
            ),
    )
}

@Preview(showBackground = true, heightDp = 320, widthDp = 280)
@Composable
private fun TaskListCardPreview() {
    val tasks = listOf(
        TaskUiModel(id = "1", title = "Morning stand-up", startTimeLabel = "09:00", endTimeLabel = "09:15", isCompleted = false),
        TaskUiModel(id = "2", title = "Design sync", startTimeLabel = "10:30", endTimeLabel = "11:15", isCompleted = false),
    )
    Box(modifier = Modifier.height(320.dp)) {  // ← Constrain the preview
        TaskListCard(
            list = TaskListUiModel(id = "day-1", dateLabel = "Mon, Aug 19", tasks = tasks),
            onTaskChecked = {},
            onTaskClick = {},
        )
    }
}
