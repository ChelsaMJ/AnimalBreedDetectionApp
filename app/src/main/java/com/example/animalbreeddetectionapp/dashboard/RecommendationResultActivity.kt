package com.example.animalbreeddetectionapp.dashboard

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.animalbreeddetectionapp.R
import org.json.JSONObject

class RecommendationResultActivity : AppCompatActivity() {

    private lateinit var imgMain: ImageView
    private lateinit var tvBreed: TextView
    private lateinit var tvMatch: TextView
    private lateinit var tvHeightVal: TextView
    private lateinit var tvWeightVal: TextView
    private lateinit var tvLifeVal: TextView
    private lateinit var tvGoodWith: TextView
    private lateinit var tvTemp: TextView
    private lateinit var tvCareTip: TextView
    private lateinit var btnDone: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendation_result)

        imgMain = findViewById(R.id.img_reco_main)
        tvBreed = findViewById(R.id.tv_reco_breed)
        tvMatch = findViewById(R.id.tv_reco_match)
        tvHeightVal = findViewById(R.id.tv_height_val)
        tvWeightVal = findViewById(R.id.tv_weight_val)
        tvLifeVal = findViewById(R.id.tv_life_val)
        tvGoodWith = findViewById(R.id.tv_good_with)
        tvTemp = findViewById(R.id.tv_temperament)
        tvCareTip = findViewById(R.id.tv_care_tip)
        btnDone = findViewById(R.id.btn_done_reco)
        btnBack = findViewById(R.id.btn_back_reco)

        btnBack.setOnClickListener { finish() }
        btnDone.setOnClickListener { finish() }

        val aiText = intent.getStringExtra("aiText")
        if (aiText.isNullOrBlank()) {
            // nothing — show placeholder
            tvBreed.text = "No recommendation"
            return
        }

        // Try parse JSON first (we requested JSON). Fallback to text parsing.
        try {
            val obj = JSONObject(aiText)
            fillFromJson(obj)
        } catch (e: Exception) {
            // fallback: try to extract simple lines
            fallbackFromText(aiText)
        }
    }

    private fun fillFromJson(o: JSONObject) {
        val breed = o.optString("breed", "Unknown")
        val match = o.optString("match_percent", "")
        val summary = o.optString("summary", "")
        val maintenance = o.optString("maintenance", "")
        val price = o.optString("price_range", "")
        val height = o.optString("height", "")
        val weight = o.optString("weight", "")
        val lifespan = o.optString("lifespan", "")
        val goodWith = if (o.has("good_with")) o.optJSONArray("good_with") else null
        val temperament = if (o.has("temperament")) o.optJSONArray("temperament") else null
        val careTip = o.optString("care_tip", "")

        tvBreed.text = breed
        tvMatch.text = if (match.isNotBlank()) match else ""
        tvHeightVal.text = if (height.isNotBlank()) height else "-"
        tvWeightVal.text = if (weight.isNotBlank()) weight else "-"
        tvLifeVal.text = if (lifespan.isNotBlank()) lifespan else "-"
        tvCareTip.text = if (careTip.isNotBlank()) careTip else "-"
        // join arrays
        if (goodWith != null) {
            val arr = mutableListOf<String>()
            for (i in 0 until goodWith.length()) arr.add(goodWith.optString(i))
            tvGoodWith.text = arr.joinToString(", ")
        }
        if (temperament != null) {
            val arr = mutableListOf<String>()
            for (i in 0 until temperament.length()) arr.add(temperament.optString(i))
            tvTemp.text = arr.joinToString(", ")
        }
        // placeholder image; you may replace with a real image later
        imgMain.setImageResource(R.drawable.placeholder)
    }

    private fun fallbackFromText(text: String) {
        // crude extraction using labels
        tvBreed.text = extractAfterLabel(text, listOf("breed:", "breed name:", "name:")) ?: "Breed"
        tvMatch.text = extractAfterLabel(text, listOf("match_percent:", "match %:", "match:")) ?: ""
        tvHeightVal.text = extractAfterLabel(text, listOf("height:")) ?: "-"
        tvWeightVal.text = extractAfterLabel(text, listOf("weight:")) ?: "-"
        tvLifeVal.text = extractAfterLabel(text, listOf("lifespan:", "life span:")) ?: "-"
        tvCareTip.text = extractAfterLabel(text, listOf("care tip:", "care_tip:", "care:")) ?: "-"
        tvGoodWith.text = extractAfterLabel(text, listOf("good with:", "good_with:")) ?: "-"
        tvTemp.text = extractAfterLabel(text, listOf("temperament:")) ?: "-"
        imgMain.setImageResource(R.drawable.placeholder)
    }

    private fun extractAfterLabel(text: String, labels: List<String>): String? {
        val lower = text.toLowerCase()
        for (label in labels) {
            val idx = lower.indexOf(label)
            if (idx >= 0) {
                val start = idx + label.length
                val rest = text.substring(start).trim()
                // stop at newline if present
                val end = rest.indexOf("\n")
                return if (end >= 0) rest.substring(0, end).trim() else rest.trim()
            }
        }
        return null
    }
}
