package com.gritto.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gritto.app.data.network.ApiClient
import com.gritto.app.data.network.createHttpClient
import com.gritto.app.data.network.platformHttpClientEngine
import com.gritto.app.data.network.provideJson
import com.gritto.app.data.remote.model.AuthResponseDto
import com.gritto.app.data.remote.model.ProfileDto
import com.gritto.app.data.remote.model.toProfileInfo
import com.gritto.app.data.repository.DefaultGrittoRepository
import com.gritto.app.data.repository.GrittoRepository
import com.gritto.app.model.ChatMessage
import com.gritto.app.model.ProfileInfo
import com.gritto.app.model.SampleData
import com.gritto.app.ui.components.MainNavDestination
import io.ktor.client.HttpClient

class GrittoAppState internal constructor(
    val tokenHolder: TokenHolder,
    val httpClient: HttpClient,
    val repository: GrittoRepository,
) {
    var isSignedIn by mutableStateOf(false)
 
    var selectedDestination by mutableStateOf(MainNavDestination.Home)
 
    val chatHistory = mutableStateListOf<ChatMessage>().apply { addAll(SampleData.initialChat) }
    var messageCounter by mutableStateOf(chatHistory.size)
 
    var profile by mutableStateOf<ProfileInfo?>(SampleData.profile)
        private set
    var userId by mutableStateOf<String?>(null)
 
    val sessionToken: String? get() = tokenHolder.token
 
    fun updateAuth(authResponse: AuthResponseDto) {
        tokenHolder.token = authResponse.data.token
        profile = authResponse.data.user.toProfileInfo()
        userId = authResponse.data.user.id
        isSignedIn = true
    }
 
    fun updateProfile(profileInfo: ProfileInfo) {
        profile = profileInfo
    }
 
    fun signOut() {
        tokenHolder.token = null
        profile = null
        userId = null
        isSignedIn = false
        selectedDestination = MainNavDestination.Home
    }
}

class TokenHolder(var token: String? = null)

@Composable
fun rememberGrittoState(): GrittoAppState {
    val tokenHolder = remember { TokenHolder() }
    val json = remember { provideJson() }
    val httpClient = remember { createHttpClient(platformHttpClientEngine(), json) }
    val apiClient = remember {
        ApiClient(
            httpClient = httpClient,
//            baseUrl = "http://10.194.233.51:8080",
            baseUrl = "http://100.70.70.2:8080",
            tokenProvider = { tokenHolder.token },
        )
    }
    val repository = remember { DefaultGrittoRepository(apiClient) }
    return remember { GrittoAppState(tokenHolder, httpClient, repository) }
}
