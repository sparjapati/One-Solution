package edu.vermaSanjay15907.oneSolution.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import edu.vermaSanjay15907.oneSolution.R
import edu.vermaSanjay15907.oneSolution.databinding.ComplaintImageviewBinding

class ComplaintImagesAdapter(private val context: Context, private val images: ArrayList<String>) :
    RecyclerView.Adapter<ComplaintImagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.complaint_imageview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val url = images[position]
        Picasso.get().load(url).placeholder(R.drawable.placeholder)
            .into(holder.binding.imageView)


    }

    override fun getItemCount(): Int {
        return images.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ComplaintImageviewBinding.bind(itemView)
    }
}