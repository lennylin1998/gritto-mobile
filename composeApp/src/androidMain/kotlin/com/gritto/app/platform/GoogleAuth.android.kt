package com.gritto.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.gritto.app.config.AppConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
actual fun rememberGoogleAuthLauncher(
    clientId: String,
    onResult: (Result<String>) -> Unit,
): GoogleAuthLauncher? {
    if (clientId.isBlank()) return null
    val context = LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }
    val coroutineScope = rememberCoroutineScope()

    val bottomSheetOption = remember(clientId) {
        GetGoogleIdOption.Builder()
            .setServerClientId(clientId)
            .setFilterByAuthorizedAccounts(false)
            .build()
    }

    val buttonOption = remember(clientId) {
        GetSignInWithGoogleOption.Builder(clientId).build()
    }

    fun launchWithOption(option: CredentialOption) {
        coroutineScope.launch {
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(option)
                .build()
            try {
                val result = credentialManager.getCredential(
                    context = context,
                    request = request,
                )
                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    try {
                        val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        onResult(Result.success(googleCredential.idToken))
                    } catch (e: GoogleIdTokenParsingException) {
                        onResult(Result.failure(e))
                    }
                } else {
                    onResult(Result.failure(IllegalStateException("Unexpected credential type")))
                }
            } catch (exception: GetCredentialException) {
                val failure = if (exception is GetCredentialCancellationException) {
                    CancellationException("Google Sign-In cancelled", exception)
                } else {
                    exception
                }
                onResult(Result.failure(failure))
            }
        }
    }

    return object : GoogleAuthLauncher {
        override fun launchBottomSheet() {
            launchWithOption(bottomSheetOption)
        }

        override fun launchSignInButton() {
            launchWithOption(buttonOption)
        }
    }
}

//private val credentials: Any
//private val credentials: Any
actual val platformGoogleClientId: String = AppConfig.GOOGLE_WEB_CLIENT_ID
