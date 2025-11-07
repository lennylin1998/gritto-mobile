package com.gritto.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingScreen(
    googleSignInAvailable: Boolean,
    onGoogleSignInClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            )
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Welcome to Gritto",
                style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Turn ambitions into structured plans with your AI accountability partner.",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.85f)),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                    textAlign = TextAlign.Center,
                )
            }
            Surface(
                shadowElevation = 10.dp,
                shape = MaterialTheme.shapes.large
            ) {
                Button(
                    onClick = onGoogleSignInClick,
                    modifier = Modifier.padding(horizontal = 4.dp),
                    shape = MaterialTheme.shapes.medium,
                    enabled = googleSignInAvailable && !isLoading,
                ) {
                    GoogleGlyph()
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = when {
                            isLoading -> "Connecting…"
                            googleSignInAvailable -> "Continue with Google"
                            else -> "Google Sign-In Unavailable"
                        },
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
            if (!googleSignInAvailable) {
                Text(
                    text = "Google Sign-In isn’t available on this platform yet.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f)),
                    textAlign = TextAlign.Center,
                )
            }
            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            }
        }
        Text(
            text = "Built for the Google Cloud Run Hackathon",
            style = MaterialTheme.typography.labelMedium.copy(color = Color.White.copy(alpha = 0.7f)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

@Composable
private fun GoogleGlyph() {
    Surface(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.White),
        color = Color.Transparent
    ) {
        Text(
            text = "G",
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color(0xFF4285F4),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
