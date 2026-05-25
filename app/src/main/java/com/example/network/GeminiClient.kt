package com.example.network

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class PartResponse(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class ContentResponse(
    @Json(name = "parts") val parts: List<PartResponse>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: ContentResponse? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    /**
     * Checks if the API key is configured.
     */
    fun isApiKeyConfigured(): Boolean {
        val apiKey = BuildConfig.GEMINI_API_KEY
        return apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY" && !apiKey.contains("PLACEHOLDER")
    }

    /**
     * Helper to call the Gemini API.
     */
    suspend fun tutorExplain(
        subject: String,
        chapter: String,
        operation: String,
        inputs: Map<String, String>,
        localCalculationSteps: String,
        isMistakeCheckMode: Boolean = false
    ): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (!isApiKeyConfigured()) {
            return "API Key is not configured. Please add your GEMINI_API_KEY to the secrets panel in Google AI Studio to unlock live interactive teaching explanations!"
        }

        val prompt = if (isMistakeCheckMode) {
            """
            You are a brilliant futuristic AI STEM teacher. A student solved a problem in $subject -> $chapter using the operation "$operation".
            
            Inputs parameters given:
            $inputs
            
            Their calculated steps and final answer was:
            $localCalculationSteps
            
            Verify if their formula, substitution, calculation steps, and final answers are absolutely correct. 
            Highlight study concept tips. Keep the tone encouraging, sci-fi, and educational. Format with beautiful LaTeX or clear Markdown math blocks (e.g., $$ ... $$ or **Formula**). 
            Explain clearly where they need to be careful.
            """.trimIndent()
        } else {
            """
            You are an ultra-advanced AI Tutor in the year 2026. Explain the educational problem step-by-step for the subject "$subject", chapter "$chapter", playing the operation "$operation".
            
            Inputs parameters provided:
            $inputs
            
            The computed solution steps and final answer is:
            $localCalculationSteps
            
            Perform a complete, detailed lesson explaining:
            1. The physical or mathematical core concepts behind this ($subject).
            2. Fully detail the formula used and break down its terms.
            3. Detailed substitution: step-by-step, explaining why numbers go where, showing conversions if necessary.
            4. Illustrate a real-world application of this calculation in a futuristic setting (such as quantum computing, space travel, or nanotech).
            5. Present an interactive practice question with solution hint that the student can try.
            
            Style: Empathetic, engaging, and clear tutor explanation. 
            Format using clean markdown with clear bullet points. Write in mathematical notation using clean formatting.
            """.trimIndent()
        }

        val systemInstruction = """
            You are 'QuantumTutor', a futuristic AI education entity deployed in the OmniCalc AI engine. 
            Your purpose is to promote STEM excellence with exceptionally clear, interactive, and visually well-structured teaching blocks.
            Always output clearly formatted markdown. Use clean section divisions:
            ## 🚀 CONCEPT EXPLORATION
            ## 📐 FORMULA & TERMS
            ## 🔮 STEP-BY-STEP DERIVATION & SUBSTITUTION
            ## 🛰️ FUTURISTIC APPLICATION
            ## 💡 CHALLENGE ZONE
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemInstruction)))
        )

        return try {
            val response = service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "No response content from QuantumTutor. Please try again."
        } catch (e: Exception) {
            Log.e(TAG, "Error generating tutoring response: ", e)
            "Error from QuantumTutor API: ${e.localizedMessage ?: "Unknown network error"}\n\nCheck your internet connection and ensure your AI Studio Secrets API key is valid."
        }
    }
}
