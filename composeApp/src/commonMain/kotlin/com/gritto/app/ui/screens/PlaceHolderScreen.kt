package com.gritto.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import moe.tlaster.precompose.navigation.Navigator


@Composable fun HomeScreen(navigator: Navigator) = PlaceholderScreen("Home")
@Composable fun ChatScreen(navigator: Navigator) = PlaceholderScreen("Chat")
@Composable fun ProfileScreen(navigator: Navigator) = PlaceholderScreen("Profile")
@Composable fun ProfileEditScreen(navigator: Navigator) = PlaceholderScreen("Profile")
@Composable fun GoalPage(navigator: Navigator, goalId: String?) = PlaceholderScreen("Goal $goalId")
@Composable fun GoalEditPage(navigator: Navigator, goalId: String?) = PlaceholderScreen("Goal Edit $goalId")
@Composable fun MilestonePage(navigator: Navigator, milestoneId: String?) = PlaceholderScreen("Milestone $milestoneId")
@Composable fun MilestoneEditPage(navigator: Navigator, milestoneId: String?) = PlaceholderScreen("Milestone Edit $milestoneId")
@Composable fun TaskPage(navigator: Navigator, taskId: String?) = PlaceholderScreen("Task $taskId")
@Composable fun TaskEditPage(navigator: Navigator, taskId: String?) = PlaceholderScreen("Task Edit $taskId")
@Composable fun GoalTreePage(navigator: Navigator, goalId: String?) = PlaceholderScreen("Goal Tree $goalId")
@Composable fun GoalTreePreviewPage(navigator: Navigator) = PlaceholderScreen("Goal Tree Preview")

@Composable
private fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$name Screen", style = MaterialTheme.typography.headlineSmall)
    }
}