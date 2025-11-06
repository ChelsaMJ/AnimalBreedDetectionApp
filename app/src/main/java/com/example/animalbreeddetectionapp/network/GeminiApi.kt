package com.example.animalbreeddetectionapp.network

import android.util.Log
import com.example.animalbreeddetectionapp.utils.Constants
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

object GeminiApi {

    private val client = OkHttpClient()

    /**
     * Existing image-based analysis (keeps your implementation)
     */
    fun analyzeBreed(imageBase64: String, onResult: (String) -> Unit, onError: (String) -> Unit) {
        val promptText = """
            You are an animal expert.
            Identify the animal breed from this image and respond in 3 clear points:
            1️⃣ Breed Name
            2️⃣ Short Description
            3️⃣ Care Tips
        """.trimIndent()

        val imageObject = JSONObject()
            .put("inline_data", JSONObject()
                .put("mime_type", "image/jpeg")
                .put("data", imageBase64)
            )

        val textObject = JSONObject().put("text", promptText)

        val partsArray = JSONArray()
            .put(textObject)
            .put(imageObject)

        val contentObject = JSONObject().put("parts", partsArray)
        val contentsArray = JSONArray().put(contentObject)
        val bodyJson = JSONObject().put("contents", contentsArray)

        val body = RequestBody.create(
            MediaType.parse("application/json"),
            bodyJson.toString()
        )

        val request = Request.Builder()
            .url(Constants.GEMINI_API_URL)
            .addHeader("Content-Type", "application/json")
            .addHeader("x-goog-api-key", Constants.GEMINI_API_KEY)
            .post(body)
            .build()

        Log.d("GeminiApi", "Sending image analysis request to Gemini...")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("GeminiApi", "Network Error: ${e.message}")
                onError("Network Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body()?.string()
                Log.d("GeminiApi", "Raw Response: $res")

                if (!response.isSuccessful || res.isNullOrEmpty()) {
                    onError("Empty or invalid response from AI.")
                    return
                }

                try {
                    val jsonRes = JSONObject(res)
                    val candidates = jsonRes.optJSONArray("candidates")
                    if (candidates == null || candidates.length() == 0) {
                        onError("No candidates in AI response.")
                        return
                    }

                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    var resultText = ""

                    if (parts != null) {
                        for (i in 0 until parts.length()) {
                            val part = parts.getJSONObject(i)
                            val text = part.optString("text", "")
                            if (text.isNotBlank()) resultText += text + "\n"
                        }
                    }

                    if (resultText.isNotBlank()) {
                        onResult(resultText.trim())
                    } else {
                        onError("AI did not return text content.")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    onError("Failed to parse AI response.")
                }
            }
        })
    }

    /**
     * NEW: Send a natural language prompt (text only) to Gemini and return the text response.
     * onResult -> AI reply (string)
     */
    fun generateText(prompt: String, onResult: (String) -> Unit, onError: (String) -> Unit) {
        val textObject = JSONObject().put("text", prompt)
        val partsArray = JSONArray().put(textObject)
        val contentObject = JSONObject().put("parts", partsArray)
        val contentsArray = JSONArray().put(contentObject)
        val bodyJson = JSONObject().put("contents", contentsArray)

        val body = RequestBody.create(
            MediaType.parse("application/json"),
            bodyJson.toString()
        )

        val request = Request.Builder()
            .url(Constants.GEMINI_API_URL)
            .addHeader("Content-Type", "application/json")
            .addHeader("x-goog-api-key", Constants.GEMINI_API_KEY)
            .post(body)
            .build()

        Log.d("GeminiApi", "Sending text prompt to Gemini...")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("GeminiApi", "Network Error: ${e.message}")
                onError("Network Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body()?.string()
                Log.d("GeminiApi", "Raw Response: $res")

                if (!response.isSuccessful || res.isNullOrEmpty()) {
                    onError("Empty or invalid response from AI.")
                    return
                }

                try {
                    val jsonRes = JSONObject(res)
                    val candidates = jsonRes.optJSONArray("candidates")
                    if (candidates == null || candidates.length() == 0) {
                        onError("No candidates in AI response.")
                        return
                    }

                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    var resultText = ""

                    if (parts != null) {
                        for (i in 0 until parts.length()) {
                            val part = parts.getJSONObject(i)
                            val text = part.optString("text", "")
                            if (text.isNotBlank()) resultText += text + "\n"
                        }
                    }

                    if (resultText.isNotBlank()) {
                        onResult(resultText.trim())
                    } else {
                        onError("AI did not return text content.")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    onError("Failed to parse AI response.")
                }
            }
        })
    }
}
