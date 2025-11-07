package com.gritto.app.platform

import androidx.compose.runtime.Composable

@Composable
actual fun rememberGoogleAuthLauncher(
    clientId: String,
    onResult: (Result<String>) -> Unit,
): GoogleAuthLauncher? = null

actual val platformGoogleClientId: String = ""
