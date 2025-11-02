package com.gritto.app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import com.gritto.app.model.ChatMessage
import com.gritto.app.model.SampleData
import com.gritto.app.ui.model.GoalUiModel
import com.gritto.app.ui.components.MainNavDestination

class GrittoState {
    var isSignedIn by mutableStateOf(false)
    var selectedDestination by mutableStateOf(MainNavDestination.Home)

    var homeTaskLists by mutableStateOf(SampleData.homeTaskLists)
    val homeGoals = mutableStateListOf<GoalUiModel>().apply { addAll(SampleData.homeGoals) }
    val chatHistory = mutableStateListOf<ChatMessage>().apply { addAll(SampleData.initialChat) }
    var messageCounter by mutableStateOf(chatHistory.size)
    var profile by mutableStateOf(SampleData.profile)
}

@Composable
fun rememberGrittoState(): GrittoState = remember { GrittoState() }
