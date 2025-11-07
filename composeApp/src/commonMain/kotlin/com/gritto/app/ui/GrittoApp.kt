package com.gritto.app.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.gritto.app.model.ChatMessage
import com.gritto.app.navigation.GrittoNavRoutes
import com.gritto.app.platform.platformGoogleClientId
import com.gritto.app.platform.rememberGoogleAuthLauncher
import com.gritto.app.ui.components.GrittoNavBar
import com.gritto.app.ui.components.MainNavDestination
import com.gritto.app.ui.screens.ChatScreen
import com.gritto.app.ui.screens.HomeScreen
import com.gritto.app.ui.screens.OnboardingScreen
import com.gritto.app.ui.screens.ProfileScreen
import com.gritto.app.ui.viewmodel.HomeViewModel
import com.gritto.app.ui.viewmodel.OnboardingViewModel
import com.gritto.app.ui.viewmodel.ProfileViewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import kotlinx.coroutines.CancellationException

@Composable
fun GrittoApp(
    navigator: Navigator,
    state: GrittoAppState,
    modifier: Modifier = Modifier,
) {
    if (!state.isSignedIn) {
        OnboardingRoute(
            state = state,
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
            MainNavDestination.Home -> HomeRoute(
                state = state,
                navigator = navigator,
                padding = padding,
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

            MainNavDestination.Profile -> ProfileRoute(
                state = state,
                navigator = navigator,
                padding = padding,
            )
        }
    }
}

@Composable
private fun HomeRoute(
    state: GrittoAppState,
    navigator: Navigator,
    padding: PaddingValues,
) {
    val viewModel = viewModel(modelClass = HomeViewModel::class) {
        HomeViewModel(state.repository)
    }
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(state.sessionToken) {
        if (state.sessionToken != null) {
            viewModel.refresh()
        }
    }

    HomeScreen(
        taskLists = uiState.taskLists,
        goals = uiState.goals,
        contentPadding = padding,
        isLoading = uiState.isLoading,
        errorMessage = uiState.error,
        onRetry = { viewModel.refresh() },
        onTaskListsChange = { viewModel.onTaskListsChange(it) },
        onGoalReordered = { viewModel.onGoalsReordered(it) },
        onGoalClick = { goal ->
            navigator.navigate(GrittoNavRoutes.goalTree(goal.id))
        },
        onTaskClick = { task ->
            navigator.navigate(GrittoNavRoutes.task(task.id))
        },
        onTaskChecked = { task -> viewModel.setTaskCompletion(task.id, true) },
        onTaskCompletionUndo = { task -> viewModel.setTaskCompletion(task.id, false) },
    )
}

@Composable
private fun ProfileRoute(
    state: GrittoAppState,
    navigator: Navigator,
    padding: PaddingValues,
) {
    val viewModel = viewModel(modelClass = ProfileViewModel::class, keys = listOf("profile")) {
        ProfileViewModel(state.repository, onProfileLoaded = state::updateProfile)
    }
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(state.sessionToken) {
        if (state.sessionToken != null) {
            viewModel.refresh()
        }
    }

    ProfileScreen(
        uiState = uiState,
        contentPadding = padding,
        onRetry = { viewModel.refresh() },
        onEditHours = { navigator.navigate(GrittoNavRoutes.ProfileEdit) },
        onSignOut = { state.signOut() },
    )
}

@Composable
private fun OnboardingRoute(
    state: GrittoAppState,
    modifier: Modifier,
) {
    val viewModel = viewModel(modelClass = OnboardingViewModel::class) {
        OnboardingViewModel(
            repository = state.repository,
            onAuthSuccess = { auth ->
                state.updateAuth(auth)
            },
        )
    }
    val uiState by viewModel.uiState.collectAsState()
    var googleError by remember { mutableStateOf<String?>(null) }
    val googleLauncher = rememberGoogleAuthLauncher(
        clientId = platformGoogleClientId,
    ) { result ->
        result
            .onSuccess {
                googleError = null
                viewModel.signIn(it)
            }
            .onFailure { throwable ->
                googleError = if (throwable is CancellationException) {
                    null
                } else {
                    throwable.message ?: "Google Sign-In failed"
                }
            }
    }
    val isGoogleAvailable = googleLauncher != null

    LaunchedEffect(googleLauncher) {
        googleLauncher?.launchBottomSheet()
    }

    OnboardingScreen(
        googleSignInAvailable = isGoogleAvailable,
        onGoogleSignInClick = { googleLauncher?.launchSignInButton() },
        isLoading = uiState.isLoading,
        errorMessage = googleError ?: uiState.error,
        modifier = modifier,
    )
}

private fun appendChatMessage(
    state: GrittoAppState,
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
