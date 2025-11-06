package com.example.animalbreeddetectionapp.fragments

import android.content.Intent
import android.os.Bundle
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
        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        val uid = auth.currentUser?.uid
        if (uid != null) {
            dbRef.child(uid).get().addOnSuccessListener {
                val name = it.child("name").value?.toString() ?: "User"
                val email = auth.currentUser?.email ?: "Unknown"
                textName.text = name
                textEmail.text = email
            }.addOnFailureListener {
                textName.text = "Failed to load"
            }
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
