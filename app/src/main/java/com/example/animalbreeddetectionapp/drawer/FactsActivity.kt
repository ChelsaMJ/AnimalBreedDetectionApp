package com.example.animalbreeddetectionapp.drawer

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.animalbreeddetectionapp.R

class FactsActivity : AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var animalImage: ImageView
    private lateinit var animalName: TextView
    private lateinit var factsText: TextView
    private lateinit var toolbar: Toolbar

    // small data holder
    data class AnimalFact(val id: String, val displayName: String, val imageRes: Int, val fact: String)

    private val animals = listOf(
        AnimalFact(
            id = "dog",
            displayName = "Dog",
            imageRes = R.drawable.dog,
            fact = "Dogs have been human companions for thousands of years. Their sense of smell is at least 40× better than humans and they can learn hundreds of commands and signals."
        ),
        AnimalFact(
            id = "cat",
            displayName = "Cat",
            imageRes = R.drawable.cat,
            fact = "Cats sleep about 70% of the day and can rotate their ears 180°. They use whiskers to sense nearby objects and tiny changes in air currents."
        ),
        AnimalFact(
            id = "elephant",
            displayName = "Elephant",
            imageRes = R.drawable.elephant,
            fact = "Elephants are highly social and intelligent. They can recognize themselves in a mirror, show empathy, and have exceptional memory — 'an elephant never forgets.'"
        ),
        AnimalFact(
            id = "parrot",
            displayName = "Parrot",
            imageRes = R.drawable.parrot,
            fact = "Parrots can mimic human speech and recognize words. Many species are excellent problem solvers and enjoy social play and puzzles."
        ),
        AnimalFact(
            id = "lion",
            displayName = "Lion",
            imageRes = R.drawable.lion,
            fact = "Lions are the only big cats that live in social groups called prides. Female lions do most of the hunting while males defend the territory."
        ),
        AnimalFact(
            id = "dolphin",
            displayName = "Dolphin",
            imageRes = R.drawable.dolphin,
            fact = "Dolphins are highly social and use unique signature whistles (like names) to identify one another. They are also excellent problem solvers and can learn complex tricks."
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facts)

        // views
        spinner = findViewById(R.id.spinner_animals)
        animalImage = findViewById(R.id.animal_image)
        animalName = findViewById(R.id.animal_name)
        factsText = findViewById(R.id.facts_text)



        // spinner setup: show display names
        val names = animals.map { it.displayName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, names)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // set default selection (first)
        updateUIFor(animals[0])

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected = animals[position]
                crossfadeUpdate(selected)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // no-op
            }
        }
    }

    // immediate update (no animation) used for initial state
    private fun updateUIFor(animal: AnimalFact) {
        animalImage.setImageResource(animal.imageRes)
        animalName.text = animal.displayName
        factsText.text = animal.fact
        animalImage.alpha = 1f
        animalName.alpha = 1f
        factsText.alpha = 1f
    }

    // animate crossfade for a smooth transition
    private fun crossfadeUpdate(animal: AnimalFact) {
        // fade out
        animalImage.animate().alpha(0f).setDuration(180).withEndAction {
            animalImage.setImageResource(animal.imageRes)
            animalImage.animate().alpha(1f).setDuration(220).start()
        }.start()

        animalName.animate().alpha(0f).setDuration(160).withEndAction {
            animalName.text = animal.displayName
            animalName.animate().alpha(1f).setDuration(200).start()
        }.start()

        factsText.animate().alpha(0f).setDuration(160).withEndAction {
            factsText.text = animal.fact
            factsText.animate().alpha(1f).setDuration(200).start()
        }.start()
    }


}
