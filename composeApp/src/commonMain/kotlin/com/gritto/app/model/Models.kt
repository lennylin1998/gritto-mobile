package com.gritto.app.model

data class DailyTask(
    val id: String,
    val title: String,
    val time: String,
    val completed: Boolean
)

data class GoalProgress(
    val id: String,
    val title: String,
    val description: String,
    val completion: Float
)

data class ScheduledTask(
    val id: String,
    val title: String,
    val dateTime: String,
    val status: TaskStatus
)

enum class TaskStatus {
    Pending,
    Completed
}

sealed class ChatMessage(open val id: String, open val text: String) {
    data class User(override val id: String, override val text: String) : ChatMessage(id, text)
    data class Agent(override val id: String, override val text: String) : ChatMessage(id, text)
}

object SampleData {
    val todayTasks = listOf(
        DailyTask(id = "task-1", title = "Morning focus session", time = "07:30", completed = false),
        DailyTask(id = "task-2", title = "Submit project outline", time = "11:00", completed = true),
        DailyTask(id = "task-3", title = "Run 5km workout", time = "18:30", completed = false)
    )

    val goals = listOf(
        GoalProgress(
            id = "goal-1",
            title = "Launch Gritto MVP",
            description = "Ship core onboarding, home, and reflection experiences.",
            completion = 0.65f
        ),
        GoalProgress(
            id = "goal-2",
            title = "Build daily writing habit",
            description = "Draft 150 words every morning before 9am.",
            completion = 0.45f
        ),
        GoalProgress(
            id = "goal-3",
            title = "Improve cardio endurance",
            description = "Train for a 10k run with three sessions each week.",
            completion = 0.3f
        )
    )

    val scheduledTasks = listOf(
        ScheduledTask(id = "schedule-1", title = "Sprint planning sync", dateTime = "Mon • 10:00", status = TaskStatus.Pending),
        ScheduledTask(id = "schedule-2", title = "Evening reflection", dateTime = "Mon • 20:30", status = TaskStatus.Pending),
        ScheduledTask(id = "schedule-3", title = "Share progress update", dateTime = "Tue • 09:15", status = TaskStatus.Completed)
    )

    val initialChat = listOf(
        ChatMessage.Agent(id = "chat-1", text = "Hi! I'm your Gritto reflection partner. What would you like to celebrate today?"),
        ChatMessage.User(id = "chat-2", text = "I wrapped the onboarding flow draft."),
        ChatMessage.Agent(id = "chat-3", text = "Nice work. How confident do you feel about shipping it tomorrow?")
    )
}
