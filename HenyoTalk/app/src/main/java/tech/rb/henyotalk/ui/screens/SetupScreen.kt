package com.rbtech.henyotalk.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rbtech.henyotalk.ui.theme.HenyoTalkTheme

@Composable
fun SetupScreen(
    isTtsReady: Boolean,
    // The lambda no longer needs the apiKey parameter
    onStart: (topic: String, language: String) -> Unit
) {
    var topic by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("English") }
    val languages = listOf("English", "Tagalog", "Bisaya")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "HenyoTalks Setup",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = topic,
            onValueChange = { topic = it },
            label = { Text("Enter a Topic") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))

        // API Key field is removed for security and better UX.

        Text(text = "Select a Language:", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            languages.forEach { language ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (language == selectedLanguage),
                        onClick = { selectedLanguage = language }
                    )
                    Text(
                        text = language,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 4.dp, end = 16.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onStart(topic, selectedLanguage) },
            enabled = topic.isNotBlank() && isTtsReady
        ) {
            if (isTtsReady) {
                Text("Start Conversation")
            } else {
                Text("Initializing Audio...")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SetupScreenPreview() {
    HenyoTalkTheme {
        SetupScreen(isTtsReady = true, onStart = { _, _ -> })
    }
}