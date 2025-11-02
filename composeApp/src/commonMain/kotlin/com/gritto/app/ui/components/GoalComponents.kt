package com.gritto.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import com.gritto.app.ui.model.GoalUiModel
import kotlin.math.roundToInt
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GoalCard(
    goal: GoalUiModel,
    modifier: Modifier = Modifier,
    onClick: (GoalUiModel) -> Unit,
    progressLabel: String = "${(goal.progress * 100).roundToInt()}%",
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        onClick = { onClick(goal) },
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            GoalColorBadge(color = goal.accentColor.toComposeColor())
            Spacer(modifier = Modifier.size(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = goal.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                LinearProgressIndicator(
                    progress = { goal.progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(percent = 50)),
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = progressLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
//            PriorityPill(priority = goal.priority)
        }
    }
}

@Stable
class GoalListState internal constructor(
    private val goals: SnapshotStateList<GoalUiModel>,
    internal var onReordered: (List<GoalUiModel>) -> Unit,
) {
    val items: List<GoalUiModel> get() = goals

    fun move(from: Int, to: Int) {
        if (from !in goals.indices || to !in goals.indices || from == to) {
            return
        }
        val updated = goals.toMutableList()
        val moving = updated.removeAt(from)
        updated.add(to, moving)
        goals.clear()
        goals.addAll(updated.reassignPriorities())
        onReordered(goals.toList())
    }

    fun indexOf(goalId: String): Int = goals.indexOfFirst { it.id == goalId }

    internal fun replaceAll(newGoals: List<GoalUiModel>) {
        goals.clear()
        goals.addAll(newGoals)
    }
}

@Composable
fun rememberGoalListState(
    goals: List<GoalUiModel>,
    onReordered: (List<GoalUiModel>) -> Unit = {},
): GoalListState {
    val state = remember {
        GoalListState(
            goals = goals.reassignPriorities().toMutableStateList(),
            onReordered = onReordered,
        )
    }
    LaunchedEffect(goals) {
        state.replaceAll(goals.reassignPriorities())
    }
    LaunchedEffect(onReordered) {
        state.onReordered = onReordered
    }
    return state
}

@Composable
fun GoalList(
    state: GoalListState,
    modifier: Modifier = Modifier,
    onGoalClick: (GoalUiModel) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    var draggingGoalId by remember { mutableStateOf<String?>(null) }
    var itemOffset by remember { mutableFloatStateOf(0f) }
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            items = state.items,
            key = { it.id },
        ) { goal ->
            val isDragging = draggingGoalId == goal.id
            GoalCard(
                goal = goal,
                modifier = Modifier
                    .fillMaxWidth()
                    .run {
                        if (isDragging) {
                            translationOverlay(itemOffset)
                        } else {
                            this
                        }
                    }
                    .pointerInput(goal.id) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                draggingGoalId = goal.id
                            },
                            onDragCancel = {
                                draggingGoalId = null
                                itemOffset = 0f
                            },
                            onDragEnd = {
                                draggingGoalId = null
                                itemOffset = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                itemOffset += dragAmount.y
                                val currentIndex = state.indexOf(goal.id)
                                val proposedIndex = findTargetIndex(
                                    state = state,
                                    lazyListState = lazyListState,
                                    currentGoalId = goal.id,
                                    dragOffset = itemOffset,
                                )
                                if (proposedIndex != null && proposedIndex != currentIndex) {
                                    state.move(currentIndex, proposedIndex)
                                    itemOffset = 0f
                                }
                            },
                        )
                    },
                onClick = onGoalClick,
                progressLabel = "${(goal.progress * 100).roundToInt()}%",
            )
        }
    }
}

private fun findTargetIndex(
    state: GoalListState,
    lazyListState: LazyListState,
    currentGoalId: String,
    dragOffset: Float,
): Int? {
    if (dragOffset == 0f) return null
    val visibleItems = lazyListState.layoutInfo.visibleItemsInfo
    val currentInfo = visibleItems.fastFirstOrNull { it.key == currentGoalId } ?: return null
    val currentIndex = state.indexOf(currentGoalId)
    return if (dragOffset > 0f) {
        val currentBottom = currentInfo.offset + currentInfo.size + dragOffset
        visibleItems
            .filter { it.index > currentIndex }
            .firstOrNull { currentBottom > it.offset + it.size / 2f }
            ?.index
    } else {
        val currentTop = currentInfo.offset + dragOffset
        visibleItems
            .filter { it.index < currentIndex }
            .lastOrNull { currentTop < it.offset + it.size / 2f }
            ?.index
    }
}

private fun Modifier.translationOverlay(offset: Float): Modifier = this.then(
    Modifier
        .padding(horizontal = 2.dp)
        .graphicsLayer {
            translationY = offset
            shadowElevation = 8.dp.toPx()
        },
)

@Composable
private fun GoalColorBadge(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 10.dp, height = 48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(color),
    )
}

//@Composable
//private fun PriorityPill(priority: Int, modifier: Modifier = Modifier) {
//    Surface(
//        modifier = modifier,
//        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
//        shape = RoundedCornerShape(999.dp),
//    ) {
//        Text(
//            text = "P$priority",
//            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
//            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
//            color = MaterialTheme.colorScheme.primary,
//        )
//    }
//}

private fun List<GoalUiModel>.reassignPriorities(): List<GoalUiModel> =
    mapIndexed { index, item ->
        if (item.priority == index + 1) {
            item
        } else {
            item.copy(priority = index + 1)
        }
    }

private fun Long.toComposeColor(): Color = Color(this.toInt())

@Preview
@Composable
private fun GoalCardPreview() {
    GoalCard(
        goal = GoalUiModel(
            id = "1",
            name = "Launch MVP",
            progress = 0.45f,
            priority = 1,
            accentColor = 0xFF7C4DFF,
        ),
        onClick = {},
    )
}

@Preview
@Composable
private fun GoalListPreview() {
    val goals = listOf(
        GoalUiModel(id = "1", name = "Launch MVP", progress = 0.45f, priority = 1, accentColor = 0xFF7C4DFF),
        GoalUiModel(id = "2", name = "Improve onboarding", progress = 0.8f, priority = 2, accentColor = 0xFF26A69A),
        GoalUiModel(id = "3", name = "Boost retention", progress = 0.2f, priority = 3, accentColor = 0xFFFFA000),
    )
    val state = rememberGoalListState(goals)
    GoalList(
        state = state,
        onGoalClick = {},
    )
}
