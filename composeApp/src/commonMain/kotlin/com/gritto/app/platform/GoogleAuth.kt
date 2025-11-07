package com.gritto.app.platform

import androidx.compose.runtime.Composable

interface GoogleAuthLauncher {
    fun launchBottomSheet()
    fun launchSignInButton()
}

@Composable
expect fun rememberGoogleAuthLauncher(
    clientId: String,
    onResult: (Result<String>) -> Unit,
): GoogleAuthLauncher?

expect val platformGoogleClientId: String
