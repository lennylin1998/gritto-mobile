package com.gritto.app.model

import com.gritto.app.ui.model.GoalUiModel
import com.gritto.app.ui.model.TaskListUiModel
import com.gritto.app.ui.model.TaskUiModel

data class DailyTask(
    val id: String,
    val title: String,
    val description: String,
    val scheduleLabel: String,
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

data class TaskDetail(
    val id: String,
    val title: String,
    val date: String,
    val estimatedHours: Double,
    val done: Boolean,
    val description: String,
)

enum class MilestoneStatus(val label: String) {
    Blocked("Blocked"),
    InProgress("In Progress"),
    Finished("Finished"),
}

data class MilestoneDetail(
    val id: String,
    val title: String,
    val date: String,
    val estimatedHours: Double,
    val status: MilestoneStatus,
    val description: String,
)

data class GoalDetail(
    val id: String,
    val title: String,
    val startTime: String,
    val description: String,
    val context: String,
    val color: Long,
)

data class ProfileInfo(
    val name: String,
    val email: String,
    val availableHoursPerWeek: Int,
)

enum class GoalTreeNodeType {
    Goal,
    Milestone,
    Task,
}

data class GoalTreeNode(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val type: GoalTreeNodeType,
    val children: List<GoalTreeNode> = emptyList(),
)

sealed class ChatMessage(open val id: String, open val text: String) {
    data class User(override val id: String, override val text: String) : ChatMessage(id, text)
    data class Agent(override val id: String, override val text: String) : ChatMessage(id, text)
}

object SampleData {
    val todayTasks = listOf(
        DailyTask(
            id = "task-1",
            title = "Meeting with UI team",
            description = "To be successful in time management...",
            scheduleLabel = "10:00 AM",
            completed = false
        ),
        DailyTask(
            id = "task-2",
            title = "Product",
            description = "Check the business plan",
            scheduleLabel = "11:30 AM",
            completed = true
        ),
        DailyTask(
            id = "task-3",
            title = "Desktop UI mockup",
            description = "Prototype new client's website",
            scheduleLabel = "01:00 PM",
            completed = false
        ),
        DailyTask(
            id = "task-4",
            title = "Meeting with SMM Team",
            description = "Meeting about new social media strategy",
            scheduleLabel = "02:30 PM",
            completed = false
        ),
        DailyTask(
            id = "task-5",
            title = "Coffee with Mike",
            description = "Enjoy!",
            scheduleLabel = "06:00 PM",
            completed = false
        )
    )

    val homeTaskLists = listOf(
        TaskListUiModel(
            id = "day-1",
            dateLabel = "Mon, Aug 19",
            tasks = listOf(
                TaskUiModel(id = "task-1", title = "Kickoff with design", startTimeLabel = "09:00", endTimeLabel = "09:45", isCompleted = false),
                TaskUiModel(id = "task-2", title = "Define MVP checklist", startTimeLabel = "10:15", endTimeLabel = "11:00", isCompleted = false),
                TaskUiModel(id = "task-3", title = "Sync with growth team", startTimeLabel = "11:30", endTimeLabel = "12:00", isCompleted = false),
            ),
        ),
        TaskListUiModel(
            id = "day-2",
            dateLabel = "Tue, Aug 20",
            tasks = listOf(
                TaskUiModel(id = "task-4", title = "UX review with Anita", startTimeLabel = "08:30", endTimeLabel = "09:15", isCompleted = false),
                TaskUiModel(id = "task-5", title = "Ship onboarding copy", startTimeLabel = "10:00", endTimeLabel = "10:30", isCompleted = false),
                TaskUiModel(id = "task-6", title = "AI flow walkthrough", startTimeLabel = "14:00", endTimeLabel = "15:00", isCompleted = false),
            ),
        ),
        TaskListUiModel(
            id = "day-3",
            dateLabel = "Wed, Aug 21",
            tasks = listOf(
                TaskUiModel(id = "task-7", title = "Team retro prep", startTimeLabel = "09:30", endTimeLabel = "10:00", isCompleted = false),
                TaskUiModel(id = "task-8", title = "Customer interviews", startTimeLabel = "11:00", endTimeLabel = "12:30", isCompleted = false),
                TaskUiModel(id = "task-9", title = "Prototype usability fixes", startTimeLabel = "15:00", endTimeLabel = "16:30", isCompleted = false),
            ),
        ),
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

    val homeGoals = listOf(
        GoalUiModel(
            id = "goal-home-1",
            name = "Launch Gritto MVP",
            progress = 0.62f,
            priority = 1,
            accentColor = 0xFF7C4DFF,
            description = "Ship the core mobile experience to first cohort.",
        ),
        GoalUiModel(
            id = "goal-home-2",
            name = "Grow engaged waitlist",
            progress = 0.4f,
            priority = 2,
            accentColor = 0xFF26A69A,
            description = "Reach 500 signups with weekly nurture.",
        ),
        GoalUiModel(
            id = "goal-home-3",
            name = "Improve retention metrics",
            progress = 0.18f,
            priority = 3,
            accentColor = 0xFFFFA000,
            description = "Increase day-7 retention to 35%.",
        ),
        GoalUiModel(
            id = "goal-home-4",
            name = "Refine onboarding copy",
            progress = 0.73f,
            priority = 4,
            accentColor = 0xFF42A5F5,
            description = "Tighten value prop and reduce drop-off.",
        ),
    )

    val scheduledTasks = listOf(
        ScheduledTask(id = "schedule-1", title = "Sprint planning sync", dateTime = "Mon • 10:00", status = TaskStatus.Pending),
        ScheduledTask(id = "schedule-2", title = "Evening reflection", dateTime = "Mon • 20:30", status = TaskStatus.Pending),
        ScheduledTask(id = "schedule-3", title = "Share progress update", dateTime = "Tue • 09:15", status = TaskStatus.Completed)
    )

    val taskDetails = listOf(
        TaskDetail(
            id = "task-1",
            title = "Kickoff with design",
            date = "Aug 19, 2024",
            estimatedHours = 1.0,
            done = false,
            description = "Connect with design partners to align on MVP scope and visual direction.",
        ),
        TaskDetail(
            id = "task-2",
            title = "Define MVP checklist",
            date = "Aug 19, 2024",
            estimatedHours = 1.5,
            done = true,
            description = "Break down the core functionality we must ship to onboard first cohort.",
        ),
        TaskDetail(
            id = "task-7",
            title = "Team retro prep",
            date = "Aug 21, 2024",
            estimatedHours = 0.5,
            done = false,
            description = "Collect highlights and blockers so the retro is productive.",
        ),
    ).associateBy { it.id }

    val goalDetails = listOf(
        GoalDetail(
            id = "goal-home-1",
            title = "Launch Gritto MVP",
            startTime = "08:00",
            description = "Ship MVP experience to onboard first 30 builders.",
            context = "Focus on core flows: onboarding, home prioritisation, and check-ins.",
            color = 0xFF7C4DFF,
        ),
        GoalDetail(
            id = "goal-home-2",
            title = "Grow engaged waitlist",
            startTime = "09:30",
            description = "Reach 500 engaged people on the waitlist by October.",
            context = "Leverage existing communities and share weekly learning threads.",
            color = 0xFF26A69A,
        ),
    ).associateBy { it.id }

    val milestoneDetails = listOf(
        MilestoneDetail(
            id = "milestone-1",
            title = "Ship onboarding loop",
            date = "Aug 23, 2024",
            estimatedHours = 12.0,
            status = MilestoneStatus.InProgress,
            description = "Create account, basic profile, and first task import.",
        ),
        MilestoneDetail(
            id = "milestone-2",
            title = "Polish home card interactions",
            date = "Aug 28, 2024",
            estimatedHours = 10.0,
            status = MilestoneStatus.Blocked,
            description = "Finalize drag-and-drop for goals and snappy task list.",
        ),
    ).associateBy { it.id }

    val goalTreeByGoal = mapOf(
        "goal-home-1" to GoalTreeNode(
            id = "goal-home-1",
            title = "Launch Gritto MVP",
            subtitle = "Target: Sep 30",
            type = GoalTreeNodeType.Goal,
            children = listOf(
                GoalTreeNode(
                    id = "milestone-1",
                    title = "Ship onboarding loop",
                    subtitle = "In progress · 12h",
                    type = GoalTreeNodeType.Milestone,
                    children = listOf(
                        GoalTreeNode(
                            id = "task-1",
                            title = "Kickoff with design",
                            subtitle = "09:00 – 09:45",
                            type = GoalTreeNodeType.Task,
                        ),
                        GoalTreeNode(
                            id = "task-2",
                            title = "Define MVP checklist",
                            subtitle = "10:15 – 11:00",
                            type = GoalTreeNodeType.Task,
                        ),
                        GoalTreeNode(
                            id = "task-10",
                            title = "Review copy with growth",
                            subtitle = "Async · 45m",
                            type = GoalTreeNodeType.Task,
                        ),
                    ),
                ),
                GoalTreeNode(
                    id = "milestone-2",
                    title = "Polish home card interactions",
                    subtitle = "Blocked · 10h",
                    type = GoalTreeNodeType.Milestone,
                    children = listOf(
                        GoalTreeNode(
                            id = "task-7",
                            title = "Team retro prep",
                            subtitle = "09:30 – 10:00",
                            type = GoalTreeNodeType.Task,
                        ),
                        GoalTreeNode(
                            id = "task-11",
                            title = "Prototype drag handles",
                            subtitle = "Design · 2h",
                            type = GoalTreeNodeType.Task,
                        ),
                    ),
                ),
            ),
        ),
        "goal-home-2" to GoalTreeNode(
            id = "goal-home-2",
            title = "Grow engaged waitlist",
            subtitle = "Target: 500 people",
            type = GoalTreeNodeType.Goal,
            children = listOf(
                GoalTreeNode(
                    id = "milestone-3",
                    title = "Publish weekly progress thread",
                    subtitle = "Finished · 6h",
                    type = GoalTreeNodeType.Milestone,
                    children = listOf(
                        GoalTreeNode(
                            id = "task-12",
                            title = "Draft narrative arc",
                            subtitle = "08:00 – 09:15",
                            type = GoalTreeNodeType.Task,
                        ),
                        GoalTreeNode(
                            id = "task-13",
                            title = "Collect user stories",
                            subtitle = "Async · 1h",
                            type = GoalTreeNodeType.Task,
                        ),
                    ),
                ),
            ),
        ),
    )

    val goalTreePreview = goalTreeByGoal["goal-home-1"] ?: GoalTreeNode(
        id = "preview",
        title = "Preview Goal Tree",
        type = GoalTreeNodeType.Goal,
    )

    val profile = ProfileInfo(
        name = "Alex Rivers",
        email = "alex@gritto.app",
        availableHoursPerWeek = 35,
    )

    val initialChat = listOf(
        ChatMessage.Agent(id = "chat-1", text = "Hi! I'm your Gritto reflection partner. What would you like to celebrate today?"),
        ChatMessage.User(id = "chat-2", text = "I wrapped the onboarding flow draft."),
        ChatMessage.Agent(id = "chat-3", text = "Nice work. How confident do you feel about shipping it tomorrow?")
    )
}
