package com.example.animalbreeddetectionapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.animalbreeddetectionapp.R
import org.json.JSONObject

class ExploreFragment : Fragment() {

    private lateinit var etAnimalType: EditText
    private lateinit var spSize: Spinner
    private lateinit var spCoat: Spinner
    private lateinit var spActivity: Spinner
    private lateinit var spClimate: Spinner
    private lateinit var spLivingSpace: Spinner
    private lateinit var switchAllergies: Switch
    private lateinit var spNoise: Spinner
    private lateinit var seekPrice: SeekBar
    private lateinit var tvPriceValue: TextView
    private lateinit var btnFind: Button

    // Optionally put a key for quick test, or leave blank and use Constants/secure store
    private val apiKeyForThisRun: String = "AIzaSyAqpImKx05neQux_JzmWNzfK6qDhKkpcJw" // set if you want quick in-fragment key injection

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        // NEW: text input for animal type
        etAnimalType = view.findViewById(R.id.et_animal_type)
        spSize = view.findViewById(R.id.sp_size)
        spCoat = view.findViewById(R.id.sp_coat)
        spActivity = view.findViewById(R.id.sp_activity)
        spClimate = view.findViewById(R.id.sp_climate)
        spLivingSpace = view.findViewById(R.id.sp_living_space)
        switchAllergies = view.findViewById(R.id.switch_allergies)
        spNoise = view.findViewById(R.id.sp_noise)
        seekPrice = view.findViewById(R.id.seek_price)
        tvPriceValue = view.findViewById(R.id.tv_price_value)
        btnFind = view.findViewById(R.id.btn_find_breeds)

        val sizeOptions = listOf("Any", "Small", "Medium", "Large", "Giant")
        val coatOptions = listOf("Any", "Short", "Medium", "Long", "Hypoallergenic", "Wire")
        val activityOptions = listOf("Any", "Low", "Moderate", "High", "Very High")
        val climateOptions = listOf("Any", "Hot", "Temperate", "Cold", "Humid")
        val livingSpaceOptions = listOf("Any", "Apartment (no yard)", "Apartment (with balcony)", "House with small yard", "House with large yard", "Farm/rural")
        val noiseOptions = listOf("Any", "Very quiet", "Quiet", "Average", "Noisy household")

        spSize.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sizeOptions)
        spCoat.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, coatOptions)
        spActivity.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, activityOptions)
        spClimate.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, climateOptions)
        spLivingSpace.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, livingSpaceOptions)
        spNoise.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, noiseOptions)

        tvPriceValue.text = "₹${seekPrice.progress}"
        seekPrice.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                tvPriceValue.text = "₹$progress"
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        btnFind.setOnClickListener {
            val animalTypeInput = etAnimalType.text.toString().trim().ifEmpty { "Any" } // default
            val filters = JSONObject().apply {
                put("animalType", animalTypeInput)
                put("size", spSize.selectedItem as String)
                put("coat", spCoat.selectedItem as String)
                put("activity", spActivity.selectedItem as String)
                put("climate", spClimate.selectedItem as String)
                put("livingSpace", spLivingSpace.selectedItem as String)
                put("allergies", switchAllergies.isChecked)
                put("noise", spNoise.selectedItem as String)
                put("maxPrice", seekPrice.progress)
            }

            val intent = Intent(requireContext(), com.example.animalbreeddetectionapp.dashboard.RecommendationResultActivity::class.java).apply {
                putExtra("filtersJson", filters.toString())
                putExtra("apiKey", apiKeyForThisRun)
            }
            startActivity(intent)
        }

        return view
    }
}
