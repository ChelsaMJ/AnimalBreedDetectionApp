package com.example.animalbreeddetectionapp.dashboard

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.animalbreeddetectionapp.R
import com.example.animalbreeddetectionapp.network.GeminiApi
import java.io.ByteArrayInputStream

class BreedResultActivity : AppCompatActivity() {

    private lateinit var imgMain: ImageView
    private lateinit var tvMainTitle: TextView
    private lateinit var tvMainSub: TextView
    private lateinit var llBreedList: LinearLayout
    private lateinit var btnShare: Button
    private lateinit var btnRescan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_breed_result)

        imgMain = findViewById(R.id.img_main)
        tvMainTitle = findViewById(R.id.tv_main_title)
        tvMainSub = findViewById(R.id.tv_main_sub)
        llBreedList = findViewById(R.id.ll_breed_list)
        btnShare = findViewById(R.id.btn_share)
        btnRescan = findViewById(R.id.btn_rescan)

        btnRescan.setOnClickListener { finish() }

        btnShare.setOnClickListener {
            val shareText = "${tvMainTitle.text}\n\n${tvMainSub.text}"
            val i = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(i, "Share breed result"))
        }

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
        llBreedList.removeAllViews()

        GeminiApi.analyzeBreed(imageBase64, { aiResponse ->
            runOnUiThread {
                if (aiResponse.isBlank()) {
                    tvMainTitle.text = "No breed detected"
                    tvMainSub.text = "Please try again with a clearer image."
                    return@runOnUiThread
                }

                val formatted = formatAiResponse(aiResponse)
                tvMainTitle.text = extractMainTitle(aiResponse) ?: "Detected Breed"
                tvMainSub.text = extractMainSubtitle(aiResponse) ?: formatted.take(200)

                val items = parseBreedsWithPercent(aiResponse)

                llBreedList.removeAllViews()
                if (items.isEmpty()) {
                    // fallback: show single result card
                    val v = LayoutInflater.from(this).inflate(R.layout.item_breed_small, llBreedList, false)
                    v.findViewById<ImageView>(R.id.iv_small).setImageResource(R.drawable.placeholder)
                    v.findViewById<TextView>(R.id.tv_small_name).text = tvMainTitle.text
                    v.findViewById<TextView>(R.id.tv_small_pct).text = "Detected"
                    llBreedList.addView(v)
                } else {
                    // show multiple detected breeds
                    for ((name, pct) in items) {
                        val row = LayoutInflater.from(this).inflate(R.layout.item_breed_small, llBreedList, false)
                        val iv = row.findViewById<ImageView>(R.id.iv_small)
                        val tvName = row.findViewById<TextView>(R.id.tv_small_name)
                        val tvPct = row.findViewById<TextView>(R.id.tv_small_pct)

                        iv.setImageResource(R.drawable.placeholder)
                        tvName.text = name
                        tvPct.text = pct
                        llBreedList.addView(row)
                    }
                }
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
            .replace("1️⃣", "Breed Name:")
            .replace("2️⃣", "\n\nDescription:")
            .replace("3️⃣", "\n\nCare Tips:")
            .replace("**", "")
            .replace("*", "• ")
            .trim()
    }

    private fun extractMainTitle(raw: String): String? {
        val bn = Regex("Breed Name[:\\-]?\\s*(.+)", RegexOption.IGNORE_CASE).find(raw)?.groups?.get(1)?.value
        if (!bn.isNullOrBlank()) return bn.trim()
        val firstLine = raw.trim().lineSequence().firstOrNull()
        return firstLine?.takeIf { it.length in 3..40 }?.trim()
    }

    private fun extractMainSubtitle(raw: String): String? {
        val desc = Regex(
            "Description[:\\-]?\\s*(.+)",
            setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
        ).find(raw)?.groups?.get(1)?.value
        return desc?.trim()?.takeIf { it.isNotBlank() }?.take(240)
    }


    private fun parseBreedsWithPercent(raw: String): List<Pair<String, String>> {
        val results = mutableListOf<Pair<String, String>>()
        val regex = Regex("([A-Za-z0-9\\s\\-]+?)\\s*[\\-–:]?\\s*\\(?([0-9]{1,2}(?:\\.[0-9])?)%\\)?", RegexOption.IGNORE_CASE)
        for (m in regex.findAll(raw)) {
            val name = m.groups[1]?.value?.trim() ?: continue
            val pct = m.groups[2]?.value?.trim()?.plus("%") ?: ""
            results.add(name to pct)
        }

        if (results.isEmpty()) {
            val lines = raw.lines().map { it.trim() }.filter { it.isNotBlank() }
            for (ln in lines) {
                val parts = ln.split("—", "-", ":").map { it.trim() }
                if (parts.size >= 2 && parts.last().contains("%")) {
                    results.add(parts.first() to parts.last())
                }
            }
        }
        return results
    }
}
