package com.rbtech.henyotalk.data

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

// This is now a class that can be injected.
class GeminiService @Inject constructor(
    @Named("pinoyModel") private val pinoyModel: GenerativeModel,
    @Named("expertModel") private val expertModel: GenerativeModel
) {
    /**
     * Generates a response from a specific AI persona.
     * @param persona "Pinoy" or "Expert" to select the correct model.
     * @param prompt The text prompt for the model.
     * @param history The previous conversation turns.
     * @return A ConversationResult which is either Success or Error.
     */
    suspend fun getAiResponse(
        persona: String,
        prompt: String,
        history: List<Content>
    ): ConversationResult {
        // Choose the model based on the persona
        val model = when (persona) {
            "Pinoy" -> pinoyModel
            else -> expertModel
        }

        // Run the network request on a background thread
        return withContext(Dispatchers.IO) {
            try {
                // The chat history is built from previous messages
                val chat = model.startChat(history)
                val response: GenerateContentResponse = chat.sendMessage(prompt)

                response.text?.let {
                    ConversationResult.Success(it)
                } ?: ConversationResult.Error("Received an empty response.")

            } catch (e: Exception) {
                // Return a structured error
                ConversationResult.Error("API Error: ${e.message}")
            }
        }
    }
}