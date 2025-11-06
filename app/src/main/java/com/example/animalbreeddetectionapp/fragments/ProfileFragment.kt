package com.example.animalbreeddetectionapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.animalbreeddetectionapp.Login
import com.example.animalbreeddetectionapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {

    private lateinit var textName: TextView
    private lateinit var textEmail: TextView
    private lateinit var btnLogout: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    private val TAG = "ProfileFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        textName = view.findViewById(R.id.textName)
        textEmail = view.findViewById(R.id.textEmail)
        btnLogout = view.findViewById(R.id.btnLogout)

        auth = FirebaseAuth.getInstance()
        // IMPORTANT: use the same "users" node as Signup writes to
        dbRef = FirebaseDatabase.getInstance().getReference("users")

        val uid = auth.currentUser?.uid
        val authDisplayName = auth.currentUser?.displayName

        if (uid != null) {
            dbRef.child(uid).get().addOnSuccessListener { dataSnapshot ->
                val dbName = dataSnapshot.child("name").value?.toString()
                val email = auth.currentUser?.email ?: "Unknown"

                // Choose the best available name: DB -> auth displayName -> "User"
                val nameToShow = dbName?.takeIf { it.isNotBlank() }
                    ?: authDisplayName?.takeIf { it.isNotBlank() }
                    ?: "User"

                Log.d(TAG, "Loaded profile: uid=$uid dbName=$dbName authName=$authDisplayName")
                textName.text = nameToShow
                textEmail.text = email
            }.addOnFailureListener { e ->
                Log.w(TAG, "Failed to read user DB data: ${e.message}", e)
                // fallback to auth displayName or email
                textName.text = authDisplayName ?: "User"
                textEmail.text = auth.currentUser?.email ?: "Unknown"
            }
        } else {
            // No logged-in user
            Log.w(TAG, "No current user found in ProfileFragment")
            textName.text = auth.currentUser?.displayName ?: "User"
            textEmail.text = auth.currentUser?.email ?: "Unknown"
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }
}
