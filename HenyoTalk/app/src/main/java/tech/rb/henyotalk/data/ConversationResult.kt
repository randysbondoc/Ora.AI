package com.rbtech.henyotalk.data

// A sealed interface is a great way to represent restricted states.
// A result can only be Success or Error.
sealed interface ConversationResult {
    data class Success(val text: String) : ConversationResult
    data class Error(val message: String) : ConversationResult
}