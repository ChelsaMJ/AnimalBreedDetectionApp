package com.example.animalbreeddetectionapp.dashboard

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.animalbreeddetectionapp.R
import com.example.animalbreeddetectionapp.network.GeminiApi
import java.io.ByteArrayInputStream

class BreedResultActivity : AppCompatActivity() {

    private lateinit var imgMain: ImageView
    private lateinit var tvMainTitle: TextView
    private lateinit var tvMainSub: TextView
    private lateinit var btnShare: Button
    private lateinit var btnRescan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_breed_result)

        imgMain = findViewById(R.id.img_main)
        tvMainTitle = findViewById(R.id.tv_main_title)
        tvMainSub = findViewById(R.id.tv_main_sub)
        btnShare = findViewById(R.id.btn_share)
        btnRescan = findViewById(R.id.btn_rescan)

        // 🔄 Rescan → go back
        btnRescan.setOnClickListener { finish() }

        // 📤 Share
        btnShare.setOnClickListener {
            val shareText = "${tvMainTitle.text}\n\n${tvMainSub.text}"
            val i = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(i, "Share breed result"))
        }

        // 🖼️ Get image bytes
        val imageBytes = intent.getByteArrayExtra("imageBytes")
        if (imageBytes != null) {
            try {
                val bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(imageBytes))
                imgMain.setImageBitmap(bitmap)
            } catch (e: Exception) {
                imgMain.setImageResource(R.drawable.placeholder)
            }
            analyzeWithGemini(imageBytes)
        } else {
            tvMainTitle.text = "No image provided"
            tvMainSub.text = "Please capture or upload an image to detect a breed."
        }
    }

    private fun analyzeWithGemini(imageBytes: ByteArray) {
        val imageBase64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
        tvMainTitle.text = "Analyzing..."
        tvMainSub.text = "Please wait while the AI analyzes your image."

        GeminiApi.analyzeBreed(imageBase64, { aiResponse ->
            runOnUiThread {
                if (aiResponse.isBlank()) {
                    tvMainTitle.text = "No breed detected"
                    tvMainSub.text = "Try again with a clearer image."
                    return@runOnUiThread
                }

                val formatted = formatAiResponse(aiResponse)
                tvMainTitle.text = extractMainTitle(aiResponse) ?: "Detected Breed"
                tvMainSub.text = extractMainSubtitle(aiResponse) ?: formatted.take(400)
            }
        }, { error ->
            runOnUiThread {
                tvMainTitle.text = "Analysis failed"
                tvMainSub.text = "AI error: $error"
                Toast.makeText(this, "AI Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun formatAiResponse(raw: String): String {
        return raw
            .replace("Breed Name:", "\n\n🐾 Breed Name:")
            .replace("Scientific Name:", "\n🔬 Scientific Name:")
            .replace("Description:", "\n📖 Description:")
            .replace("Care Tips:", "\n💡 Care Tips:")
            .replace("*", "")
            .trim()
    }


    private fun extractMainTitle(raw: String): String? {
        val bn = Regex("Breed Name[:\\-]?\\s*(.+)", RegexOption.IGNORE_CASE).find(raw)?.groups?.get(1)?.value
        if (!bn.isNullOrBlank()) return bn.trim()
        val firstLine = raw.trim().lineSequence().firstOrNull()
        return firstLine?.takeIf { it.length in 3..50 }?.trim()
    }

    private fun extractMainSubtitle(raw: String): String? {
        val desc = Regex(
            "Description[:\\-]?\\s*(.+)",
            setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
        ).find(raw)?.groups?.get(1)?.value
        return desc?.trim()?.takeIf { it.isNotBlank() }?.take(400)
    }
}
