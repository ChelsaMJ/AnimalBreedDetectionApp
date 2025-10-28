package com.example.animalbreeddetectionapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.animalbreeddetectionapp.databinding.ItemSavedBreedBinding
import com.example.animalbreeddetectionapp.models.SavedBreed
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class SavedBreedsAdapter(
    private val ctx: Context,
    private val list: MutableList<SavedBreed>
) : RecyclerView.Adapter<SavedBreedsAdapter.VH>() {

    inner class VH(val binding: ItemSavedBreedBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemSavedBreedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        holder.binding.tvBreed.text = item.breedName
        holder.binding.tvConfidence.text = "Confidence: ${(item.confidence * 100).toInt()}%"
        holder.binding.tvTime.text = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm")
            .format(java.util.Date(item.timestamp))

        if (!item.imageUrl.isNullOrEmpty()) {
            Picasso.get().load(item.imageUrl).fit().centerCrop().into(holder.binding.ivPreview)
        } else {
            holder.binding.ivPreview.setImageResource(android.R.drawable.ic_menu_report_image)
        }

        holder.binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(ctx)
                .setTitle("Delete")
                .setMessage("Remove this saved breed?")
                .setPositiveButton("Yes") { _, _ ->
                    val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return@setPositiveButton
                    FirebaseDatabase.getInstance()
                        .getReference("saved_breeds")
                        .child(uid)
                        .child(item.id)
                        .removeValue()
                        .addOnSuccessListener {
                            list.removeAt(position)
                            notifyItemRemoved(position)
                        }
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun getItemCount(): Int = list.size
}
