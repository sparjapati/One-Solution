package edu.vermaSanjay15907.oneSolution.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import edu.vermaSanjay15907.oneSolution.R
import edu.vermaSanjay15907.oneSolution.databinding.HomeComplaintItemBinding
import edu.vermaSanjay15907.oneSolution.fragments.HomeFragmentDirections
import edu.vermaSanjay15907.oneSolution.models.Complaint

class HomeActivityComplaintListAdapter(
    private val context: Context,
    private val complaints: ArrayList<Complaint>
) : RecyclerView.Adapter<HomeActivityComplaintListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.home_complaint_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val complaint = complaints[position]
        val complaintId = ""
        holder.apply {

            complainingPersonName.text = "Demo Person"
            complaintAddress.text = "New Delhi Road"
            complaintDate.text = "Today"

//            FirebaseDatabase.getInstance().reference.child(USERS).child(complaint.complainedBy)
//                .child(PERSONAL_DETAILS)
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        val complainingUser = snapshot.getValue(User::class.java)
//                        holder.complainingPersonName.text =
//                            "${complainingUser!!.fname} ${complainingUser.lname}"
//                        holder.complaintAddress.text =
//                            "${complaint.address.nearByLocation}, ${complaint.address.cityOrVillage}, ${complaint.address.district}, ${complaint.address.state}"
//                        holder.complaintDate.text = TimeAgo.using(complaint.date.toLong())
//                        if (complaint.status == STATUS_PENDING)
//                            holder.complaintStatus.setBackgroundColor(Color.YELLOW)
//                        else if (complaint.status == STATUS_SOLVED)
//                            holder.complaintStatus.setBackgroundColor(Color.GREEN)
////                        if (complaint.images.size > 0)
////                            Picasso.get().load(complaint.images[0])
////                                .placeholder(R.drawable.placeholder)
////                                .into(holder.complaintPhoto)
//                        Picasso.get().load(complaint.images)
//                            .placeholder(R.drawable.placeholder)
//                            .into(holder.complaintPhoto)
//
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                    }
//                })
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
//        return complaints.size
        return 25
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = HomeComplaintItemBinding.bind(itemView)

        //        val complaintPhoto = binding.ivComplaintPhoto
//        val complainingPersonName = binding.tvComplaingPersonName
//        val complaintAddress = binding.tvComplaintAddress
//        val complaintDate = binding.tvComplaintDate
        var complaintPhoto: ImageView = itemView.findViewById(R.id.ivComplaintPhoto)
        var complainingPersonName: TextView = itemView.findViewById(R.id.tvComplaingPersonName)
        var complaintAddress: TextView = itemView.findViewById(R.id.tvComplaintAddress)
        var complaintDate: TextView = itemView.findViewById(R.id.tvComplaintDate)
        var complaintStatus: ImageView = itemView.findViewById(R.id.ivStatus)

    }
}