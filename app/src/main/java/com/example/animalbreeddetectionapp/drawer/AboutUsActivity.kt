package com.example.animalbreeddetectionapp.drawer


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.animalbreeddetectionapp.R


class AboutUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "About Us"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
