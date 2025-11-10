package com.example.animalbreeddetectionapp.drawer


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.animalbreeddetectionapp.R

class AboutAppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "About Chimera"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
