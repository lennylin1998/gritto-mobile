package com.gritto.app.navigation

object GrittoNavRoutes {
    const val Main = "main"
    const val Task = "task/{taskId}"
    const val TaskEdit = "task/edit/{taskId}"
    const val Goal = "goal/{goalId}"
    const val GoalEdit = "goal/edit/{goalId}"
    const val GoalTree = "goal-tree/{goalId}"
    const val GoalTreePreview = "goal-tree-preview/{goalPreviewId}"
    const val Milestone = "milestone/{milestoneId}"
    const val MilestoneEdit = "milestone/edit/{milestoneId}"
    const val ProfileEdit = "profile/edit"

    fun task(taskId: String): String = Task.replace("{taskId}", taskId)
    fun taskEdit(taskId: String): String = TaskEdit.replace("{taskId}", taskId)
    fun goal(goalId: String): String = Goal.replace("{goalId}", goalId)
    fun goalEdit(goalId: String): String = GoalEdit.replace("{goalId}", goalId)
    fun goalTree(goalId: String): String = GoalTree.replace("{goalId}", goalId)
    fun goalTreePreview(goalPreviewId: String): String = GoalTreePreview.replace("{goalPreviewId}", goalPreviewId)
    fun milestone(milestoneId: String): String = Milestone.replace("{milestoneId}", milestoneId)
    fun milestoneEdit(milestoneId: String): String = MilestoneEdit.replace("{milestoneId}", milestoneId)
}
