package edu.vermaSanjay15907.oneSolution.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.vermaSanjay15907.oneSolution.R
import edu.vermaSanjay15907.oneSolution.databinding.NewComplaintSelectedImageListItemBinding

class SelectImageRecyclerViewAdapter(
    private val context: Context,
    private val imagesList: ArrayList<Uri>
) : RecyclerView.Adapter<SelectImageRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = NewComplaintSelectedImageListItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.new_complaint_selected_image_list_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUri = imagesList[position]
        holder.binding.ivSelectedImage.setImageURI(imageUri)
        holder.binding.ivDelete.setOnClickListener {
            imagesList.remove(imageUri)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }
}