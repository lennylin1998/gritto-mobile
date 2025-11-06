package com.gritto.app.ui.viewmodel

import com.gritto.app.data.network.ApiResult
import com.gritto.app.data.remote.model.ProfileDto
import com.gritto.app.data.remote.model.ProfileUpdateRequestDto
import com.gritto.app.data.remote.model.toProfileInfo
import com.gritto.app.data.repository.GrittoRepository
import com.gritto.app.model.ProfileInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: ProfileInfo? = null,
    val error: String? = null,
)

class ProfileViewModel(
    private val repository: GrittoRepository,
    private val onProfileLoaded: (ProfileInfo) -> Unit = {},
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState(isLoading = true)
            when (val result = repository.fetchProfile()) {
                is ApiResult.Success -> {
                    val profile = result.value.data.toProfileInfo()
                    onProfileLoaded(profile)
                    _uiState.value = ProfileUiState(isLoading = false, profile = profile)
                }

                is ApiResult.Error -> {
                    _uiState.value = ProfileUiState(
                        isLoading = false,
                        error = result.message,
                    )
                }
            }
        }
    }

    fun updateAvailableHours(hours: Int, onComplete: (Result<ProfileInfo>) -> Unit = {}) {
        viewModelScope.launch {
            when (val result = repository.updateProfile(ProfileUpdateRequestDto(availableHoursPerWeek = hours))) {
                is ApiResult.Success -> {
                    val profile = result.value.data.toProfileInfo()
                    onProfileLoaded(profile)
                    _uiState.value = _uiState.value.copy(profile = profile, error = null)
                    onComplete(Result.success(profile))
                }

                is ApiResult.Error -> {
                    onComplete(Result.failure(IllegalStateException(result.message)))
                }
            }
        }
    }
}
