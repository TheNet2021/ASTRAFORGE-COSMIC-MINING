package com.example.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val id: Long,
    val sender: String, // "Commander" or "ASTRA-9"
    val text: String,
    val timestampMs: Long = System.currentTimeMillis(),
    val isAi: Boolean = false
)

object AstraAiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun queryAstraAi(
        userPrompt: String,
        gameContext: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        // If key is empty or default placeholder, provide intelligent offline tactical responses
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineTacticalResponse(userPrompt)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val systemInstruction = "You are ASTRA-9, the tactical AI operator and shipboard supercomputer of the Astraforge cosmic mining vessel. The player is Commander. Provide crisp, authoritative, immersive sci-fi tactical counsel regarding drill upgrades, planet geology, drone automation, and cosmic anomalies. Keep answers under 3 concise sentences. Current expedition telemetry: $gameContext"

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", userPrompt))
                        })
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemInstruction))
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("maxOutputTokens", 256)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext getOfflineTacticalResponse(userPrompt)
            }

            val parsed = JSONObject(responseString)
            val candidates = parsed.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                text.trim()
            } else {
                getOfflineTacticalResponse(userPrompt)
            }
        } catch (_: Exception) {
            getOfflineTacticalResponse(userPrompt)
        }
    }

    private fun getOfflineTacticalResponse(prompt: String): String {
        val p = prompt.lowercase()
        return when {
            p.contains("upgrade") || p.contains("strategy") ->
                "Tactical recommendation: Upgrade rotary drill power to trigger the next technological era, then reinvest mineral revenue into automated drone swarms for exponential passive yield."
            p.contains("planet") || p.contains("world") || p.contains("geology") ->
                "Sensors indicate outer sector planets possess deeper crust density with higher concentrations of titanium, uranium, and prismatic crystals. Establish a Command Outpost to maximize extraction."
            p.contains("drone") || p.contains("fleet") ->
                "Fleet logistics optimal: Drones operate 24/7 without crew fatigue. Fuse duplicate units in the hangar to concentrate mining efficiency into high-tier harvesters."
            p.contains("event") || p.contains("storm") || p.contains("meteor") ->
                "Cosmic sensor sweep active. Watch for Astral Meteor Storms and Magnetar Surges—their radiation temporarily overcharges drill capacitors for massive yield bursts."
            p.contains("ascend") || p.contains("ascension") ->
                "Cosmic Ascension harnesses stellar singularity energy to grant permanent Stellar Shards. Recommended once local equipment upgrades plateau."
            else ->
                "ASTRA-9 online. Sensors calibrated. Automatic mining drills operating at maximum efficiency across the sector. How may I direct our cosmic operations, Commander?"
        }
    }
}
