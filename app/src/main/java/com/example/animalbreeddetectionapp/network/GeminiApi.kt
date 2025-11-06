package com.example.animalbreeddetectionapp.network

import android.util.Log
import com.example.animalbreeddetectionapp.utils.Constants
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

object GeminiApi {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .build()

    /**
     * Image-based analysis (same public signature)
     */
    fun analyzeBreed(imageBase64: String, onResult: (String) -> Unit, onError: (String) -> Unit) {
        val promptText = """
You are an animal breed identification expert.

Analyze the given image and return a **short, clean, and concise summary** 
of the most likely breed or species.

Your response must follow this exact simple format (no numbering, no emojis, no markdown):

Breed Name: <breed name>
Scientific Name: <scientific name, if known>
Description: <one short paragraph describing key features and behavior>
Care Tips: <one short line with basic care tip>

Keep everything under 5 lines. Avoid unnecessary words, emojis, or numbering.
""".trimIndent()

        val imageObject = JSONObject()
            .put("inline_data", JSONObject()
                .put("mime_type", "image/jpeg")
                .put("data", imageBase64)
            )

        val textObject = JSONObject().put("text", promptText)
        val partsArray = JSONArray().put(textObject).put(imageObject)
        val contentObject = JSONObject().put("parts", partsArray)
        val contentsArray = JSONArray().put(contentObject)
        val bodyJson = JSONObject().put("contents", contentsArray)

        // Modern Kotlin extension
        val mediaType = "application/json".toMediaTypeOrNull()
        val body = bodyJson.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(Constants.GEMINI_API_URL)
            .addHeader("Content-Type", "application/json")
            .addHeader("x-goog-api-key", Constants.GEMINI_API_KEY)   // <-- IMPORTANT: use x-goog-api-key
            .post(body)
            .build()

        Log.d("GeminiApi", "Sending image analysis request to Gemini...")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val msg = "Network Error: ${e.message}"
                Log.e("GeminiApi", msg, e)
                onError(msg)
            }

            override fun onResponse(call: Call, response: Response) {
                val raw = response.body?.string()
                Log.d("GeminiApi", "Raw Response: $raw")

                if (!response.isSuccessful) {
                    val snippet = raw?.take(1200) ?: "empty body"
                    val errMsg = "AI call failed: code=${response.code} body=$snippet"
                    Log.e("GeminiApi", errMsg)
                    onError(errMsg)
                    return
                }

                if (raw.isNullOrEmpty()) {
                    onError("AI returned empty body")
                    return
                }

                try {
                    val jsonRes = JSONObject(raw)
                    val candidates = jsonRes.optJSONArray("candidates")
                    var resultText = ""

                    if (candidates != null && candidates.length() > 0) {
                        val candidate = candidates.getJSONObject(0)
                        val content = candidate.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null) {
                            for (i in 0 until parts.length()) {
                                val part = parts.getJSONObject(i)
                                val text = part.optString("text", "")
                                if (text.isNotBlank()) resultText += text + "\n"
                            }
                        }
                    } else {
                        // If structure differs, return raw body for debugging
                        resultText = raw
                    }

                    if (resultText.isNotBlank()) {
                        onResult(resultText.trim())
                    } else {
                        onError("AI did not return text content.")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    onError("Failed to parse AI response: ${e.message}")
                }
            }
        })
    }

    /**
     * Text prompt -> Gemini generate (uses same x-goog-api-key header)
     */
    fun generateText(prompt: String, onResult: (String) -> Unit, onError: (String) -> Unit) {
        val textObject = JSONObject().put("text", prompt)
        val partsArray = JSONArray().put(textObject)
        val contentObject = JSONObject().put("parts", partsArray)
        val contentsArray = JSONArray().put(contentObject)
        val bodyJson = JSONObject().put("contents", contentsArray)

        val mediaType = "application/json".toMediaTypeOrNull()
        val body = bodyJson.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(Constants.GEMINI_API_URL)
            .addHeader("Content-Type", "application/json")
            .addHeader("x-goog-api-key", Constants.GEMINI_API_KEY)   // <-- use x-goog-api-key
            .post(body)
            .build()

        Log.d("GeminiApi", "Sending text prompt to Gemini...")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val msg = "Network Error: ${e.message}"
                Log.e("GeminiApi", msg, e)
                onError(msg)
            }

            override fun onResponse(call: Call, response: Response) {
                val raw = response.body?.string()
                Log.d("GeminiApi", "Raw Response: $raw")

                if (!response.isSuccessful) {
                    val snippet = raw?.take(1200) ?: "empty body"
                    val errMsg = "AI call failed: code=${response.code} body=$snippet"
                    Log.e("GeminiApi", errMsg)
                    onError(errMsg)
                    return
                }

                if (raw.isNullOrEmpty()) {
                    onError("AI returned empty body")
                    return
                }

                try {
                    val jsonRes = JSONObject(raw)
                    val candidates = jsonRes.optJSONArray("candidates")
                    var resultText = ""

                    if (candidates != null && candidates.length() > 0) {
                        val candidate = candidates.getJSONObject(0)
                        val content = candidate.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null) {
                            for (i in 0 until parts.length()) {
                                val part = parts.getJSONObject(i)
                                val text = part.optString("text", "")
                                if (text.isNotBlank()) resultText += text + "\n"
                            }
                        }
                    } else {
                        // fallback to raw response if structure different
                        resultText = raw
                    }

                    if (resultText.isNotBlank()) {
                        onResult(resultText.trim())
                    } else {
                        onError("AI did not return text content.")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    onError("Failed to parse AI response: ${e.message}")
                }
            }
        })
    }
}
