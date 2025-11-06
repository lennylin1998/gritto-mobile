package com.gritto.app.data.remote.model

import com.gritto.app.model.ProfileInfo
import com.gritto.app.model.TaskDetail

fun ProfileDto.toProfileInfo(): ProfileInfo = ProfileInfo(
    name = name,
    email = email,
    availableHoursPerWeek = availableHoursPerWeek,
    profileImageUrl = profileImageUrl,
    timezone = timezone ?: "UTC",
)

fun TaskDetailDto.toTaskDetail(): TaskDetail = TaskDetail(
    id = id,
    title = title,
    date = date,
    estimatedHours = estimatedHours,
    done = done,
    description = description.orEmpty(),
)
