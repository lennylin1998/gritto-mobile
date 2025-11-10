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
import com.gritto.app.ui.viewmodel.ChatViewModel
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

            MainNavDestination.Chat -> ChatRoute(
                state = state,
                navigator = navigator,
                padding = padding,
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
private fun ChatRoute(
    state: GrittoAppState,
    navigator: Navigator,
    padding: PaddingValues,
) {
    val viewModel = viewModel(modelClass = ChatViewModel::class) {
        ChatViewModel(
            repository = state.repository,
            userIdProvider = { state.userId },
        )
    }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(state.sessionToken, state.userId) {
        if (state.sessionToken != null) {
            viewModel.ensureSession()
        }
    }

    ChatScreen(
        uiState = uiState,
        onSendMessage = { message ->
            viewModel.sendMessage(message)
        },
        onRetry = { viewModel.retry() },
        onBack = { state.selectedDestination = MainNavDestination.Home },
        onShowGoalPreview = {
            uiState.goalPreviewId?.let { previewId ->
                navigator.navigate(GrittoNavRoutes.goalTreePreview(previewId))
            }
        },
        contentPadding = padding,
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
