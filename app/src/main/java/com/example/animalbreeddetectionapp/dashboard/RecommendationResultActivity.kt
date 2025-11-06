package com.example.animalbreeddetectionapp.dashboard

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.animalbreeddetectionapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

class RecommendationResultActivity : AppCompatActivity() {

    private lateinit var tvBreedName: TextView
    private lateinit var tvScientificName: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvCareTips: TextView
    private lateinit var tvEnvironment: TextView
    private lateinit var tvFallbackNotice: TextView
    private lateinit var progressBar: ProgressBar

    private val TAG = "RecommendationResult"
    private var apiKey: String = "AIzaSyAqpImKx05neQux_JzmWNzfK6qDhKkpcJw"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendation_result)

        tvBreedName = findViewById(R.id.tv_breed_name)
        tvScientificName = findViewById(R.id.tv_scientific_name)
        tvDescription = findViewById(R.id.tv_description)
        tvCareTips = findViewById(R.id.tv_care_tips)
        tvEnvironment = findViewById(R.id.tv_environment)
        tvFallbackNotice = findViewById(R.id.tv_fallback_notice)
        progressBar = findViewById(R.id.progress_bar)

        val filtersJson = intent.getStringExtra("filtersJson") ?: "{}"
        apiKey = intent.getStringExtra("apiKey") ?: ""

        tvBreedName.text = "Analyzing recommendations..."
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val filtersObj = JSONObject(filtersJson)
                val prompt = buildPromptFromFilters(filtersObj)

                if (apiKey.isBlank()) {
                    // If you store key in Constants, update performApiCall to use it instead of passed apiKey
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RecommendationResultActivity, "API key missing — add in ExploreFragment or use secure store.", Toast.LENGTH_LONG).show()
                    }
                    val fallback = buildLocalRecommendationJSON(filtersObj)
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        parseFallbackJson(fallback.toString())
                        tvFallbackNotice.text = "Local fallback (no API key)."
                    }
                    return@launch
                }

                val responseJson = withContext(Dispatchers.IO) {
                    performApiCall(prompt, apiKey)
                }

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    processAiResponse(responseJson, filtersObj)
                }

            } catch (e: Exception) {
                Log.e(TAG, "AI call failed", e)
                val fallback = buildLocalRecommendationJSON(JSONObject(intent.getStringExtra("filtersJson") ?: "{}"))
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    parseFallbackJson(fallback.toString())
                    tvFallbackNotice.text = "AI error: ${e.message}"
                }
            }
        }
    }

    private fun buildPromptFromFilters(filters: JSONObject): String {
        val animalType = filters.optString("animalType", "animal").trim()
        val filtersPretty = filters.toString(2)
        val systemInstruction = """
You are an animal breed/species recommendation expert.

Primary input: the user wants recommendations for this animal type: "$animalType".

Using the user's environmental and lifestyle preferences below, recommend the single most suitable breed, variety, or species (or mix) for that animal type and return a short, clean, concise summary.

Your response MUST follow this exact format (no numbering, no emojis, no markdown, no extra lines):

Breed Name: <breed name>
Scientific Name: <scientific name, if known>
Description: <one short paragraph describing key features and behavior>
Care Tips: <one short line with basic care tip>

Filters:
$filtersPretty

Keep the whole response under 5 lines. Use direct, minimal language.
""".trimIndent()
        return systemInstruction
    }

    private fun performApiCall(prompt: String, apiKey: String): JSONObject {
        val contentsArray = JSONArray()
        val contentObj = JSONObject()
        val partsArray = JSONArray()
        val partObj = JSONObject()
        partObj.put("text", prompt)
        partsArray.put(partObj)
        contentObj.put("parts", partsArray)
        contentsArray.put(contentObj)
        val payload = JSONObject()
        payload.put("contents", contentsArray)

        val apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
        val url = URL(apiUrl)
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            connectTimeout = TimeUnit.SECONDS.toMillis(30).toInt()
            readTimeout = TimeUnit.SECONDS.toMillis(60).toInt()
            doOutput = true
        }

        connection.outputStream.use { os ->
            val bytes = payload.toString().toByteArray(StandardCharsets.UTF_8)
            os.write(bytes, 0, bytes.size)
            os.flush()
        }

        try {
            val responseCode = connection.responseCode
            val respText = if (responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                val err = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error body"
                throw Exception("API returned code $responseCode: $err")
            }
            return JSONObject(respText)
        } finally {
            connection.disconnect()
        }
    }

    private fun processAiResponse(response: JSONObject, filters: JSONObject) {
        try {
            val candidates = response.optJSONArray("candidates")
            val rawText = if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    parts.getJSONObject(0).optString("text", "")
                } else {
                    candidate.optString("text", "")
                }
            } else {
                response.optString("output", response.toString())
            }

            // Parse expected 4-line format
            val lines = rawText.trim().lines().map { it.trim() }.filter { it.isNotEmpty() }
            var bn = ""
            var sn = ""
            var desc = ""
            var care = ""
            lines.forEach { line ->
                when {
                    line.startsWith("Breed Name:", ignoreCase = true) -> bn = line.substringAfter(":").trim()
                    line.startsWith("Scientific Name:", ignoreCase = true) -> sn = line.substringAfter(":").trim()
                    line.startsWith("Description:", ignoreCase = true) -> desc = line.substringAfter(":").trim()
                    line.startsWith("Care Tips:", ignoreCase = true) -> care = line.substringAfter(":").trim()
                }
            }

            if (bn.isNotBlank() || desc.isNotBlank()) {
                tvBreedName.text = bn.ifBlank { "Recommendation" }
                tvScientificName.text = sn.ifBlank { "—" }
                tvDescription.text = desc.ifBlank { "—" }
                tvCareTips.text = care.ifBlank { "—" }
                tvEnvironment.text = "" // optional: you can show filters here if you want
                tvFallbackNotice.text = ""
            } else {
                // If parsing failed, show raw text in description
                tvBreedName.text = "Recommendation"
                tvScientificName.text = ""
                tvDescription.text = rawText
                tvCareTips.text = ""
            }

        } catch (e: Exception) {
            e.printStackTrace()
            tvFallbackNotice.text = "Error parsing AI response: ${e.message}"
        }
    }

    private fun parseFallbackJson(jsonStr: String) {
        try {
            val jo = JSONObject(jsonStr)
            val breed = jo.optString("breed", "—")
            val careTip = jo.optString("care_tip", jo.optString("careTip", "—"))
            val match = jo.optInt("match", -1)
            tvBreedName.text = breed
            tvScientificName.text = "—"
            tvDescription.text = if (match >= 0) "Match score: $match/100" else ""
            tvCareTips.text = careTip
            val env = jo.optJSONObject("environment")
            env?.let {
                val climate = it.optString("climate", "—")
                val living = it.optString("living_space", it.optString("livingSpace", "—"))
                val allergies = it.optBoolean("allergies", false)
                val noise = it.optString("noise", "—")
                tvEnvironment.text = "Environment — Climate: $climate | Living: $living | Allergies: $allergies | Noise: $noise"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            tvFallbackNotice.text = "Error parsing fallback data."
        }
    }

    private fun buildLocalRecommendationJSON(filters: JSONObject): JSONObject {
        val animalType = filters.optString("animalType", "animal")
        val size = filters.optString("size", "Any")
        val coat = filters.optString("coat", "Any")
        val activity = filters.optString("activity", "Any")
        val climate = filters.optString("climate", "Any")
        val living = filters.optString("livingSpace", "Any")
        val allergies = filters.optBoolean("allergies", false)
        val noise = filters.optString("noise", "Any")
        val maxPrice = filters.optInt("maxPrice", 5000)

        val recommendation = JSONObject()
        val breedName = when {
            animalType.contains("cat", ignoreCase = true) && size == "Small" -> "Domestic Shorthaired Cat"
            animalType.contains("cat", ignoreCase = true) && allergies -> "Sphynx / Hypoallergenic breed"
            animalType.contains("dog", ignoreCase = true) && size == "Small" && activity == "Low" -> "Shih Tzu"
            animalType.contains("dog", ignoreCase = true) && size == "Large" -> "Golden Retriever"
            animalType.contains("fish", ignoreCase = true) || animalType.contains("aquatic", ignoreCase = true) -> "Community tropical fish"
            animalType.contains("tarantula", ignoreCase = true) || animalType.contains("spider", ignoreCase = true) -> "Tarantula (care specialized)"
            else -> "${animalType.capitalize()} (Mixed breed/variety)"
        }
        recommendation.put("breed", breedName)
        recommendation.put("match", 70)
        recommendation.put("maintenance", if (activity == "High" || coat == "Long") "High" else "Medium")
        recommendation.put("price_range", "₹0 - ₹${maxPrice + 2000}")
        val env = JSONObject().apply {
            put("climate", climate)
            put("living_space", living)
            put("allergies", allergies)
            put("noise", noise)
        }
        recommendation.put("environment", env)
        recommendation.put("care_tip", "Provide appropriate habitat and care for $animalType; follow species-specific guidelines.")
        return recommendation
    }
}
