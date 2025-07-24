package com.rbtech.henyotalk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import com.rbtech.henyotalk.data.ConversationResult
import com.rbtech.henyotalk.data.GeminiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Sealed event for one-off actions like speaking text
sealed class UiEvent {
    data class SpeakText(val text: String, val utteranceId: String) : UiEvent()
}

// Represents the entire state of the conversation screen
data class HenyoTalkUiState(
    val activeSpeaker: Speaker? = null,
    val displayedText: String = "Setup your conversation to begin.",
    val isConversationRunning: Boolean = false
)

// Enum to make speaker identification type-safe
enum class Speaker(val id: String) {
    PINOY("Pinoy"),
    EXPERT("Expert")
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val geminiService: GeminiService // Hilt injects our service
) : ViewModel() {

    private val _uiState = MutableStateFlow(HenyoTalkUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventChannel = Channel<UiEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var conversationJob: Job? = null
    val ttsCompletionChannel = Channel<String>()

    fun startConversation(topic: String, language: String) {
        if (_uiState.value.isConversationRunning) return

        conversationJob = viewModelScope.launch {
            _uiState.update { it.copy(isConversationRunning = true) }
            val conversationHistory = mutableListOf<Content>()

            // The initial prompt for the Pinoy character, based on user's selected topic and language
            var nextPrompt = createInitialPrompt(topic, language)

            // Loop for a 5-turn conversation
            for (turn in 1..5) {
                if (!processTurn(Speaker.PINOY, nextPrompt, conversationHistory, turn)) break
                // The Pinoy's response becomes the prompt for the Expert
                nextPrompt = (uiState.value.displayedText)

                if (!processTurn(Speaker.EXPERT, nextPrompt, conversationHistory, turn)) break
                // The Expert's response becomes the prompt for the Pinoy
                nextPrompt = (uiState.value.displayedText)
            }

            _uiState.update { it.copy(activeSpeaker = null, displayedText = "Conversation Ended.") }
            _eventChannel.send(UiEvent.SpeakText("Conversation Ended.", "end_conversation"))
        }
    }

    private suspend fun processTurn(
        speaker: Speaker,
        prompt: String,
        history: MutableList<Content>,
        turn: Int
    ): Boolean {
        _uiState.update { it.copy(activeSpeaker = speaker, displayedText = "${speaker.id} is thinking...") }

        when (val result = geminiService.getAiResponse(speaker.id, prompt, history)) {
            is ConversationResult.Success -> {
                val responseText = result.text
                _uiState.update { it.copy(displayedText = responseText) }
                _eventChannel.send(UiEvent.SpeakText(responseText, "${speaker.id}_turn_$turn"))
                ttsCompletionChannel.receive() // Wait for TTS to finish speaking

                // Add the turn to history for context
                history.add(content("user") { text(prompt) })
                history.add(content("model") { text(responseText) })
                return true
            }
            is ConversationResult.Error -> {
                _uiState.update { it.copy(activeSpeaker = null, displayedText = result.message) }
                return false // Stop conversation on error
            }
        }
    }

    private fun createInitialPrompt(topic: String, language: String): String {
        val promptInLanguage = when (language) {
            "Tagalog" -> "Simulan natin ang usapan. Ano ang unang tanong mo tungkol sa $topic?"
            "Bisaya" -> "Sugdan nato ang istorya. Unsa imong unang pangutana bahin sa $topic?"
            else -> "Let's start the conversation. What is your first question about $topic?"
        }
        return "You are Pinoy. The topic is '$topic'. The conversation language is $language. $promptInLanguage"
    }
}