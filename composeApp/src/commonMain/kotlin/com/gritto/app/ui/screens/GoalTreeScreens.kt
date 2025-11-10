package com.gritto.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.gritto.app.model.GoalTreeNode
import com.gritto.app.model.GoalTreeNodeType
import com.gritto.app.model.SampleData
import com.gritto.app.navigation.GrittoNavRoutes
import com.gritto.app.ui.components.MissingEntityMessage
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun GoalTreeScreen(
    goalId: String?,
    navigator: Navigator,
) {
    val root = remember(goalId) { SampleData.goalTreeByGoal[goalId] }

    if (root == null) {
        MissingEntityScaffold(
            title = "Goal Tree",
            onBack = navigator::goBack,
            message = "We couldn't find this goal tree.",
        )
        return
    }

    GoalTreeContent(
        title = root.title,
        node = root,
        isPreview = false,
        onBack = navigator::goBack,
        onAddGoal = { navigator.navigate(GrittoNavRoutes.goalEdit(root.id)) },
        onGoalTapped = { navigator.navigate(GrittoNavRoutes.goal(it)) },
        onGoalPlusTapped = { navigator.navigate(GrittoNavRoutes.milestoneEdit("milestone-1")) },
        onMilestoneTapped = { navigator.navigate(GrittoNavRoutes.milestone(it)) },
        onMilestonePlusTapped = { navigator.navigate(GrittoNavRoutes.taskEdit("task-1")) },
        onTaskTapped = { navigator.navigate(GrittoNavRoutes.task(it)) },
    )
}

@Composable
fun GoalTreePreviewScreen(
    goalPreviewId: String?,
    navigator: Navigator,
) {
    if (goalPreviewId.isNullOrBlank()) {
        MissingEntityScaffold(
            title = "Goal Preview",
            onBack = navigator::goBack,
            message = "We couldn’t find that goal preview.",
        )
        return
    }
    val preview = remember(goalPreviewId) { SampleData.goalTreePreview }
    GoalTreeContent(
        title = "Goal Preview",
        node = preview,
        isPreview = true,
        onBack = navigator::goBack,
        onAddGoal = {},
        onGoalTapped = {},
        onGoalPlusTapped = {},
        onMilestoneTapped = {},
        onMilestonePlusTapped = {},
        onTaskTapped = {},
    )
}

@Composable
private fun GoalTreeContent(
    title: String,
    node: GoalTreeNode,
    isPreview: Boolean,
    onBack: () -> Unit,
    onAddGoal: () -> Unit,
    onGoalTapped: (String) -> Unit,
    onGoalPlusTapped: (String) -> Unit,
    onMilestoneTapped: (String) -> Unit,
    onMilestonePlusTapped: (String) -> Unit,
    onTaskTapped: (String) -> Unit,
) {
    val expanded = remember { mutableStateMapOf<String, Boolean>() }

    val expandedSnapshot = expanded.toMap()
    val items = remember(node, expandedSnapshot) {
        buildList<GoalTreeRenderItem> {
            collectNode(node, level = 0, expanded = expandedSnapshot)
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        val icon = if (isPreview) Icons.Filled.Close else Icons.Filled.ArrowBack
                        Icon(icon, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isPreview) {
                        IconButton(onClick = onAddGoal) {
                            Icon(Icons.Filled.Add, contentDescription = "Add goal")
                        }
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            items.forEach { item ->
                when (item) {
                    is GoalTreeRenderItem.Header -> stickyHeader(key = item.node.id) {
                        GoalTreeRow(
                            node = item.node,
                            level = item.level,
                            expanded = expandedSnapshot[item.node.id] ?: true,
                            onExpandToggle = {
                                if (item.node.children.isNotEmpty()) {
                                    expanded[item.node.id] = !(expanded[item.node.id] ?: true)
                                }
                            },
                            onTitleClick = {
                                when (item.node.type) {
                                    GoalTreeNodeType.Goal -> onGoalTapped(item.node.id)
                                    GoalTreeNodeType.Milestone -> onMilestoneTapped(item.node.id)
                                    GoalTreeNodeType.Task -> onTaskTapped(item.node.id)
                                }
                            },
                            onAddClick = {
                                when (item.node.type) {
                                    GoalTreeNodeType.Goal -> onGoalPlusTapped(item.node.id)
                                    GoalTreeNodeType.Milestone -> onMilestonePlusTapped(item.node.id)
                                    GoalTreeNodeType.Task -> onTaskTapped(item.node.id)
                                }
                            },
                            showAdd = !isPreview && item.node.type != GoalTreeNodeType.Task,
                        )
                    }
                    is GoalTreeRenderItem.Item -> item(item.node.id) {
                        GoalTreeRow(
                            node = item.node,
                            level = item.level,
                            expanded = false,
                            onExpandToggle = {},
                            onTitleClick = { onTaskTapped(item.node.id) },
                            onAddClick = { onTaskTapped(item.node.id) },
                            showAdd = false,
                        )
                    }
                }
            }
        }
    }
}

private sealed class GoalTreeRenderItem {
    data class Header(val node: GoalTreeNode, val level: Int) : GoalTreeRenderItem()
    data class Item(val node: GoalTreeNode, val level: Int) : GoalTreeRenderItem()
}

private fun MutableList<GoalTreeRenderItem>.collectNode(
    node: GoalTreeNode,
    level: Int,
    expanded: Map<String, Boolean>,
) {
    if (node.type == GoalTreeNodeType.Task) {
        add(GoalTreeRenderItem.Item(node, level))
    } else {
        add(GoalTreeRenderItem.Header(node, level))
    }
    if (node.children.isNotEmpty() && (expanded[node.id] ?: true)) {
        node.children.forEach { child -> collectNode(child, level + 1, expanded) }
    }
}

@Composable
private fun GoalTreeRow(
    node: GoalTreeNode,
    level: Int,
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    onTitleClick: () -> Unit,
    onAddClick: () -> Unit,
    showAdd: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width((level * 16).dp))
        if (node.children.isNotEmpty()) {
            IconButton(onClick = onExpandToggle) {
                Icon(
                    imageVector = if (expanded) Icons.Rounded.ExpandMore else Icons.Rounded.ChevronRight,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                )
            }
        } else {
            Spacer(modifier = Modifier.width(40.dp))
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(MaterialTheme.shapes.medium)
                .clickable(onClick = onTitleClick)
                .padding(vertical = 8.dp),
        ) {
            Text(
                text = node.title,
                style = MaterialTheme.typography.titleMedium,
            )
            node.subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (showAdd) {
            IconButton(onClick = onAddClick) {
                Icon(Icons.Filled.Add, contentDescription = "Add child")
            }
        }
    }
}

@Composable
private fun MissingEntityScaffold(title: String, onBack: () -> Unit, message: String) {
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        MissingEntityMessage(
            label = message,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
        )
    }
}
