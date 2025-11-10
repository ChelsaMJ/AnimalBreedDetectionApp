package com.example.animalbreeddetectionapp.drawer

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.animalbreeddetectionapp.R
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView

class ChecklistAdapter(
    private val items: MutableList<String>,
    private val prefs: SharedPreferences
) : RecyclerView.Adapter<ChecklistAdapter.ViewHolder>() {

    // we persist checked items as a Set<String> of item texts (simple approach)
    private var checkedSet: MutableSet<String> =
        prefs.getStringSet(ChecklistActivity.PREF_CHECKED_SET, emptySet())?.toMutableSet()
            ?: mutableSetOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checklist, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val text = items[position]
        holder.checkBox.text = text
        holder.checkBox.isChecked = checkedSet.contains(text)

        // avoid multiple listeners stacking by clearing then setting
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = checkedSet.contains(text)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) checkedSet.add(text) else checkedSet.remove(text)
            prefs.edit().putStringSet(ChecklistActivity.PREF_CHECKED_SET, checkedSet).apply()
        }
    }

    override fun getItemCount(): Int = items.size

    fun addItem(item: String) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.item_checkbox)
    }
}
