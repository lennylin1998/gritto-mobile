package com.gritto.app.gritto_mobile

import androidx.compose.runtime.Composable
import com.gritto.app.ui.screens.ChatScreen
import com.gritto.app.ui.screens.GoalEditPage
import com.gritto.app.ui.screens.GoalPage
import com.gritto.app.ui.screens.GoalTreePage
import com.gritto.app.ui.screens.GoalTreePreviewPage
import com.gritto.app.ui.screens.HomeScreen
import com.gritto.app.ui.screens.MilestonePage
import com.gritto.app.ui.screens.MilestoneEditPage
import com.gritto.app.ui.screens.ProfileScreen
import com.gritto.app.ui.screens.ProfileEditScreen
import com.gritto.app.ui.screens.TaskPage
import com.gritto.app.ui.screens.TaskEditPage
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.gritto.app.theme.GrittoTheme

@Composable
@Preview
fun App() {
    GrittoTheme {
        PreComposeApp {
            val navigator = rememberNavigator()
            GrittoNavHost(navigator = navigator)
        }
    }
}

@Composable
private fun GrittoNavHost(navigator: Navigator) {
    NavHost(
        navigator = navigator,
        initialRoute = GrittoNavRoutes.Home
    ) {
        scene(route = GrittoNavRoutes.Home) {
            HomeScreen(navigator = navigator)
        }
        scene(route = GrittoNavRoutes.Chat) {
            ChatScreen(navigator = navigator)
        }
        scene(route = GrittoNavRoutes.Profile) {
            ProfileScreen(navigator = navigator)
        }
        scene(route = GrittoNavRoutes.ProfileEdit) {
            ProfileEditScreen(navigator = navigator)
        }
        scene(route = GrittoNavRoutes.Goal) { backStackEntry ->
            val goalId = backStackEntry.path<String>("goalId")
            GoalPage(navigator = navigator, goalId = goalId)
        }
        scene(route = GrittoNavRoutes.GoalEdit) { backStackEntry ->
            val goalId = backStackEntry.path<String>("goalId")
            GoalEditPage(navigator = navigator, goalId = goalId)
        }
        scene(route = GrittoNavRoutes.Milestone) { backStackEntry ->
            val milestoneId = backStackEntry.path<String>("milestoneId")
            MilestonePage(navigator = navigator, milestoneId = milestoneId)
        }
        scene(route = GrittoNavRoutes.MilestoneEdit) { backStackEntry ->
            val milestoneId = backStackEntry.path<String>("milestoneId")
            MilestoneEditPage(navigator = navigator, milestoneId = milestoneId)
        }
        scene(route = GrittoNavRoutes.Task) { backStackEntry ->
            val taskId = backStackEntry.path<String>("taskId")
            TaskPage(navigator = navigator, taskId = taskId)
        }
        scene(route = GrittoNavRoutes.TaskEdit) { backStackEntry ->
            val taskId = backStackEntry.path<String>("taskId")
            TaskEditPage(navigator = navigator, taskId = taskId)
        }
        scene(route = GrittoNavRoutes.GoalTree) { backStackEntry ->
            val goalId = backStackEntry.path<String>("goalId")
            GoalTreePage(navigator = navigator, goalId = goalId)
        }
        scene(route = GrittoNavRoutes.GoalTreePreview) {
            GoalTreePreviewPage(navigator = navigator)
        }
    }
}
const val Home = "home"
const val Chat = "chat"
const val Profile = "profile"
const val Goal = "goal/{goalId}"
const val GoalEdit = "goal_edit/{goalId}"
const val Milestone = "milestone/{milestoneId}"
const val Task = "task/{taskId}"
const val GoalTree = "goal_tree/{goalId}"
const val GoalTreePreview = "goal_tree_preview"
internal object GrittoNavRoutes {
    const val Home: String = "home"
    const val Chat: String = "chat"
    const val Profile: String ="profile"
    const val ProfileEdit: String = "profile/edit"
    const val Goal: String = "goal/{goalId}"
    const val GoalEdit: String = "goal/edit/{goalId}"
    const val Milestone: String = "milestone/{milestoneId}"
    const val MilestoneEdit: String = "milestone/edit/{milestoneId}"
    const val Task: String = "task/{taskId}"
    const val TaskEdit: String = "task_edit/{taskId}"
    const val GoalTree: String = "goal_tree/{goalId}"
    const val GoalTreePreview: String = "goal_tree_preview/{goalPreviewId}"

    fun goal(goalId: String): String = Goal.replace("{goalId}", goalId)
    fun goalEdit(goalId: String): String = GoalEdit.replace("{goalId}", goalId)
    fun milestone(milestoneId: String): String = Milestone.replace("{milestoneId}", milestoneId)
    fun milestoneEdit(milestoneId: String): String = MilestoneEdit.replace("{milestoneId}", milestoneId)
    fun task(taskId: String): String = Task.replace("{taskId}", taskId)
    fun taskEdit(taskId: String): String = TaskEdit.replace("{taskId}", taskId)
    fun goalTree(goalId: String): String = GoalTree.replace("{goalId}", goalId)
}
