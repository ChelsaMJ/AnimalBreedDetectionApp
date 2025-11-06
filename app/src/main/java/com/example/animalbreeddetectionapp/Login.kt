package com.example.animalbreeddetectionapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var loginBtn: Button
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var signupRedirect: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        loginBtn = findViewById(R.id.loginBtn)
        emailInput = findViewById(R.id.email)
        passwordInput = findViewById(R.id.password)
        signupRedirect = findViewById(R.id.signupRedirect)

        // Auto-login check
        auth.currentUser?.let {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        loginBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Login failed! Please sign up first.",
                                Toast.LENGTH_LONG
                            ).show()
                            startActivity(Intent(this, Signup::class.java))
                        }
                    }
            }
        }

        signupRedirect.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }
    }
}
