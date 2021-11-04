package edu.vermaSanjay15907.oneSolution.fragments

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import edu.vermaSanjay15907.oneSolution.adapters.ComplaintImagesAdapter
import edu.vermaSanjay15907.oneSolution.adapters.SelectImageRecyclerViewAdapter
import edu.vermaSanjay15907.oneSolution.databinding.FragmentComplaintDetailBinding
import edu.vermaSanjay15907.oneSolution.models.Complaint
import edu.vermaSanjay15907.oneSolution.models.User
import edu.vermaSanjay15907.oneSolution.utils.Konstants
import edu.vermaSanjay15907.oneSolution.utils.Konstants.COMPLAINTS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.COMPLAINT_IMAGES
import edu.vermaSanjay15907.oneSolution.utils.Konstants.PROFILE_DETAILS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.STATUS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.STATUS_SOLVED
import edu.vermaSanjay15907.oneSolution.utils.Konstants.TAG
import edu.vermaSanjay15907.oneSolution.utils.Konstants.USERS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.WORK_DOCUMENTS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.showSnackBar
import edu.vermaSanjay15907.oneSolution.utils.setAddress
import edu.vermaSanjay15907.oneSolution.utils.setComplaintStatus


class ComplaintDetailFragment : Fragment() {
    private lateinit var binding: FragmentComplaintDetailBinding
    private lateinit var complaintId: String
    private lateinit var database: FirebaseDatabase
    private lateinit var dialog: ProgressDialog
    private var imagesUris = ArrayList<Uri>()
    private var complaint: Complaint? = null
    private lateinit var activity: Activity
    private lateinit var snackBar: Snackbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentComplaintDetailBinding.inflate(layoutInflater, container, false)
        database = FirebaseDatabase.getInstance()
        activity = requireActivity()
        initialiseDialog()

        val args = arguments?.let { ComplaintDetailFragmentArgs.fromBundle(it) }
        complaintId = args!!.complaintId

        val submittedImages = ArrayList<String>()
        val submittedImagesAdapter = ComplaintImagesAdapter(requireActivity(), submittedImages)
        val submittedLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvImages.layoutManager = submittedLayoutManager
        binding.rvImages.isNestedScrollingEnabled = false
        binding.rvImages.adapter = submittedImagesAdapter

        val workDoneImages = ArrayList<String>()
        val workDoneAdapter = ComplaintImagesAdapter(requireActivity(), workDoneImages)
        val workLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvWorkImages.layoutManager = workLayoutManager
        binding.rvWorkImages.isNestedScrollingEnabled = false
        binding.rvWorkImages.adapter = workDoneAdapter

        dialog.show()

        val selectImageRecyclerViewAdapter =
            SelectImageRecyclerViewAdapter(requireActivity(), imagesUris)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvSelectImage.layoutManager = layoutManager
        binding.rvSelectImage.adapter = selectImageRecyclerViewAdapter

        database.reference.child(COMPLAINTS).child(complaintId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(complaintSnapshot: DataSnapshot) {
                    complaint = complaintSnapshot.getValue(Complaint::class.java)
                    Log.d(TAG, "onDataChange: $complaint")
                    complaint?.apply {
                        if (status != STATUS_SOLVED)
                            isOfficerSetup()

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
                                }
                            })

                        binding.apply {
                            ivStatus.setComplaintStatus(status)
                            tvComplaintAddress.setAddress(address)
                            tvComplaintDescription.text = description
                            // todo add all images
                            submittedImages.clear()
                            if (complaint!!.submittedImages != "") {
                                submittedImages.add(complaint!!.submittedImages)
                                submittedImagesAdapter.notifyDataSetChanged()
                            } else {
                                binding.tvNoPhoto.visibility = View.VISIBLE
                            }
                            workDoneImages.clear()
                            if (complaint!!.workImages != "") {
                                workDoneImages.add(complaint!!.workImages)
                                workDoneAdapter.notifyDataSetChanged()
                            } else {
                                binding.tvNoWorkPhoto.visibility = View.VISIBLE
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
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

        binding.tvComplaintAddress.setOnClickListener {
            //todo copy to clip board
        }

        snackBar = Snackbar.make(
            requireActivity().findViewById(R.id.content),
            "As you are a officer, you can change status of application(by attaching some document)",
            Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction(" Ok") {
            snackBar.dismiss()
            postSnackBarDismiss()
        }

        binding.btnSubmit.setOnClickListener {
            // upload images and attach to complaint
            if (imagesUris.size > 0) {
                val image = imagesUris[0]
                val imageReference =
                    FirebaseStorage.getInstance().reference.child(COMPLAINT_IMAGES)
                        .child(complaintId).child(
                            WORK_DOCUMENTS
                        )
                        .child(image.lastPathSegment.toString())
                imageReference.putFile(image)
                    .addOnCompleteListener { imageUploadTask ->
                        if (imageUploadTask.isSuccessful) {
                            Log.d(TAG, "submitComplaint: image uploaded")
                            imageReference.downloadUrl.addOnSuccessListener { urlUri ->
                                Log.d(TAG, "submitComplaint: $urlUri")
                                FirebaseDatabase.getInstance().reference.child(COMPLAINTS)
                                    .child(complaintId).child(WORK_DOCUMENTS)
                                    .setValue(urlUri.toString())
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                requireContext(),
                                                "Submitted Successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            imagesUris.clear()
                                            selectImageRecyclerViewAdapter.notifyDataSetChanged()
                                        } else
                                            Log.d(
                                                TAG,
                                                "onCreateView: Some error occurred while downloading url"
                                            )
                                    }
                            }
                        } else {
                            Log.d(
                                TAG,
                                "submitComplaint: Some error occurred while uploading image"
                            )
                        }
                    }
            }

        }

        binding.btnAddImage.setOnClickListener {
            getImages()
        }

        setWorkImageRecyclerViewAdapter()

        return binding.root

    }

    private fun setChangeStatusSpinnerAdapter() {
        val adapter = ArrayAdapter.createFromResource(
            activity,
            edu.vermaSanjay15907.oneSolution.R.array.complaint_status,
            edu.vermaSanjay15907.oneSolution.R.layout.spinner_layout
        )

        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spChangeStatus.adapter = adapter

        binding.spChangeStatus.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedStatus = binding.spChangeStatus.selectedItem.toString()
                    if (selectedStatus == "Solved") {
                        val confirmationDialog =
                            AlertDialog.Builder(activity).setTitle("Change Status of Complaint")
                                .setMessage("Are you sure to change status to Solved?\n(Make sure to contact with complainer if he/she is satisfied or not.)")

                        confirmationDialog.setPositiveButton(
                            "Yes"
                        ) { _: DialogInterface, _: Int ->
                            changeStatusToSolved(complaintId)
                        }
                        confirmationDialog.setNegativeButton("No") { _: DialogInterface, _: Int ->
                            binding.spChangeStatus.setSelection(0)
                        }
                        confirmationDialog.show()
                    }
                    else
                        showSnackBar(activity,"You can't change status again to pending!!!")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

    }

    private fun changeStatusToSolved(complaintId: String) {
        database.reference.child(COMPLAINTS).child(complaintId).child(STATUS)
            .setValue(STATUS_SOLVED).addOnCompleteListener { task ->
                if (task.isSuccessful)
                    showSnackBar(activity, "Changed status successfully", false)
                else
                    showSnackBar(activity, "Some Error occurred!!!\nPlease try again", false)
            }
    }


    private fun postSnackBarDismiss() {
        binding.rootLayout.apply {
//            isUserInteractionEnabled(false)
            alpha = 1.0f
            isSmoothScrollingEnabled = true
        }
    }

    private fun preSnackBarDismiss() {
        binding.rootLayout.apply {
//            isUserInteractionEnabled(false)
            alpha = 0.2f
            isSmoothScrollingEnabled = false
        }
    }


    private fun isOfficerSetup() {
        FirebaseDatabase.getInstance().reference.child(USERS)
            .child(FirebaseAuth.getInstance().uid!!).child(PROFILE_DETAILS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currUser = snapshot.getValue(User::class.java)
                    Log.d(TAG, "complaint details: $currUser")
                    if (currUser!!.isOfficer) {
                        preSnackBarDismiss()
                        setChangeStatusSpinnerAdapter()
                        binding.apply {
                            spChangeStatus.setOnClickListener {
                                showSnackBar(activity,"You don't have permission to change status of application",true)
                            }
                            snackBar.show()
                            labelAddComplaintPhotos.visibility = View.VISIBLE
                            ivImages.visibility = View.VISIBLE
                            constraintLayout.visibility = View.VISIBLE
                            btnAddImage.visibility = View.VISIBLE
                            rvSelectImage.visibility = View.VISIBLE
                            btnSubmit.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun getImages() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, Konstants.GET_IMAGE_REQUEST_CODE)
    }

    private fun initialiseDialog() {
        dialog = ProgressDialog(activity)
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog.setTitle("Loading data")
        dialog.setMessage("Please wait...")
        dialog.setCancelable(false)
    }


    private fun setWorkImageRecyclerViewAdapter() {
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Konstants.GET_IMAGE_REQUEST_CODE) {
            if (data != null) {
                data.data?.let {
                    imagesUris.add(it)
                    binding.rvSelectImage.adapter?.notifyDataSetChanged()
                }
            }
        }
    }
}