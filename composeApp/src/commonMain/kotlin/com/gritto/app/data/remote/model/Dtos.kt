package com.gritto.app.data.remote.model

import kotlinx.serialization.SerialName
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
    val title: String,
    val priority: Int,
    val color: Long,
    val totalTaskHours: Double,
    val doneTaskHours: Double,
)

@Serializable
data class TaskSummaryDto(
    val id: String,
    val milestoneId: String,
    val title: String,
    val description: String? = null,
    val date: String,
    val estimatedHours: Double,
    val status: String? = null,
    val done: Boolean? = null,
)

@Serializable
data class TaskDetailResponseDto(
    val data: TaskDetailDto,
)

@Serializable
data class TaskDetailDto(
    val id: String,
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
    val title: String,
    val description: String? = null,
    val startDate: String? = null,
    val color: Long? = null,
    val status: String? = null,
    val priority: Int? = null,
    val minHoursPerWeek: Double? = null,
)

@Serializable
data class MilestoneSummaryDto(
    val id: String,
    val title: String,
    val status: String,
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
    val title: String,
    val description: String? = null,
    val status: String,
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
data class ChatSessionDto(
    val sessionId: String,
    val chatId: String? = null,
    val state: String? = null,
    val iteration: Int? = null,
    val goalPreviewId: String? = null,
    val context: ChatContextDto? = null,
)

@Serializable
data class ChatContextDto(
    val availableHoursLeft: Int? = null,
    val upcomingTasks: List<UpcomingTaskDto> = emptyList(),
)

@Serializable
data class UpcomingTaskDto(
    val id: String,
    val title: String,
    val goalId: String? = null,
    val milestoneId: String? = null,
    val date: String? = null,
    val estimatedHours: Double? = null,
    val done: Boolean? = null,
)

@Serializable
data class ChatMessageRequestDto(
    val sessionId: String,
    val userId: String,
    val message: String,
    val context: ChatContextDto? = null,
)

@Serializable
data class ChatMessageResponseDto(
    val sessionId: String,
    val reply: String,
    val action: ChatActionDto? = null,
    val state: ChatSessionStateDto? = null,
)

@Serializable
data class ChatActionDto(
    val type: String,
    val payload: ChatActionPayloadDto? = null,
)

@Serializable
data class ChatActionPayloadDto(
    val goalPreview: GoalPreviewDto? = null,
    val goalPreviewId: String? = null,
)

@Serializable
data class GoalPreviewDto(
    val id: String? = null,
    val goal: PreviewGoalDto? = null,
    val milestones: List<PreviewMilestoneDto> = emptyList(),
)

@Serializable
data class PreviewGoalDto(
    val title: String,
)

@Serializable
data class PreviewMilestoneDto(
    val title: String,
    val tasks: List<PreviewTaskDto> = emptyList(),
)

@Serializable
data class PreviewTaskDto(
    val title: String,
)

@Serializable
data class ChatSessionStateDto(
    val state: String? = null,
    val iteration: Int? = null,
    val sessionActive: Boolean? = null,
)

@Serializable
data class ApiListResponseDto<T>(
    val data: List<T>,
)

@Serializable
data class ApiDataResponseDto<T>(
    val data: T,
)
