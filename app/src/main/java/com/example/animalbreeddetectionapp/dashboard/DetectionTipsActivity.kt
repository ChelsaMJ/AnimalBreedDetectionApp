package com.example.animalbreeddetectionapp.dashboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.animalbreeddetectionapp.databinding.ActivityDetectionTipsBinding

class DetectionTipsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetectionTipsBinding
    private val tips = listOf(
        "Use natural light. Avoid harsh shadows and bright backlight.",
        "Get close enough so the animal fills most of the frame.",
        "Keep the camera steady or use burst mode to pick the best shot.",
        "Aim for clear view of the animal's body and head; avoid obstructed views.",
        "Capture multiple angles (front, side, top) if possible.",
        "Avoid zooming heavily — move closer instead for better detail."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetectionTipsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Dynamically add tip cards
        tips.forEachIndexed { index, tip ->
            val tipView = layoutInflater.inflate(android.R.layout.simple_list_item_2, null)
            val title = tipView.findViewById<android.widget.TextView>(android.R.id.text1)
            val subtitle = tipView.findViewById<android.widget.TextView>(android.R.id.text2)
            title.text = "Tip ${index + 1}"
            subtitle.text = tip
            subtitle.setOnClickListener {
                // toggle full text visibility (simple expand)
                subtitle.maxLines = if (subtitle.maxLines == 2) Int.MAX_VALUE else 2
            }
            tipView.setOnLongClickListener {
                copyToClipboard(tip)
                true
            }
            binding.linearTips.addView(tipView)
        }

        binding.btnShareTips.setOnClickListener { shareAllTips() }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("tip", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Tip copied", Toast.LENGTH_SHORT).show()
    }

    private fun shareAllTips() {
        val shareText = tips.joinToString(separator = "\n\n") { "- $it" }
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_TEXT, "Detection Tips:\n$shareText")
        startActivity(Intent.createChooser(i, "Share tips via"))
    }
}
