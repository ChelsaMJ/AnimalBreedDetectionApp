package com.example.animalbreeddetectionapp.dashboard

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animalbreeddetectionapp.adapters.SavedBreedsAdapter
import com.example.animalbreeddetectionapp.databinding.ActivitySavedBreedsBinding
import com.example.animalbreeddetectionapp.models.SavedBreed
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SavedBreedsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavedBreedsBinding
    private lateinit var adapter: SavedBreedsAdapter
    private val items = mutableListOf<SavedBreed>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedBreedsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = SavedBreedsAdapter(this, items)
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter

        loadSavedBreeds()
    }

    private fun loadSavedBreeds() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "Please login to view saved breeds", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.progress.visibility = View.VISIBLE
        val dbRef = FirebaseDatabase.getInstance()
            .getReference("saved_breeds")
            .child(uid)

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                items.clear()
                for (snap in snapshot.children) {
                    val breed = snap.getValue(SavedBreed::class.java)
                    if (breed != null) items.add(breed)
                }
                adapter.notifyDataSetChanged()
                binding.progress.visibility = View.GONE
                binding.tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progress.visibility = View.GONE
                Toast.makeText(this@SavedBreedsActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
