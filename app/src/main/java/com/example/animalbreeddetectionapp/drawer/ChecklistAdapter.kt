package com.example.animalbreeddetectionapp.drawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.animalbreeddetectionapp.R

class ChecklistAdapter(
    val items: MutableList<String>,
    private val checkedSet: MutableSet<String>,
    private val onCheckedChanged: (itemText: String, checked: Boolean) -> Unit
) : RecyclerView.Adapter<ChecklistAdapter.CheckViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checklist, parent, false) // your item layout
        return CheckViewHolder(v)
    }

    override fun onBindViewHolder(holder: CheckViewHolder, position: Int) {
        val text = items[position]
        holder.checkBox.text = text

        // remove listener before changing checked state to avoid triggering it
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = checkedSet.contains(text)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) checkedSet.add(text) else checkedSet.remove(text)
            onCheckedChanged(text, isChecked)
        }
    }

    override fun getItemCount(): Int = items.size

    fun addItem(item: String) {
        items.add(0, item) // add to top (or use add(item) to append)
        notifyItemInserted(0)
        // optional: you may want to scroll to top in activity after adding
    }

    inner class CheckViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }
}
