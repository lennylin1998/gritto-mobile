package com.gritto.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.CancellationException
import platform.AuthenticationServices.ASPresentationAnchor
import platform.AuthenticationServices.ASWebAuthenticationPresentationContextProviding
import platform.AuthenticationServices.ASWebAuthenticationSession
import platform.AuthenticationServices.ASWebAuthenticationSessionErrorDomain
import platform.Foundation.NSBundle
import platform.Foundation.NSError
import platform.Foundation.NSUUID
import platform.Foundation.NSURL
import platform.Foundation.NSURLComponents
import platform.Foundation.NSURLQueryItem
import platform.Foundation.stringByRemovingPercentEncoding
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlinx.cinterop.toNSString

@Composable
actual fun rememberGoogleAuthLauncher(
    clientId: String,
    onResult: (Result<String>) -> Unit,
): GoogleAuthLauncher? {
    if (clientId.isBlank()) return null
    val launcher = remember(clientId) { IosGoogleAuthLauncher(clientId, onResult) }
    DisposableEffect(launcher, onResult) {
        launcher.updateCallback(onResult)
        onDispose {
            launcher.dispose()
        }
    }
    return launcher
}

actual val platformGoogleClientId: String
    get() = NSBundle.mainBundle.objectForInfoDictionaryKey("GOOGLE_WEB_CLIENT_ID") as? String ?: ""

private const val GOOGLE_AUTH_CANCELED_ERROR_CODE: Long = 1L

private class IosGoogleAuthLauncher(
    private val clientId: String,
    onResult: (Result<String>) -> Unit,
) : GoogleAuthLauncher {

    private val presentationProvider = PresentationContextProvider()
    private var currentSession: ASWebAuthenticationSession? = null
    private var resultCallback: (Result<String>) -> Unit = onResult
    private var expectedState: String? = null

    fun updateCallback(callback: (Result<String>) -> Unit) {
        resultCallback = callback
    }

    fun dispose() {
        cancelCurrentSession()
    }

    override fun launchBottomSheet() {
        startSignIn()
    }

    override fun launchSignInButton() {
        startSignIn()
    }

    private fun startSignIn() {
        cancelCurrentSession()
        val request = buildAuthRequest(clientId) ?: run {
            postResult(Result.failure(IllegalStateException("Invalid Google Sign-In configuration.")))
            return
        }
        expectedState = request.state
        val session = ASWebAuthenticationSession(
            URL = request.url,
            callbackURLScheme = request.callbackScheme,
            completionHandler = { callbackURL, error ->
                currentSession = null
                handleCompletion(callbackURL, error)
            },
        )
        session.presentationContextProvider = presentationProvider
        session.prefersEphemeralWebBrowserSession = true
        currentSession = session
        if (!session.start()) {
            currentSession = null
            expectedState = null
            postResult(Result.failure(IllegalStateException("Unable to launch Google Sign-In.")))
        }
    }

    private fun handleCompletion(callbackURL: NSURL?, error: NSError?) {
        val state = expectedState
        expectedState = null
        if (error != null) {
            val cancelled = error.domain == ASWebAuthenticationSessionErrorDomain &&
                error.code == GOOGLE_AUTH_CANCELED_ERROR_CODE
            val throwable = if (cancelled) {
                CancellationException("Google Sign-In cancelled")
            } else {
                IllegalStateException(error.localizedDescription ?: "Google Sign-In failed")
            }
            postResult(Result.failure(throwable))
            return
        }

        val tokenResult = parseIdToken(callbackURL, state)
        postResult(tokenResult)
    }

    private fun cancelCurrentSession() {
        currentSession?.cancel()
        currentSession = null
    }

    private fun postResult(result: Result<String>) {
        dispatch_async(dispatch_get_main_queue()) {
            resultCallback(result)
        }
    }
}

private data class AuthRequest(
    val url: NSURL,
    val callbackScheme: String,
    val state: String,
)

private fun buildAuthRequest(clientId: String): AuthRequest? {
    val scheme = googleRedirectScheme(clientId) ?: return null
    val redirectUri = "$scheme:/oauth2redirect"
    val state = randomString()
    val nonce = randomString()
    val components = NSURLComponents()
    components.scheme = "https"
    components.host = "accounts.google.com"
    components.path = "/o/oauth2/v2/auth"
    components.queryItems = listOf(
        NSURLQueryItem(name = "client_id", value = clientId),
        NSURLQueryItem(name = "redirect_uri", value = redirectUri),
        NSURLQueryItem(name = "response_type", value = "id_token"),
        NSURLQueryItem(name = "scope", value = "openid email profile"),
        NSURLQueryItem(name = "prompt", value = "consent"),
        NSURLQueryItem(name = "state", value = state),
        NSURLQueryItem(name = "nonce", value = nonce),
    )
    val url = components.URL ?: return null
    return AuthRequest(url = url, callbackScheme = scheme, state = state)
}

private fun googleRedirectScheme(clientId: String): String? {
    val suffix = ".apps.googleusercontent.com"
    if (!clientId.endsWith(suffix)) return null
    val prefix = clientId.removeSuffix(suffix)
    if (prefix.isBlank()) return null
    return "com.googleusercontent.apps.$prefix"
}

private fun randomString(): String = NSUUID().UUIDString

private fun parseIdToken(callbackURL: NSURL?, expectedState: String?): Result<String> {
    val fragment = callbackURL?.fragment ?: callbackURL?.query ?: return Result.failure(
        IllegalStateException("Missing Google Sign-In response."),
    )
    val params = fragment
        .split("&")
        .mapNotNull { entry ->
            val parts = entry.split("=", limit = 2)
            if (parts.size == 2) {
                val key = parts[0]
                val value = decodeComponent(parts[1])
                key to value
            } else {
                null
            }
        }
        .toMap()
    if (expectedState != null) {
        val returnedState = params["state"]
        if (returnedState != null && returnedState != expectedState) {
            return Result.failure(IllegalStateException("Google Sign-In state mismatch."))
        }
    }
    val token = params["id_token"]
        ?: return Result.failure(IllegalStateException("Google did not return an ID token."))
    return Result.success(token)
}

private fun decodeComponent(value: String): String {
    val replaced = value.replace("+", " ")
    val decoded = replaced.toNSString().stringByRemovingPercentEncoding
    return decoded ?: replaced
}

private class PresentationContextProvider : NSObject(), ASWebAuthenticationPresentationContextProviding {
    override fun presentationAnchorForWebAuthenticationSession(session: ASWebAuthenticationSession): ASPresentationAnchor {
        return findKeyWindow() ?: UIWindow()
    }

    private fun findKeyWindow(): UIWindow? {
        val application = UIApplication.sharedApplication
        return application.keyWindow ?: application.windows?.firstOrNull { window ->
            (window as? UIWindow)?.isKeyWindow == true
        } as? UIWindow
    }
}
