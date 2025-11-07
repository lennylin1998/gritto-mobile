Sign in with Google helps you quickly integrate user authentication with your Android app. Users can use their Google Account to sign in to your app, provide consent, and securely share their profile information with your app. Android's Credential Manager Jetpack library makes this integration smooth, offering a consistent experience across Android devices using a single API.

This document guides you through implementing Sign in with Google in Android apps, how you can set up the Sign in with Google button UI, and configuring app-optimized one tap sign-up and sign-in experiences. For smooth device migration, Sign in with Google supports auto sign-in, and its cross-platform nature across Android, iOS, and web surfaces helps you provide sign-in access for your app on any device. If you use Firebase Authentication for your application, you can learn more about integrating Sign in with Google and Credential Manager in their Authenticate with Google on Android guide.

Note: For authorization actions needed to access data stored in the Google Account such as Google Drive, use the AuthorizationClient API.
To set up Sign in with Google, follow these two main steps:

Configure Sign in with Google as an option for Credential Manager's bottom sheet UI. This can be configured to automatically prompt the user to sign in. If you have implemented either passkeys or passwords, you can request all relevant credential types simultaneously, so that the user does not have to remember the option they've used previously to sign in.

Credential Manager bottom sheet
Figure 1. The Credential Manager bottomsheet credential selection UI
Add the Sign in with Google button to your app's UI. The Sign in with Google button offers a streamlined way for users to use their existing Google Accounts to sign up or sign in to Android apps. Users will click the Sign in with Google button if they dismiss the bottom sheet UI, or if they explicitly want to use their Google Account for sign up and sign in. For developers, this means easier user onboarding and reduced friction during sign-up.

Animation showing the Sign in with Google flow
Figure 2. The Credential Manager Sign in with Google button UI
This document explains how to integrate the Sign in with Google button and bottom sheet dialog with the Credential Manager API using the Google ID helper library.

Set up your Google Cloud Console project
Open your project in the Cloud Console, or create a project if you don't already have one.
On the Branding page, make sure all of the information is complete and accurate.
Make sure your app has a correct App Name, App Logo, and App Homepage assigned. These values will be presented to users on the Sign in with Google consent screen on sign up and the Third-party apps & services screen.
Make sure you have specified the URLs of your app's privacy policy and terms of service.
In the Clients page, create an Android client ID for your app if you don't already have one. You will need to specify your app's package name and SHA-1 signature.
Go to the Clients page.
Click Create client.
Select the Android application type.
Enter a name for the OAuth client. This name is displayed on your project's Clients page to identify the client.
Enter the package name of your Android app. This value is defined in the package attribute of the <manifest> element in your AndroidManifest.xml file.
Enter the SHA-1 signing certificate fingerprint of the app distribution.
If your app uses app signing by Google Play, copy the SHA-1 fingerprint from the app signing page of the Play Console.
If you manage your own keystore and signing keys, use the keytool utility included with Java to print certificate information in a human-readable format. Copy the SHA-1 value in the Certificate fingerprints section of the keytool output. See Authenticating Your Client in the Google APIs for Android documentation for more information.
(Optional) Verify ownership of your Android application.
In the Clients page, create a new "Web application" client ID if you haven't already. You can ignore the "Authorized JavaScript Origins" and "Authorized redirect URIs" fields for now. This client ID will be used to identify your backend server when it communicates with Google's authentication services.
Go to the Clients page.
Click Create client.
Select the Web application type.
Verify app ownership
You can verify ownership of your application to reduce the risk of app impersonation.

Note: Android app ownership verification is only available for Google Play apps.
To complete the verification process, you can use your Google Play Developer Account if you have one and your app is registered on the Google Play Console. The following requirements must be met for a successful verification:

You must have a registered application in the Google Play Console with the same package name and SHA-1 signing certificate fingerprint as the Android OAuth client you are completing the verification for.
You must have Admin permission for the app in the Google Play Console. Learn more about access management in the Google Play Console.
In the Verify App Ownership section of the Android client, click the Verify Ownership button to complete the verification process.

If the verification is successful, a notification will be displayed to confirm the success of the verification process. Otherwise, an error prompt will be shown.

To fix a failed verification, try the following:

Make sure the app you are verifying is a registered app in the Google Play Console.
Make sure you have Admin permission for the app in the Google Play Console.
Declare dependencies
Add the following dependencies to your app module's build script- make sure to replace <latest version> with the latest version of the googleid library:

Kotlin
Groovy

dependencies {
implementation("androidx.credentials:credentials:1.6.0-beta03")
implementation("androidx.credentials:credentials-play-services-auth:1.6.0-beta03")
implementation("com.google.android.libraries.identity.googleid:googleid:<latest version>")
}
Instantiate a Google sign-in request
To begin your implementation, instantiate a Google sign-in request. Use GetGoogleIdOption to retrieve a user's Google ID Token.


val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
.setFilterByAuthorizedAccounts(true)
.setServerClientId(WEB_CLIENT_ID)
.setAutoSelectEnabled(true)
// nonce string to use when generating a Google ID token
.setNonce(nonce)
.build()
First, check if the user has any accounts that have previously been used to sign in to your app by calling the API with the setFilterByAuthorizedAccounts parameter set to true. Users can choose between available accounts to sign in.

If no authorized Google Accounts are available, the user should be prompted to sign up with any of their available accounts. To do this, prompt the user by calling the API again and setting setFilterByAuthorizedAccounts to false. Learn more about sign up.

Enable automatic sign-in for returning users (recommended)
Developers should enable automatic sign-in for users who register with their single account. This provides a seamless experience across devices, especially during device migration, where users can quickly regain access to their account without re-entering credentials. For your users, this removes unnecessary friction when they were already previously signed in.

To enable automatic sign-in, use setAutoSelectEnabled(true). Automatic sign in is only possible when the following criteria are met:

There is a single credential matching the request, which can be a Google Account or a password, and this credential matches the default account on the Android-powered device.
The user has not explicitly signed out.
The user hasn't disabled automatic sign-in in their Google Account settings.

val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
.setFilterByAuthorizedAccounts(true)
.setServerClientId(WEB_CLIENT_ID)
.setAutoSelectEnabled(true)
// nonce string to use when generating a Google ID token
.setNonce(nonce)
.build()
Remember to correctly handle sign-out when implementing automatic sign-in, so that users can always choose the proper account after they explicitly sign out of your app.

Set a nonce to improve security
To improve sign-in security and avoid replay attacks, add setNonce to include a nonce in each request. Learn more about generating a nonce.


val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
.setFilterByAuthorizedAccounts(true)
.setServerClientId(WEB_CLIENT_ID)
.setAutoSelectEnabled(true)
// nonce string to use when generating a Google ID token
.setNonce(nonce)
.build()
Create the Sign in with Google flow
The steps to set up a Sign in with Google flow are as follows:

Instantiate a GetCredentialRequest, then add the previously created googleIdOption using addCredentialOption() to retrieve the credentials.
Pass this request to getCredential() (Kotlin) or getCredentialAsync() (Java) call to retrieve the user's available credentials.
Once the API is successful, extract the CustomCredential which holds the result for GoogleIdTokenCredential data.
The type for CustomCredential should be equal to the value of GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL. Convert the object into a GoogleIdTokenCredential using the GoogleIdTokenCredential.createFrom method.
If the conversion succeeds, extract the GoogleIdTokenCredential ID, validate it, and authenticate the credential on your server.

If the conversion fails with a GoogleIdTokenParsingException, then you may need to update your Sign in with Google library version.

Catch any unrecognized custom credential types.


val request: GetCredentialRequest = GetCredentialRequest.Builder()
.addCredentialOption(googleIdOption)
.build()

coroutineScope {
try {
val result = credentialManager.getCredential(
request = request,
context = activityContext,
)
handleSignIn(result)
} catch (e: GetCredentialException) {
// Handle failure
}
}

fun handleSignIn(result: GetCredentialResponse) {
// Handle the successfully returned credential.
val credential = result.credential
val responseJson: String

    when (credential) {

        // Passkey credential
        is PublicKeyCredential -> {
            // Share responseJson such as a GetCredentialResponse to your server to validate and
            // authenticate
            responseJson = credential.authenticationResponseJson
        }

        // Password credential
        is PasswordCredential -> {
            // Send ID and password to your server to validate and authenticate.
            val username = credential.id
            val password = credential.password
        }

        // GoogleIdToken credential
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    // Use googleIdTokenCredential and extract the ID to validate and
                    // authenticate on your server.
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)
                    // You can use the members of googleIdTokenCredential directly for UX
                    // purposes, but don't use them to store or control access to user
                    // data. For that you first need to validate the token:
                    // pass googleIdTokenCredential.getIdToken() to the backend server.
                    // see [validation instructions](https://developers.google.com/identity/gsi/web/guides/verify-google-id-token)
                } catch (e: GoogleIdTokenParsingException) {
                    Log.e(TAG, "Received an invalid google id token response", e)
                }
            } else {
                // Catch any unrecognized custom credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }

        else -> {
            // Catch any unrecognized credential type here.
            Log.e(TAG, "Unexpected type of credential")
        }
    }
}
Trigger a Sign in with Google button flow
To trigger the Sign in with Google button flow, use GetSignInWithGoogleOption instead of GetGoogleIdOption:


val signInWithGoogleOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption.Builder(
serverClientId = WEB_CLIENT_ID
).setNonce(nonce)
.build()
Note: This GetSignInWithGoogleOption must be the only option in the GetCredentialRequest.
Handle the returned GoogleIdTokenCredential as described in the following code example.


fun handleSignInWithGoogleOption(result: GetCredentialResponse) {
// Handle the successfully returned credential.
val credential = result.credential

    when (credential) {
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    // Use googleIdTokenCredential and extract id to validate and
                    // authenticate on your server.
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)
                } catch (e: GoogleIdTokenParsingException) {
                    Log.e(TAG, "Received an invalid google id token response", e)
                }
            } else {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }

        else -> {
            // Catch any unrecognized credential type here.
            Log.e(TAG, "Unexpected type of credential")
        }
    }
}
Once you instantiate the Google sign in request, launch the authentication flow in a similar manner as mentioned in the Sign in with Google section.

Enable sign-up for new users (recommended)
Sign in with Google is the easiest way for users to create a new account with your app or service in just a few taps.

If no saved credentials are found (no Google Accounts returned by getGoogleIdOption), prompt your user to sign up. First, check if setFilterByAuthorizedAccounts(true) to see if any previously used accounts exist. If none are found, prompt the user to sign up with their Google Account using setFilterByAuthorizedAccounts(false)

Example:


val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
.setFilterByAuthorizedAccounts(false)
.setServerClientId(WEB_CLIENT_ID)
.build()
Once you instantiate the Google sign up request, launch the authentication flow. If users don't want to use Sign in with Google for sign up, consider optimizing your app for autofill. Once your user has created an account, consider enrolling them in passkeys as a final step to account creation.

Handle sign-out
When a user signs out of your app, call the API clearCredentialState() method to clear the current user credential state from all credential providers. This will notify all credential providers that any stored credential session for the given app should be cleared.

A credential provider may have stored an active credential session and use it to limit sign-in options for future get-credential calls. For example, it may prioritize the active credential over any other available credential. When your user explicitly signs out of your app and in order to get the holistic sign-in options the next time, you should call this API to let the provider clear any stored credential session.