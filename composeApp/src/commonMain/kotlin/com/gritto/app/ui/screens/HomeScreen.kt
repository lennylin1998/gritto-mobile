package com.gritto.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.PaddingValues
import com.gritto.app.model.DailyTask
import com.gritto.app.model.GoalProgress
import com.gritto.app.ui.components.TaskItem
import androidx.compose.ui.graphics.Color

@Composable
fun HomeScreen(
    tasks: List<DailyTask>,
    goals: List<GoalProgress>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = contentPadding.calculateTopPadding() + 16.dp,
            bottom = contentPadding.calculateBottomPadding() + 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Today's Tasks",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
        itemsIndexed(tasks) { index, task ->
            TaskCard(
                task = task,
                indicatorColor = taskAccentColors[index % taskAccentColors.size]
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Goal Progress",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
        items(goals) { goal ->
            GoalCard(goal = goal)
        }
    }
}

@Composable
private fun TaskCard(
    task: DailyTask,
    indicatorColor: Color
) {
    TaskItem(
        title = task.title,
        description = task.description,
        scheduleLabel = task.scheduleLabel,
        indicatorColor = indicatorColor,
        checked = task.completed
    )
}

@Composable
private fun GoalCard(goal: GoalProgress) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = goal.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = goal.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            LinearProgressIndicator(
                progress = { goal.completion },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "${(goal.completion * 100).toInt()}% complete",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private val taskAccentColors = listOf(
    Color(0xFF4A6BFF),
    Color(0xFF61D992),
    Color(0xFF9F74FF),
    Color(0xFFFF8A65)
)
