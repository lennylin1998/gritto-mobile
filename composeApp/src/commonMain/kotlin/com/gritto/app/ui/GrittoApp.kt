package com.gritto.app.ui

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gritto.app.model.ChatMessage
import com.gritto.app.navigation.GrittoNavRoutes
import com.gritto.app.ui.components.GrittoNavBar
import com.gritto.app.ui.components.MainNavDestination
import com.gritto.app.ui.screens.ChatScreen
import com.gritto.app.ui.screens.HomeScreen
import com.gritto.app.ui.screens.OnboardingScreen
import com.gritto.app.ui.screens.ProfileScreen
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun GrittoApp(
    navigator: Navigator,
    state: GrittoState,
    modifier: Modifier = Modifier,
) {
    if (!state.isSignedIn) {
        OnboardingScreen(
            onSignIn = {
                state.isSignedIn = true
                state.selectedDestination = MainNavDestination.Home
            },
            modifier = modifier,
        )
        return
    }

    Scaffold(
        modifier = modifier.systemBarsPadding(),
        bottomBar = {
            GrittoNavBar(
                selectedDestination = state.selectedDestination,
                onDestinationSelected = { state.selectedDestination = it },
            )
        },
    ) { padding ->
        when (state.selectedDestination) {
            MainNavDestination.Home -> HomeScreen(
                taskLists = state.homeTaskLists,
                goals = state.homeGoals,
                contentPadding = padding,
                onTaskListsChange = { state.homeTaskLists = it },
                onGoalReordered = { updated ->
                    state.homeGoals.apply {
                        clear()
                        addAll(updated)
                    }
                },
                onGoalClick = { goal ->
                    navigator.navigate(GrittoNavRoutes.goalTree(goal.id))
                },
                onTaskClick = { task ->
                    navigator.navigate(GrittoNavRoutes.task(task.id))
                },
            )

            MainNavDestination.Chat -> ChatScreen(
                messages = state.chatHistory,
                onSendMessage = { text ->
                    appendChatMessage(
                        state = state,
                        userMessage = text,
                    )
                },
                onBack = { state.selectedDestination = MainNavDestination.Home },
                onShowGoalPreview = { navigator.navigate(GrittoNavRoutes.GoalTreePreview) },
                contentPadding = padding,
            )

            MainNavDestination.Profile -> ProfileScreen(
                profile = state.profile,
                onEditHours = { navigator.navigate(GrittoNavRoutes.ProfileEdit) },
                onSignOut = {
                    state.isSignedIn = false
                    state.selectedDestination = MainNavDestination.Home
                },
                contentPadding = padding,
            )
        }
    }
}

private fun appendChatMessage(
    state: GrittoState,
    userMessage: String,
) {
    if (userMessage.isBlank()) return
    val trimmed = userMessage.trim()
    val userId = "chat-${++state.messageCounter}"
    state.chatHistory.add(ChatMessage.User(id = userId, text = trimmed))

    val agentReply = buildAgentReply(trimmed)
    val agentId = "chat-${++state.messageCounter}"
    state.chatHistory.add(ChatMessage.Agent(id = agentId, text = agentReply))
}

private fun buildAgentReply(message: String): String {
    val encouragement = when {
        message.contains("done", ignoreCase = true) -> "Nice win. Capture what helped you succeed so we can repeat it."
        message.contains("stuck", ignoreCase = true) -> "Thanks for being honest. Let's pick one tiny step you can ship today."
        message.length > 120 -> "That's a thorough reflection. Summarize the single action you'll take next."
        else -> "Got it. What single next step keeps you aligned with your goal?"
    }
    return encouragement
}
