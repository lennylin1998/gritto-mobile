package com.gritto.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
                scene(route = GrittoNavRoutes.GoalTreePreview) {
                    GoalTreePreviewScreen(navigator = navigator)
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
                    ProfileEditScreen(
                        profile = appState.profile,
                        navigator = navigator,
                        onSave = { hours ->
                            appState.profile = appState.profile.copy(availableHoursPerWeek = hours)
                        },
                    )
                }
            }
        }
    }
}
