package com.gritto.app.data.repository

import com.gritto.app.data.network.ApiClient
import com.gritto.app.data.network.ApiResult
import com.gritto.app.data.remote.model.ActiveGoalDto
import com.gritto.app.data.remote.model.ApiDataResponseDto
import com.gritto.app.data.remote.model.ApiListResponseDto
import com.gritto.app.data.remote.model.AuthResponseDto
import com.gritto.app.data.remote.model.ChatHistoryResponseDto
import com.gritto.app.data.remote.model.ChatMessageRequestDto
import com.gritto.app.data.remote.model.ChatMessageResponseDto
import com.gritto.app.data.remote.model.MilestoneDetailResponseDto
import com.gritto.app.data.remote.model.MilestoneListResponseDto
import com.gritto.app.data.remote.model.ProfileDto
import com.gritto.app.data.remote.model.ProfileUpdateRequestDto
import com.gritto.app.data.remote.model.TaskDetailResponseDto
import com.gritto.app.data.remote.model.TaskSummaryDto
import com.gritto.app.data.remote.model.TaskStatusDto
import com.gritto.app.data.remote.model.TaskUpdateRequestDto
import com.gritto.app.data.remote.model.GoalPreviewResponseDto
import io.ktor.client.request.parameter
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

interface GrittoRepository {
    suspend fun loginWithGoogle(idToken: String): ApiResult<AuthResponseDto>
    suspend fun fetchProfile(): ApiResult<ApiDataResponseDto<ProfileDto>>
    suspend fun updateProfile(hours: Int): ApiResult<ApiDataResponseDto<ProfileDto>>
    suspend fun fetchTasksForDay(day: LocalDate): ApiResult<ApiListResponseDto<TaskSummaryDto>>
    suspend fun fetchActiveGoals(): ApiResult<ApiListResponseDto<ActiveGoalDto>>
    suspend fun fetchTaskDetail(taskId: String): ApiResult<TaskDetailResponseDto>
    suspend fun fetchGoalDetail(goalId: String): ApiResult<ApiDataResponseDto<com.gritto.app.data.remote.model.GoalDetailDto>>
    suspend fun fetchGoalMilestones(goalId: String): ApiResult<MilestoneListResponseDto>
    suspend fun fetchMilestoneDetail(milestoneId: String): ApiResult<MilestoneDetailResponseDto>
    suspend fun fetchLatestGoalSession(): ApiResult<com.gritto.app.data.remote.model.ChatSessionResponseDto>
    suspend fun fetchGoalPreview(goalPreviewId: String): ApiResult<GoalPreviewResponseDto>
    suspend fun fetchGoalSessionHistory(sessionId: String): ApiResult<ChatHistoryResponseDto>
    suspend fun sendGoalSessionMessage(body: ChatMessageRequestDto): ApiResult<ChatMessageResponseDto>
    suspend fun updateTask(taskId: String, request: TaskUpdateRequestDto): ApiResult<TaskDetailResponseDto>
    suspend fun markTaskDone(taskId: String): ApiResult<ApiDataResponseDto<TaskStatusDto>>
    suspend fun markTaskUndone(taskId: String): ApiResult<ApiDataResponseDto<TaskStatusDto>>
    suspend fun updateProfile(request: ProfileUpdateRequestDto): ApiResult<ApiDataResponseDto<ProfileDto>>
}

class DefaultGrittoRepository(
    private val apiClient: ApiClient,
) : GrittoRepository {

    override suspend fun loginWithGoogle(idToken: String): ApiResult<AuthResponseDto> =
        apiClient.post(
            path = "/v1/auth/google",
            body = mapOf("idToken" to idToken),
        )

    override suspend fun fetchProfile(): ApiResult<ApiDataResponseDto<ProfileDto>> =
        apiClient.get(path = "/v1/me")

    override suspend fun updateProfile(hours: Int): ApiResult<ApiDataResponseDto<ProfileDto>> =
        apiClient.put(
            path = "/v1/me",
            body = mapOf("availableHoursPerWeek" to hours),
        )

    override suspend fun fetchTasksForDay(day: LocalDate): ApiResult<ApiListResponseDto<TaskSummaryDto>> =
        apiClient.get(path = "/v1/tasks:query") {
            parameter("day", day.toString())
        }

    override suspend fun fetchActiveGoals(): ApiResult<ApiListResponseDto<ActiveGoalDto>> =
        apiClient.get(path = "/v1/goals") {
            parameter("status", "active")
        }

    override suspend fun fetchTaskDetail(taskId: String): ApiResult<TaskDetailResponseDto> =
        apiClient.get(path = "/v1/tasks/$taskId")

    override suspend fun fetchGoalDetail(goalId: String): ApiResult<ApiDataResponseDto<com.gritto.app.data.remote.model.GoalDetailDto>> =
        apiClient.get(path = "/v1/goals/$goalId")

    override suspend fun fetchGoalMilestones(goalId: String): ApiResult<MilestoneListResponseDto> =
        apiClient.get(path = "/v1/goals/$goalId/milestones")

    override suspend fun fetchMilestoneDetail(milestoneId: String): ApiResult<MilestoneDetailResponseDto> =
        apiClient.get(path = "/v1/milestones/$milestoneId")

    override suspend fun fetchLatestGoalSession(): ApiResult<com.gritto.app.data.remote.model.ChatSessionResponseDto> =
        apiClient.get(path = "/v1/agent/goal/session:latest")

    override suspend fun fetchGoalPreview(goalPreviewId: String): ApiResult<GoalPreviewResponseDto> =
        apiClient.get(path = "/v1/goal-previews/$goalPreviewId")

    override suspend fun fetchGoalSessionHistory(sessionId: String): ApiResult<ChatHistoryResponseDto> =
        apiClient.get(path = "/v1/agent/goal/session/$sessionId/history")

    override suspend fun sendGoalSessionMessage(body: ChatMessageRequestDto): ApiResult<ChatMessageResponseDto> =
        apiClient.post(path = "/v1/agent/goal/session:message", body = body)

    override suspend fun updateTask(taskId: String, request: TaskUpdateRequestDto): ApiResult<TaskDetailResponseDto> =
        apiClient.patch(path = "/v1/tasks/$taskId", body = request)

    override suspend fun markTaskDone(taskId: String): ApiResult<ApiDataResponseDto<TaskStatusDto>> =
        apiClient.post(path = "/v1/tasks/$taskId/done", body = emptyMap<String, String>())

    override suspend fun markTaskUndone(taskId: String): ApiResult<ApiDataResponseDto<TaskStatusDto>> =
        apiClient.post(path = "/v1/tasks/$taskId/undone", body = emptyMap<String, String>())

    override suspend fun updateProfile(request: ProfileUpdateRequestDto): ApiResult<ApiDataResponseDto<ProfileDto>> =
        apiClient.patch(path = "/v1/me", body = request)
}
