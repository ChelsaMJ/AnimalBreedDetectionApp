package com.example.animalbreeddetectionapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.animalbreeddetectionapp.R
import com.example.animalbreeddetectionapp.dashboard.AnimalFactsActivity
import com.example.animalbreeddetectionapp.dashboard.CommunityActivity
import com.example.animalbreeddetectionapp.dashboard.DetectionTipsActivity
import com.example.animalbreeddetectionapp.dashboard.SavedBreedsActivity
import kotlin.jvm.java

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Set up dashboard cards
        view.findViewById<CardView>(R.id.cardFacts).setOnClickListener {
            startActivity(Intent(requireContext(), AnimalFactsActivity::class.java))
        }

        view.findViewById<CardView>(R.id.cardTips).setOnClickListener {
            startActivity(Intent(requireContext(), DetectionTipsActivity::class.java))
        }

        view.findViewById<CardView>(R.id.cardCommunity).setOnClickListener {
            startActivity(Intent(requireContext(), CommunityActivity::class.java))
        }

        view.findViewById<CardView>(R.id.cardSaved).setOnClickListener {
            startActivity(Intent(requireContext(), SavedBreedsActivity::class.java))
        }

        return view
    }
}
