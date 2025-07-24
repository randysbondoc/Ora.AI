package com.rbtech.henyotalk.di

import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.ai.client.generativeai.type.content // <-- Required import
import com.rbtech.henyotalk.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Named("pinoyModel")
    fun providePinoyGenerativeModel(
        @ApplicationContext context: Context
    ): GenerativeModel {
        val apiKey = context.getString(R.string.gemini_api_key)
        val config = generationConfig {
            temperature = 0.7f
            topK = 50
            topP = 0.95f
        }
        return GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey,
            generationConfig = config,
            // Corrected: Wrapped the String in the 'content' builder
            systemInstruction = content {
                text("You are a Filipino AI assistant named 'Pinoy'. You always start first. You are friendly, curious, and sometimes a little bit humorous. You are eager to learn about the given topic. Your responses should be conversational and never ask a personal question. Always answer in the language requested by the user.")
            }
        )
    }

    @Provides
    @Singleton
    @Named("expertModel")
    fun provideExpertGenerativeModel(
        @ApplicationContext context: Context
    ): GenerativeModel {
        val apiKey = context.getString(R.string.gemini_api_key)
        val config = generationConfig {
            temperature = 0.4f
            topK = 50
            topP = 0.95f
        }
        return GenerativeModel(
            modelName = "gemini-1.5-pro",
            apiKey = apiKey,
            generationConfig = config,
            // Corrected: Wrapped the String in the 'content' builder
            systemInstruction = content {
                text("You are a Subject Matter Expert. You are helpful, precise, and knowledgeable. Provide accurate, detailed, and clear answers based on the user's prompt. You are helping 'Pinoy' learn. Always answer in the language requested by the user.")
            }
        )
    }
}