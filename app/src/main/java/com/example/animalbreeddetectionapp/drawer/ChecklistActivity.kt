package com.example.animalbreeddetectionapp.drawer

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animalbreeddetectionapp.R


class ChecklistActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChecklistAdapter

    // Sample checklist items — replace/add as you like
    private val defaultItems = listOf(
        "Check camera permissions",
        "Ensure good lighting",
        "Crop image if needed",
        "Select animal type manually (if auto fails)",
        "Save detection result",
        "Share result with friends",
        "Read guidebook for care tips"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checklist)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Checklist"

        recyclerView = findViewById(R.id.checklist_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // load saved items if any; otherwise use defaults
        val items = loadItems(prefs).ifEmpty { defaultItems }

        adapter = ChecklistAdapter(items.toMutableList(), prefs)
        recyclerView.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // modern back handling
        return true
    }

    private fun loadItems(prefs: android.content.SharedPreferences): List<String> {
        // optional: support persisted custom items later; for now only default items are used
        // Keep this function to extend later if you want to allow adding custom checklist items.
        return emptyList()
    }

    companion object {
        const val PREFS_NAME = "chimera_checklist_prefs"
        const val PREF_CHECKED_SET = "checked_items_set"
    }
}
