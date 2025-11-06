package com.example.animalbreeddetectionapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase

class Signup : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseDatabase.getInstance().getReference("users")

    private lateinit var signupBtn: Button
    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginRedirect: TextView

    private val TAG = "Signup"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        signupBtn = findViewById(R.id.signupBtn)
        nameInput = findViewById(R.id.name)
        emailInput = findViewById(R.id.email)
        passwordInput = findViewById(R.id.password)
        loginRedirect = findViewById(R.id.loginRedirect)

        signupBtn.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid
                        if (uid == null) {
                            Toast.makeText(this, "Failed to get user id after signup", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        }

                        // Build DB payload
                        val userData = mapOf(
                            "name" to name,
                            "email" to email
                        )

                        // Save to realtime DB under "users/<uid>"
                        db.child(uid).setValue(userData)
                            .addOnSuccessListener {
                                // Update FirebaseAuth displayName so auth.currentUser?.displayName is set
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build()

                                auth.currentUser?.updateProfile(profileUpdates)
                                    ?.addOnCompleteListener { profileTask ->
                                        if (profileTask.isSuccessful) {
                                            Log.d(TAG, "DisplayName updated successfully")
                                        } else {
                                            Log.w(TAG, "DisplayName update failed: ${profileTask.exception?.message}")
                                        }
                                        // Notify user and go to Login (or main activity)
                                        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this, Login::class.java))
                                        finish()
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Failed to save user data to DB: ${e.message}", e)
                                Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_LONG).show()
                            }

                    } else {
                        Toast.makeText(
                            this,
                            "Signup failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        loginRedirect.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }
    }
}
