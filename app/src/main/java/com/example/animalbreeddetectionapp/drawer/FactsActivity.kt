package com.example.animalbreeddetectionapp.drawer


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.animalbreeddetectionapp.R


class FactsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facts)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Facts by Animal"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
