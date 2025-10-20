package com.gritto.app.ui

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.gritto.app.model.ChatMessage
import com.gritto.app.model.SampleData
import com.gritto.app.model.DailyTask
import com.gritto.app.model.GoalProgress
import com.gritto.app.model.ScheduledTask
import com.gritto.app.ui.components.GrittoBottomBar
import com.gritto.app.ui.screens.GoalsScreen
import com.gritto.app.ui.screens.HomeScreen
import com.gritto.app.ui.screens.OnboardingScreen
import com.gritto.app.ui.screens.ProfileScreen
import com.gritto.app.ui.screens.ReflectionScreen
import com.gritto.app.ui.screens.TaskSchedulesScreen

enum class MainDestination(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Home("Home", Icons.Filled.Home),
    Goals("Goals", Icons.Filled.Flag),
    Reflection("Reflection", Icons.Filled.Chat),
    Schedules("Task Schedules", Icons.Filled.Schedule),
    Profile("Profile", Icons.Filled.Person)
}

@Composable
fun GrittoApp(modifier: Modifier = Modifier) {
    var isSignedIn by remember { mutableStateOf(false) }
    var selectedDestination by remember { mutableStateOf(MainDestination.Home) }

    val todayTasks = remember { SampleData.todayTasks }
    val goals = remember { SampleData.goals }
    val scheduledTasks = remember { SampleData.scheduledTasks }
    val chatHistory = remember {
        mutableStateListOf<ChatMessage>().apply { addAll(SampleData.initialChat) }
    }
    var messageCounter by remember { mutableStateOf(chatHistory.size) }

    if (!isSignedIn) {
        OnboardingScreen(
            onSignIn = {
                isSignedIn = true
                selectedDestination = MainDestination.Home
            },
            modifier = modifier
        )
        return
    }

    Scaffold(
        modifier = modifier.systemBarsPadding(),
        bottomBar = {
            GrittoBottomBar(
                selected = selectedDestination,
                onSelected = { selectedDestination = it }
            )
        }
    ) { padding ->
        when (selectedDestination) {
            MainDestination.Home -> HomeScreen(
                tasks = todayTasks,
                goals = goals,
                contentPadding = padding
            )
            MainDestination.Goals -> GoalsScreen(
                goals = goals,
                contentPadding = padding
            )
            MainDestination.Reflection -> ReflectionScreen(
                messages = chatHistory,
                onSendMessage = { text ->
                    appendReflectionMessage(
                        chatHistory = chatHistory,
                        nextId = { ++messageCounter },
                        userMessage = text
                    )
                },
                contentPadding = padding
            )
            MainDestination.Schedules -> TaskSchedulesScreen(
                tasks = scheduledTasks,
                contentPadding = padding
            )
            MainDestination.Profile -> ProfileScreen(
                onSignOut = {
                    isSignedIn = false
                    selectedDestination = MainDestination.Home
                },
                contentPadding = padding
            )
        }
    }
}

private fun appendReflectionMessage(
    chatHistory: SnapshotStateList<ChatMessage>,
    nextId: () -> Int,
    userMessage: String
) {
    if (userMessage.isBlank()) return
    val trimmedMessage = userMessage.trim()
    val userId = "chat-${nextId()}"
    chatHistory.add(ChatMessage.User(id = userId, text = trimmedMessage))

    val agentResponse = buildAgentReply(trimmedMessage)
    val agentId = "chat-${nextId()}"
    chatHistory.add(ChatMessage.Agent(id = agentId, text = agentResponse))
}

private fun buildAgentReply(message: String): String {
    val encouragement = when {
        message.contains("done", ignoreCase = true) -> "Nice win. Capture what helped you succeed so we can repeat it."
        message.contains("stuck", ignoreCase = true) -> "Thanks for being honest. Let's pick one tiny step you can ship today."
        message.length > 120 -> "That's a thorough reflection. Summarize the single action you'll take next."
        else -> "Got it. What single next step keeps you aligned with your goal?"
    }
    return "$encouragement"
}
