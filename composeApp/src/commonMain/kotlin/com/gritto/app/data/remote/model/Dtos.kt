package com.gritto.app.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDto(
    val data: AuthDataDto,
)

@Serializable
data class AuthDataDto(
    val token: String,
    val user: ProfileDto,
)

@Serializable
data class ProfileDto(
    val id: String,
    val name: String,
    val email: String,
    val profileImageUrl: String? = null,
    val timezone: String? = null,
    val availableHoursPerWeek: Int,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class ActiveGoalDto(
    val id: String,
    val userId: String? = null,
    val title: String,
    val priority: Int,
    val color: Long,
    val totalTaskHours: Double,
    val doneTaskHours: Double,
)

@Serializable
data class TaskSummaryDto(
    val id: String,
    val goalId: String,
    val milestoneId: String,
    val title: String,
    val description: String? = null,
    val date: String,
    val estimatedHours: Double,
    val status: String? = null,
    val done: Boolean? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class TaskDetailResponseDto(
    val data: TaskDetailDto,
)

@Serializable
data class TaskDetailDto(
    val id: String,
    val goalId: String? = null,
    val milestoneId: String? = null,
    val title: String,
    val description: String? = null,
    val date: String,
    val estimatedHours: Double,
    val done: Boolean,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class TaskUpdateRequestDto(
    val title: String? = null,
    val description: String? = null,
    val date: String? = null,
    val estimatedHours: Double? = null,
    val done: Boolean? = null,
)

@Serializable
data class GoalDetailResponseDto(
    val data: GoalDetailDto,
)

@Serializable
data class GoalDetailDto(
    val id: String,
    val userId: String,
    val title: String,
    val description: String? = null,
    val context: String? = null,
    val startDate: String? = null,
    val targetDate: String? = null,
    val color: Long? = null,
    val status: String,
    val priority: Int,
    val minHoursPerWeek: Double,
    val milestones: List<String> = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class MilestoneSummaryDto(
    val id: String,
    val goalId: String,
    val parentMilestoneId: String? = null,
    val title: String,
    val status: String,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class MilestoneListResponseDto(
    val data: List<MilestoneSummaryDto>,
)

@Serializable
data class MilestoneDetailResponseDto(
    val data: MilestoneDetailDto,
)

@Serializable
data class MilestoneDetailDto(
    val id: String,
    val goalId: String,
    val parentMilestoneId: String? = null,
    val title: String,
    val description: String? = null,
    val status: String,
    val milestones: List<String> = emptyList(),
    val tasks: List<String> = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class ProfileUpdateRequestDto(
    val name: String? = null,
    val timezone: String? = null,
    val availableHoursPerWeek: Int? = null,
    val profileImageUrl: String? = null,
)

@Serializable
data class ChatSessionResponseDto(
    val data: ChatSessionDto,
)

@Serializable
data class ChatHistoryResponseDto(
    val data: ChatHistoryDto,
)

@Serializable
data class ChatSessionDto(
    val sessionId: String,
    val chatId: String? = null,
    val state: String? = null,
    val iteration: Int? = null,
    val goalPreviewId: String? = null,
    val context: ChatContextDto? = null,
    val sessionActive: Boolean? = null,
    val chat: ChatDto? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class ChatContextDto(
    val availableHoursLeft: Int? = null,
    val upcomingTasks: List<UpcomingTaskDto> = emptyList(),
)

@Serializable
data class ChatHistoryDto(
    val sessionId: String,
    val chatId: String? = null,
    val entries: List<ChatEntryDto> = emptyList(),
)

@Serializable
data class ChatGoalPreviewRequestDto(
    val goal: ChatGoalPreviewGoalDto,
    val milestones: List<GoalDraftMilestoneDto> = emptyList(),
    val iteration: Int? = null,
)

@Serializable
data class ChatGoalPreviewGoalDto(
    val title: String,
    val description: String? = null,
    val context: String? = null,
    val priority: Int? = null,
    val minHoursPerWeek: Double? = null,
    val color: Long? = null,
)

@Serializable
data class UpcomingTaskDto(
    val id: String,
    val title: String,
    val goalId: String? = null,
    val milestoneId: String? = null,
    val date: String? = null,
    val estimatedHours: Double,
    val done: Boolean? = null,
)

@Serializable
data class ChatMessageRequestDto(
    val sessionId: String,
    val userId: String,
    val message: String,
    val context: ChatContextDto? = null,
    val goalPreview: ChatGoalPreviewRequestDto? = null,
)

@Serializable
data class ChatMessageResponseDto(
    val sessionId: String,
    val reply: String,
    val action: ChatActionDto? = null,
    val state: ChatSessionStateDto? = null,
    val context: ChatContextDto? = null,
)

@Serializable
data class ChatActionDto(
    val type: String,
    val payload: ChatActionPayloadDto? = null,
)

@Serializable
data class ChatActionPayloadDto(
    val goalPreview: GoalPreviewPayloadDto? = null,
    val goalPreviewId: String? = null,
)

@Serializable
data class GoalPreviewPayloadDto(
    val id: String? = null,
    val goal: ChatGoalPreviewGoalDto? = null,
    val milestones: List<GoalDraftMilestoneDto> = emptyList(),
    val iteration: Int? = null,
    val status: String? = null,
)

@Serializable
data class GoalDraftDto(
    val title: String,
    val description: String? = null,
    val context: String? = null,
    val priority: Int? = null,
    val minHoursPerWeek: Double? = null,
    val color: Long? = null,
    val milestones: List<GoalDraftMilestoneDto> = emptyList(),
)

@Serializable
data class GoalDraftMilestoneDto(
    val title: String,
    val description: String? = null,
    val tasks: List<GoalDraftTaskDto> = emptyList(),
)

@Serializable
data class GoalDraftTaskDto(
    val title: String,
    val description: String? = null,
    val date: String? = null,
    val estimatedHours: Double? = null,
)

@Serializable
data class ChatSessionStateDto(
    val state: String? = null,
    val iteration: Int? = null,
    val sessionActive: Boolean? = null,
    val goalPreviewId: String? = null,
)

@Serializable
data class ApiListResponseDto<T>(
    val data: List<T>,
)

@Serializable
data class ApiDataResponseDto<T>(
    val data: T,
)

@Serializable
data class ChatDto(
    val id: String,
    val userId: String,
    val goalPreviewId: String? = null,
    val sessionId: String? = null,
    val entries: List<ChatEntryDto> = emptyList(),
    val createdAt: String? = null,
)

@Serializable
data class ChatEntryDto(
    val sender: String,
    val message: String,
    val timestamp: String,
)
