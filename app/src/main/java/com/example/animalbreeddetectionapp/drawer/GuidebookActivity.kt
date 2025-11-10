package com.example.animalbreeddetectionapp.drawer


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.animalbreeddetectionapp.R


class GuidebookActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guidebook)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Animal Guidebook"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
