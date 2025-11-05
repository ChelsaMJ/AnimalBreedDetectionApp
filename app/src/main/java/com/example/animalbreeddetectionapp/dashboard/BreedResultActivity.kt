package com.example.animalbreeddetectionapp.dashboard

import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.animalbreeddetectionapp.databinding.ActivityBreedResultBinding
import com.example.animalbreeddetectionapp.network.GeminiApi

class BreedResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBreedResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBreedResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageBytes = intent.getByteArrayExtra("imageBytes")

        // Hide button initially until result is ready
        binding.btnAskMore.visibility = View.GONE

        if (imageBytes != null) {
            val imageBase64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
            analyzeWithGemini(imageBase64)
        } else {
            binding.tvBreedResult.text = "❌ No image received."
        }

        // Repurpose Ask AI button for fun facts
        binding.btnAskMore.setOnClickListener {
            showFunFact()
        }
    }

    private fun analyzeWithGemini(imageBase64: String) {
        binding.lottieView.visibility = View.VISIBLE
        binding.tvBreedResult.text = "🔍 Analyzing image, please wait..."

        GeminiApi.analyzeBreed(imageBase64, { aiResponse ->
            runOnUiThread {
                binding.lottieView.visibility = View.GONE

                // Format & animate the AI response
                binding.tvBreedResult.apply {
                    alpha = 0f
                    text = formatAiResponse(aiResponse)
                    animate().alpha(1f).setDuration(600).start()
                }

                saveToFavorites(aiResponse)
                binding.btnAskMore.visibility = View.VISIBLE
            }
        }, { error ->
            runOnUiThread {
                binding.lottieView.visibility = View.GONE
                binding.tvBreedResult.text = "⚠️ Error: $error"
                Toast.makeText(this, "AI failed to analyze image", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 🧠 Format AI output neatly with emojis & spacing
    private fun formatAiResponse(raw: String): String {
        var formatted = raw
            .replace("1️⃣", "🐾 **Breed Name:**")
            .replace("2️⃣", "\n\n📘 **Description:**")
            .replace("3️⃣", "\n\n🩺 **Care Tips:**")
            .replace("**", "") // remove bold markers
            .replace("*", "• ") // turn list items into bullets

        return formatted.trim()
    }

    private fun saveToFavorites(result: String) {
        val prefs = getSharedPreferences("favorites", MODE_PRIVATE)
        prefs.edit().putString(System.currentTimeMillis().toString(), result).apply()
    }

    // 🌟 Repurposed "Ask AI More" for fun facts / breed insights
    private fun showFunFact() {
        val facts = listOf(
            "🐾 Fun Fact: Most dogs dream just like humans do!",
            "🐕 Did you know? A dog’s nose print is as unique as a human fingerprint.",
            "🐈 Cats spend 70% of their lives sleeping — true relaxation masters!",
            "🐶 Dogs can understand up to 250 words and gestures!",
            "🐦 Birds are the only animals with feathers — and some can even mimic human speech!"
        )
        val randomFact = facts.random()
        Toast.makeText(this, randomFact, Toast.LENGTH_LONG).show()
    }
}
