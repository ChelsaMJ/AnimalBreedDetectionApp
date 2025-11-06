package com.example.animalbreeddetectionapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.animalbreeddetectionapp.R
import com.example.animalbreeddetectionapp.dashboard.RecommendationResultActivity
import com.example.animalbreeddetectionapp.network.GeminiApi
import org.json.JSONObject

class ExploreFragment : Fragment() {

    private lateinit var spSpace: Spinner
    private lateinit var spClimate: Spinner
    private lateinit var spPetType: Spinner
    private lateinit var spEnergy: Spinner
    private lateinit var spMaintenance: Spinner
    private lateinit var btnFind: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        spSpace = view.findViewById(R.id.sp_space)
        spClimate = view.findViewById(R.id.sp_climate)
        spPetType = view.findViewById(R.id.sp_pet_type)
        spEnergy = view.findViewById(R.id.sp_energy)
        spMaintenance = view.findViewById(R.id.sp_maintenance)
        btnFind = view.findViewById(R.id.btn_find_breeds)

        // simple adapters
        spSpace.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,
            listOf("Apartment", "House small yard", "House large yard"))
        spClimate.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,
            listOf("Hot", "Cold", "Moderate"))
        spPetType.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,
            listOf("Dog", "Cat", "Other"))
        spEnergy.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,
            listOf("Low", "Medium", "High"))
        spMaintenance.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,
            listOf("Low", "Medium", "High"))

        btnFind.setOnClickListener {
            val prefs = HashMap<String, String>()
            prefs["space"] = spSpace.selectedItem.toString()
            prefs["climate"] = spClimate.selectedItem.toString()
            prefs["petType"] = spPetType.selectedItem.toString()
            prefs["energy"] = spEnergy.selectedItem.toString()
            prefs["maintenance"] = spMaintenance.selectedItem.toString()

            val prompt = buildJsonPrompt(prefs)

            Toast.makeText(requireContext(), "Finding recommendations…", Toast.LENGTH_SHORT).show()

            GeminiApi.generateText(prompt, { aiReply ->
                // aiReply should be JSON string per prompt
                requireActivity().runOnUiThread {
                    try {
                        // quick validation — if parseable JSON, pass along
                        JSONObject(aiReply)
                        val intent = Intent(requireContext(), RecommendationResultActivity::class.java)
                        intent.putExtra("aiText", aiReply)
                        startActivity(intent)
                    } catch (e: Exception) {
                        // fallback: pass raw text
                        val intent = Intent(requireContext(), RecommendationResultActivity::class.java)
                        intent.putExtra("aiText", aiReply)
                        startActivity(intent)
                    }
                }
            }, { error ->
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "AI Error: $error", Toast.LENGTH_LONG).show()
                }
            })
        }

        return view
    }

    private fun buildJsonPrompt(p: HashMap<String, String>): String {
        val petType = p["petType"] ?: "dog"
        val space = p["space"] ?: "apartment"
        val climate = p["climate"] ?: "moderate"
        val energy = p["energy"] ?: "medium"
        val maintenance = p["maintenance"] ?: "medium"

        // Request JSON response to make parsing deterministic
        return """
            You are an expert in pet breed recommendations. The user preferences are:
            - pet_type: $petType
            - living_space: $space
            - climate: $climate
            - energy_level: $energy
            - maintenance_preference: $maintenance

            Please respond with a single JSON object (no extra text) with keys:
            {
              "breed": "<best breed name>",
              "match_percent": "<number or percent>",
              "summary": "<one-line summary>",
              "maintenance": "<Low/Medium/High>",
              "price_range": "<INR range e.g. ₹8,000-15,000>",
              "height": "<e.g. 9-10.5 in>",
              "weight": "<e.g. 9-16 lb>",
              "lifespan": "<e.g. 10-18 years>",
              "good_with": ["Children","Seniors","Dogs","Cats"],
              "temperament": ["Friendly","Outgoing"],
              "care_tip": "<one sentence>"
            }

            Only return the JSON object — no additional commentary or text.
        """.trimIndent()
    }
}
