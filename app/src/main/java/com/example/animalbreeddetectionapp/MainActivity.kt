package com.example.animalbreeddetectionapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.animalbreeddetectionapp.fragments.DetectFragment
import com.example.animalbreeddetectionapp.fragments.ExploreFragment
import com.example.animalbreeddetectionapp.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var pressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        // Use findViewById instead of binding
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // ✅ Set DetectFragment as the default fragment
        replaceFragment(DetectFragment())
        bottomNav.selectedItemId = R.id.nav_detect

        // ✅ Handle bottom navigation
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> replaceFragment(ExploreFragment())
                R.id.nav_detect -> replaceFragment(DetectFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finishAffinity()
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
            pressedTime = System.currentTimeMillis()
        }
    }
}
