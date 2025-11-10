package com.gritto.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.gritto.app.data.network.ApiResult
import com.gritto.app.data.remote.model.ProfileUpdateRequestDto
import com.gritto.app.data.remote.model.toProfileInfo
import com.gritto.app.model.SampleData
import com.gritto.app.navigation.GrittoNavRoutes
import com.gritto.app.theme.GrittoTheme
import com.gritto.app.ui.GrittoApp
import com.gritto.app.ui.rememberGrittoState
import com.gritto.app.ui.screens.GoalEditScreen
import com.gritto.app.ui.screens.GoalScreen
import com.gritto.app.ui.screens.GoalTreePreviewScreen
import com.gritto.app.ui.screens.GoalTreeScreen
import com.gritto.app.ui.screens.MilestoneEditScreen
import com.gritto.app.ui.screens.MilestoneScreen
import com.gritto.app.ui.screens.ProfileEditScreen
import com.gritto.app.ui.screens.TaskEditScreen
import com.gritto.app.ui.screens.TaskScreen
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import kotlinx.coroutines.launch

@Composable
fun App() {
    GrittoTheme {
        PreComposeApp {
            val navigator = rememberNavigator()
            val appState = rememberGrittoState()
            NavHost(
                navigator = navigator,
                initialRoute = GrittoNavRoutes.Main,
            ) {
                scene(route = GrittoNavRoutes.Main) {
                    GrittoApp(
                        navigator = navigator,
                        state = appState,
                    )
                }
                scene(route = GrittoNavRoutes.Task) { backStackEntry ->
                    TaskScreen(
                        taskId = backStackEntry.path<String>("taskId"),
                        navigator = navigator,
                    )
                }
                scene(route = GrittoNavRoutes.TaskEdit) { backStackEntry ->
                    TaskEditScreen(
                        taskId = backStackEntry.path<String>("taskId"),
                        navigator = navigator,
                    )
                }
                scene(route = GrittoNavRoutes.Goal) { backStackEntry ->
                    GoalScreen(
                        goalId = backStackEntry.path<String>("goalId"),
                        navigator = navigator,
                    )
                }
                scene(route = GrittoNavRoutes.GoalEdit) { backStackEntry ->
                    GoalEditScreen(
                        goalId = backStackEntry.path<String>("goalId"),
                        navigator = navigator,
                    )
                }
                scene(route = GrittoNavRoutes.GoalTree) { backStackEntry ->
                    GoalTreeScreen(
                        goalId = backStackEntry.path<String>("goalId"),
                        navigator = navigator,
                    )
                }
                scene(route = GrittoNavRoutes.GoalTreePreview) { backStackEntry ->
                    GoalTreePreviewScreen(
                        goalPreviewId = backStackEntry.path("goalPreviewId"),
                        repository = appState.repository,
                        navigator = navigator,
                    )
                }
                scene(route = GrittoNavRoutes.Milestone) { backStackEntry ->
                    MilestoneScreen(
                        milestoneId = backStackEntry.path<String>("milestoneId"),
                        navigator = navigator,
                    )
                }
                scene(route = GrittoNavRoutes.MilestoneEdit) { backStackEntry ->
                    MilestoneEditScreen(
                        milestoneId = backStackEntry.path<String>("milestoneId"),
                        navigator = navigator,
                    )
                }
                scene(route = GrittoNavRoutes.ProfileEdit) {
                    val coroutineScope = rememberCoroutineScope()
                    var isSaving by remember { mutableStateOf(false) }
                    var errorMessage by remember { mutableStateOf<String?>(null) }
                    val profile = appState.profile ?: SampleData.profile

                    ProfileEditScreen(
                        profile = profile,
                        isSaving = isSaving,
                        errorMessage = errorMessage,
                        onSave = { hours ->
                            isSaving = true
                            errorMessage = null
                            coroutineScope.launch {
                                when (val result = appState.repository.updateProfile(
                                    ProfileUpdateRequestDto(
                                        availableHoursPerWeek = hours,
                                    ),
                                )) {
                                    is ApiResult.Success -> {
                                        val updated = result.value.data.toProfileInfo()
                                        appState.updateProfile(updated)
                                        isSaving = false
                                        navigator.goBack()
                                    }
                                    is ApiResult.Error -> {
                                        errorMessage = result.message
                                        isSaving = false
                                    }
                                }
                            }
                        },
                        onCancel = { navigator.goBack() },
                    )
                }
            }
        }
    }
}
