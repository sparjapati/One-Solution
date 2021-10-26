package edu.vermaSanjay15907.oneSolution.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.vermaSanjay15907.oneSolution.adapters.ComplaintImagesAdapter
import edu.vermaSanjay15907.oneSolution.databinding.FragmentComplaintDetailBinding
import edu.vermaSanjay15907.oneSolution.models.Complaint
import edu.vermaSanjay15907.oneSolution.models.User
import edu.vermaSanjay15907.oneSolution.utils.Konstants.COMPLAINTS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.PROFILE_DETAILS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.TAG
import edu.vermaSanjay15907.oneSolution.utils.Konstants.USERS
import edu.vermaSanjay15907.oneSolution.utils.setAddress
import edu.vermaSanjay15907.oneSolution.utils.setComplaintStatus


class ComplaintDetailFragment : Fragment() {
    private lateinit var binding: FragmentComplaintDetailBinding
    private lateinit var complaintId: String
    private lateinit var database: FirebaseDatabase
    private lateinit var dialog: ProgressDialog
    private var complaint: Complaint? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentComplaintDetailBinding.inflate(layoutInflater, container, false)
        database = FirebaseDatabase.getInstance()
        initialiseDialog()

        var args = arguments?.let { ComplaintDetailFragmentArgs.fromBundle(it) }
        complaintId = args!!.complaintId

        val images = ArrayList<String>()
        val adapter = ComplaintImagesAdapter(requireActivity(), images)
        val layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvImages.layoutManager = layoutManager
        binding.rvImages.isNestedScrollingEnabled = false
        binding.rvImages.adapter = adapter

        dialog.show()

        database.reference.child(COMPLAINTS).child(complaintId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(complaintSnapshot: DataSnapshot) {
                    complaint = complaintSnapshot.getValue(Complaint::class.java)
                    Log.d(TAG, "onDataChange: $complaint")
                    complaint?.apply {
                        database.reference.child(USERS)
                            .child(complainedBy).child(PROFILE_DETAILS)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(userSnapshot: DataSnapshot) {
                                    val user =
                                        userSnapshot.getValue(User::class.java)
                                    Log.d(TAG, "onDataChange: $user")
                                    user?.apply {
                                        binding.tvComplaintBy.text =
                                            "$fname $lname"
                                    }
                                    dialog.dismiss()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })

                        binding.apply {
                            ivStatus.setComplaintStatus(status)
                            tvComplaintAddress.setAddress(address)
                            tvComplaintDescription.text = description
                            // todo add all images
                            if (complaint!!.images != "") {
                                images.add(complaint!!.images)
                                adapter.notifyDataSetChanged()
                            } else
                                binding.tvNoPhoto.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        binding.btnLocateMe.setOnClickListener {
            val address =
                "${complaint?.address?.cityOrVillage} ${complaint?.address?.district} ${complaint?.address?.state}"

            val gmmIntentUri =
                Uri.parse("geo:0,0?q=$address")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        return binding.root

    }

    private fun initialiseDialog() {
        dialog = ProgressDialog(activity)
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog.setTitle("Loading data")
        dialog.setMessage("Please wait...")
        dialog.setCancelable(false)
    }
}