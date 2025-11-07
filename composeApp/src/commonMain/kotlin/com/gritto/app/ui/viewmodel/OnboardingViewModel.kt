package com.gritto.app.ui.viewmodel

import com.gritto.app.data.network.ApiResult
import com.gritto.app.data.remote.model.AuthResponseDto
import com.gritto.app.data.repository.GrittoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

data class OnboardingUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
)

class OnboardingViewModel(
    private val repository: GrittoRepository,
    private val onAuthSuccess: (AuthResponseDto) -> Unit,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun signIn(idToken: String) {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.value = OnboardingUiState(isLoading = true, error = null)
            when (val result = repository.loginWithGoogle(idToken)) {
                is ApiResult.Success -> {
                    onAuthSuccess(result.value)
                    _uiState.value = OnboardingUiState()
                }

                is ApiResult.Error -> {
                    _uiState.value = OnboardingUiState(
                        isLoading = false,
                        error = result.message,
                    )
                }
            }
        }
    }
}
