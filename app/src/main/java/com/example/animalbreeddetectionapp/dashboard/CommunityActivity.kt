package com.example.animalbreeddetectionapp.dashboard

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.animalbreeddetectionapp.databinding.ActivityCommunityBinding

class CommunityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityBinding

    // 🌐 Curated and trusted animal community sources
    private val articles = listOf(
        Pair("🐾 Animal Planet – Stories & Videos", "https://www.animalplanet.com/"),
        Pair("🦁 National Geographic – Animal Facts & Research", "https://www.nationalgeographic.com/animals/"),
        Pair("🐶 Petfinder Blog – Adoption & Pet Care", "https://www.petfinder.com/blog/"),
        Pair("🐱 The Dodo – Heartwarming Rescue Stories", "https://www.thedodo.com/"),
        Pair("🐘 WWF – Wildlife Conservation Stories", "https://www.worldwildlife.org/stories"),
        Pair("🦜 World Animal Protection – Global Welfare Efforts", "https://www.worldanimalprotection.org/")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Dynamically populate clickable article list
        articles.forEach { (title, url) ->
            val tv = layoutInflater.inflate(android.R.layout.simple_list_item_1, null)
                .findViewById<android.widget.TextView>(android.R.id.text1)
            tv.text = title
            tv.textSize = 17f
            tv.setPadding(32, 30, 32, 30)
            tv.setOnClickListener { openArticle(url, title) }
            binding.containerArticles.addView(tv)
        }

        // Configure WebView
        binding.webview.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
        }

        binding.btnCloseWeb.setOnClickListener { hideWeb() }
    }

    private fun openArticle(url: String, title: String) {
        binding.webview.visibility = View.VISIBLE
        binding.webviewTitle.text = title
        binding.webviewTitle.visibility = View.VISIBLE
        binding.btnCloseWeb.visibility = View.VISIBLE
        binding.webview.loadUrl(url)
    }

    private fun hideWeb() {
        binding.webview.stopLoading()
        binding.webview.visibility = View.GONE
        binding.webviewTitle.visibility = View.GONE
        binding.btnCloseWeb.visibility = View.GONE
    }

    override fun onBackPressed() {
        when {
            binding.webview.visibility == View.VISIBLE && binding.webview.canGoBack() -> binding.webview.goBack()
            binding.webview.visibility == View.VISIBLE -> hideWeb()
            else -> super.onBackPressed()
        }
    }
}
