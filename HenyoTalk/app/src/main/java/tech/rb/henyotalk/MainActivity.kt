package com.rbtech.henyotalk

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.rbtech.henyotalk.ui.screens.ConversationScreen
import com.rbtech.henyotalk.ui.screens.SetupScreen
import com.rbtech.henyotalk.ui.theme.HenyoTalkTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint // Enable Hilt injection in this Activity
class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsInitialized by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)

        setContent {
            HenyoTalkTheme {
                // Use hiltViewModel() to get the ViewModel instance
                val viewModel: MainViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsState()
                var showConversationScreen by remember { mutableStateOf(false) }

                // This effect will launch only once when the composition is first created
                LaunchedEffect(Unit) {
                    viewModel.events.collectLatest { event ->
                        when (event) {
                            is UiEvent.SpeakText -> {
                                if (isTtsInitialized) {
                                    speak(event.text, event.utteranceId)
                                }
                            }
                        }
                    }
                }

                if (showConversationScreen) {
                    ConversationScreen(uiState = uiState)
                } else {
                    SetupScreen(
                        isTtsReady = isTtsInitialized,
                        onStart = { topic, language ->
                            // The API key is no longer needed here
                            viewModel.startConversation(topic, language)
                            showConversationScreen = true
                        }
                    )
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val viewModel: MainViewModel by viewModels()
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}

                // Called when TTS is finished speaking
                override fun onDone(utteranceId: String?) {
                    lifecycleScope.launch {
                        // Notify the ViewModel that speaking is complete
                        viewModel.ttsCompletionChannel.send(utteranceId ?: "")
                    }
                }

                @Deprecated("deprecated in API 21")
                override fun onError(utteranceId: String?) {
                    lifecycleScope.launch {
                        viewModel.ttsCompletionChannel.send(utteranceId ?: "")
                    }
                }
            })

            // Set language support. Note: Bisaya might not be supported on all devices.
            val tagalog = Locale("fil", "PH")
            if (tts?.isLanguageAvailable(tagalog) == TextToSpeech.LANG_AVAILABLE) {
                tts?.language = tagalog
            } else {
                tts?.language = Locale.US
            }

            isTtsInitialized = true // Signal that TTS is ready
        }
    }

    private fun speak(text: String, utteranceId: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.stop()
        tts?.shutdown()
    }
}