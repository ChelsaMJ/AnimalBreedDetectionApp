package com.example.animalbreeddetectionapp.drawer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animalbreeddetectionapp.R
import com.example.animalbreeddetectionapp.drawer.ChecklistAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray

class ChecklistActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChecklistAdapter
    private lateinit var fabAdd: FloatingActionButton

    private val prefs by lazy { getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checklist) // your RelativeLayout file

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Checklist"

        recyclerView = findViewById(R.id.checklistRecyclerView)
        fabAdd = findViewById(R.id.fab_add)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        // Load persisted data
        val items = loadItems().toMutableList()
        val checkedSet = loadCheckedSet().toMutableSet()

        adapter = ChecklistAdapter(items, checkedSet) { itemText, checked ->
            // callback whenever an item is checked/unchecked
            updateCheckedState(itemText, checked)
        }

        recyclerView.adapter = adapter

        fabAdd.setOnClickListener {
            showAddItemDialog()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun showAddItemDialog() {
        val inflater = LayoutInflater.from(this)
        val editText = EditText(this)
        editText.hint = "Enter task"

        AlertDialog.Builder(this)
            .setTitle("Add Checklist Item")
            .setView(editText)
            .setPositiveButton("Add") { dialog, _ ->
                val text = editText.text.toString().trim()
                if (text.isNotEmpty()) {
                    adapter.addItem(text)
                    saveItems(adapter.items) // persist list
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun loadItems(): List<String> {
        val json = prefs.getString(PREF_ITEMS_JSON, null) ?: return defaultItems()
        return try {
            val arr = JSONArray(json)
            val list = mutableListOf<String>()
            for (i in 0 until arr.length()) {
                list.add(arr.optString(i))
            }
            list
        } catch (e: Exception) {
            defaultItems()
        }
    }

    private fun saveItems(list: List<String>) {
        val arr = JSONArray()
        list.forEach { arr.put(it) }
        prefs.edit().putString(PREF_ITEMS_JSON, arr.toString()).apply()
    }

    private fun updateCheckedState(item: String, checked: Boolean) {
        val set = prefs.getStringSet(PREF_CHECKED_SET, emptySet())?.toMutableSet() ?: mutableSetOf()
        if (checked) set.add(item) else set.remove(item)
        prefs.edit().putStringSet(PREF_CHECKED_SET, set).apply()
    }

    private fun loadCheckedSet(): Set<String> {
        return prefs.getStringSet(PREF_CHECKED_SET, emptySet()) ?: emptySet()
    }

    private fun defaultItems(): List<String> {
        // default starter items; change as you wish
        return listOf(
            "Observe the animal’s size, shape, and fur pattern",
            "Note the animal’s behavior and habitat",
            "Capture a clear photo in good lighting",
            "Ensure the full body is visible for better detection",
            "Compare features with known breeds in the guidebook",
            "Save and label detected breeds for learning",
            "Share your discovery with other animal lovers"
        )
    }

    companion object {
        const val PREFS_NAME = "chimera_checklist_prefs"
        const val PREF_ITEMS_JSON = "checklist_items_json"
        const val PREF_CHECKED_SET = "checklist_checked_set"
    }
}
