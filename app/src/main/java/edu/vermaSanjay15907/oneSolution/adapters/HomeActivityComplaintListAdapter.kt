package edu.vermaSanjay15907.oneSolution.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import edu.vermaSanjay15907.oneSolution.R
import edu.vermaSanjay15907.oneSolution.databinding.HomeComplaintItemBinding
import edu.vermaSanjay15907.oneSolution.fragments.HomeFragmentDirections
import edu.vermaSanjay15907.oneSolution.models.Complaint
import edu.vermaSanjay15907.oneSolution.models.User
import edu.vermaSanjay15907.oneSolution.utils.Konstants.PROFILE_DETAILS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.USERS
import edu.vermaSanjay15907.oneSolution.utils.setComplaintStatus

class HomeActivityComplaintListAdapter(
    private val context: Context,
    private val complaints: ArrayList<Complaint>
) : RecyclerView.Adapter<HomeActivityComplaintListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.home_complaint_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val complaint = complaints[position]
        val complaintId = ""
        holder.apply {


            FirebaseDatabase.getInstance().reference.child(USERS).child(complaint.complainedBy)
                .child(PROFILE_DETAILS)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val complainingUser = snapshot.getValue(User::class.java)
                        holder.binding.tvComplaingPersonName.text =
                            "${complainingUser!!.fname} ${complainingUser.lname}"
                        holder.binding.tvComplaintAddress.text =
                            "${complaint.address.nearByLocation}, ${complaint.address.cityOrVillage}, ${complaint.address.district}, ${complaint.address.state}"
                        holder.binding.tvComplaintDate.text = TimeAgo.using(complaint.date.toLong())
                        holder.binding.ivStatus.setComplaintStatus(complaint.status)
//                        if (complaint.images.size > 0)
//                            Picasso.get().load(complaint.images[0])
//                                .placeholder(R.drawable.placeholder)
//                                .into(holder.complaintPhoto)
                        Picasso.get().load(complaint.images)
                            .placeholder(R.drawable.placeholder)
                            .into(holder.binding.ivComplaintPhoto)

                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }

        holder.binding.root.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(
                    HomeFragmentDirections.actionHomeFragmentToComplaintDetailFragment(
                        complaintId
                    )
                )
        }

    }

    override fun getItemCount(): Int {
        return complaints.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = HomeComplaintItemBinding.bind(itemView)
    }
}