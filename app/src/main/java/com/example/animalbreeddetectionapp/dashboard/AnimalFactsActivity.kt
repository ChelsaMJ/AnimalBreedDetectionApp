package com.example.animalbreeddetectionapp.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.animalbreeddetectionapp.databinding.ActivityAnimalFactsBinding
import com.example.animalbreeddetectionapp.models.SavedFact
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlin.random.Random

class AnimalFactsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimalFactsBinding
    private val facts = listOf(
        "Dogs have about 1,700 taste buds. Humans have about 9,000.",
        "A group of cats is called a clowder.",
        "Dalmatians are born completely white and develop spots later.",
        "Elephants are the only mammals that can't jump.",
        "A newborn kangaroo is about 1 inch long.",
        "Some breeds of chickens can remember over 100 faces.",
        "Cats sleep between 12–16 hours a day on average.",
        "Dogs' noses are wet to help absorb scent chemicals."
    )

    private lateinit var authUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimalFactsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        showDailyFact()

        binding.btnRandom.setOnClickListener { showRandomFact() }
        binding.btnShare.setOnClickListener { shareCurrentFact() }
        binding.btnSave.setOnClickListener { saveCurrentFactAsFavorite() }
    }

    private fun showDailyFact() {
        val day = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        val index = day % facts.size
        binding.tvFact.text = facts[index]
    }

    private fun showRandomFact() {
        binding.tvFact.text = facts[Random.nextInt(facts.size)]
    }

    private fun shareCurrentFact() {
        val fact = binding.tvFact.text.toString()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "🐾 Animal Fact:\n$fact")
        }
        startActivity(Intent.createChooser(intent, "Share fact via"))
    }

    private fun saveCurrentFactAsFavorite() {
        if (authUid.isEmpty()) {
            Toast.makeText(this, "Please login to save favorites", Toast.LENGTH_SHORT).show()
            return
        }

        val factText = binding.tvFact.text.toString()
        val dbRef = FirebaseDatabase.getInstance()
            .getReference("saved_facts")
            .child(authUid)

        val id = dbRef.push().key ?: System.currentTimeMillis().toString()
        val fact = SavedFact(id, factText)

        dbRef.child(id).setValue(fact)
            .addOnSuccessListener {
                Toast.makeText(this, "✅ Fact saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "❌ Save failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
