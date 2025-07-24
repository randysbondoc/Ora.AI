package com.rbtech.henyotalk.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rbtech.henyotalk.HenyoTalkUiState
import com.rbtech.henyotalk.Speaker // <-- Import the Speaker enum
import com.rbtech.henyotalk.ui.components.GlowingOrb
import com.rbtech.henyotalk.ui.theme.HenyoTalkTheme

@Composable
fun ConversationScreen(uiState: HenyoTalkUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "HenyoTalks", // Corrected App Name
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                GlowingOrb(
                    // Use theme colors and correct comparison
                    color = if (uiState.activeSpeaker == Speaker.PINOY) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                    isSpeaking = uiState.activeSpeaker == Speaker.PINOY,
                    isListening = uiState.activeSpeaker == Speaker.EXPERT
                )
                Spacer(Modifier.height(16.dp))
                Text("Pinoy", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                GlowingOrb(
                    // Use theme colors and correct comparison
                    color = if (uiState.activeSpeaker == Speaker.EXPERT) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
                    isSpeaking = uiState.activeSpeaker == Speaker.EXPERT,
                    isListening = uiState.activeSpeaker == Speaker.PINOY
                )
                Spacer(Modifier.height(16.dp))
                Text("Subject Expert", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
            }
        }

        Text(
            text = uiState.displayedText,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .heightIn(min = 120.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ConversationScreenPreview() {
    HenyoTalkTheme {
        // Use the correct Speaker enum for the preview
        ConversationScreen(uiState = HenyoTalkUiState(activeSpeaker = Speaker.PINOY, displayedText = "This is a test message that is long enough to wrap to multiple lines."))
    }
}