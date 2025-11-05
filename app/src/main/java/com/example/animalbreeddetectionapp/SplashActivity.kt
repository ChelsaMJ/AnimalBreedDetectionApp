package com.example.animalbreeddetectionapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // create layout below 👇

        // 2-second delay for splash animation/logo
        Handler(Looper.getMainLooper()).postDelayed({
            val user = FirebaseAuth.getInstance().currentUser

            if (user != null) {
                // ✅ User already logged in
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // 🚪 Not logged in
                startActivity(Intent(this, Login::class.java))
            }
            finish()
        }, 2000)
    }
}
