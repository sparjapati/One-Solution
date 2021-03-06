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
import edu.vermaSanjay15907.oneSolution.utils.*
import edu.vermaSanjay15907.oneSolution.utils.Konstants.COMPLAINTS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.COMPLAINT_IMAGES
import edu.vermaSanjay15907.oneSolution.utils.Konstants.PROFILE_DETAILS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.SOLVED_BY
import edu.vermaSanjay15907.oneSolution.utils.Konstants.STATUS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.STATUS_SOLVED
import edu.vermaSanjay15907.oneSolution.utils.Konstants.TAG
import edu.vermaSanjay15907.oneSolution.utils.Konstants.USERS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.WORK_IMAGES
import edu.vermaSanjay15907.oneSolution.utils.Konstants.copyToClipBoard
import edu.vermaSanjay15907.oneSolution.utils.Konstants.showSnackBar


class ComplaintDetailFragment : Fragment() {
    private lateinit var binding: FragmentComplaintDetailBinding
    private lateinit var complaintId: String
    private lateinit var database: FirebaseDatabase
    private lateinit var dialog: ProgressDialog
    private var imagesUris = ArrayList<Uri>()
    private lateinit var complaint: Complaint
    private lateinit var activity: Activity
    private lateinit var storage: FirebaseStorage

    private lateinit var workImageUploadDialog: ProgressDialog
    private lateinit var snackBar: Snackbar

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentComplaintDetailBinding.inflate(layoutInflater, container, false)
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        activity = requireActivity()
        initialiseDialog()
        initialiseWorkImageUploadDialog()

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
                        complaint = complaintSnapshot.getValue(Complaint::class.java)!!
                        Log.d(TAG, "onDataChange: $complaint")
                        complaint.apply {
                            if (status != STATUS_SOLVED)
                                isOfficerSetup()
                            else {
                                binding.ivStatus.setOnClickListener {
                                    showSnackBar(activity, "You can't change status!!!", true)
                                }
                            }


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
                                    submittedImages.addAll(complaint!!.submittedImages.split(" "))
                                    submittedImagesAdapter.notifyDataSetChanged()
                                } else {
                                    binding.tvNoPhoto.visibility = View.VISIBLE
                                }
                                workDoneImages.clear()
                                if (complaint!!.workImages != "") {
                                    workDoneImages.addAll(complaint!!.workImages.split(" "))
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
                    "${complaint.address?.cityOrVillage} ${complaint.address?.district} ${complaint.address?.state}"

            val gmmIntentUri =
                    Uri.parse("geo:0,0?q=$address")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        binding.tvComplaintAddress.setOnClickListener {
            copyToClipBoard(activity, binding.tvComplaintAddress.text.toString())
            Toast.makeText(activity, "Address copied", Toast.LENGTH_SHORT).show()
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


        binding.btnAddImage.setOnClickListener {
            getImages()
        }

        binding.btnAddWorkPhotos.setOnClickListener {
            // upload images and attach to complaint
            if (imagesUris.size > 0) {
                Log.d(TAG, "onCreateView: uploading ${imagesUris.size} work images")
                workImageUploadDialog.show()
                uploadImages(complaint!!.complaintId,0,complaint.workImages.split(" ").size)
            }
        }



        return binding.root

    }

    private fun uploadImages(key: String, uploadCount: Int = 0, prev: Int = 0) {
        val mainReference = storage.reference.child(COMPLAINT_IMAGES).child(key).child(
                Konstants.WORK_IMAGES
        )
        if (uploadCount < imagesUris.size) {
            val name = "image${prev + 1}"
            Log.d(TAG, "uploadImages: uploading $name")
            val ref = mainReference.child(name)
            ref.putFile(imagesUris[uploadCount]).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "uploadImages: uploaded $name at $it")
                    complaint.workImages += " $it"
//                    Log.d(TAG, "uploadImages: uploaded $name to $it")
                    uploadImages(key, uploadCount + 1, prev + 1)
                }
            }
        } else
            updateToComplaint(key)
    }

    private fun updateToComplaint(key: String) {
        Log.d(TAG, "updateToComplaint: workimages : ${complaint.workImages}")
        complaint.workImages = complaint.workImages.trim()
        database.reference.child(COMPLAINTS).child(key).child(WORK_IMAGES)
                .setValue(complaint.workImages).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                                requireContext(),
                                "Submitted Successfully",
                                Toast.LENGTH_SHORT
                        ).show()
                        imagesUris.clear()
                        binding.rvSelectImage.adapter?.notifyDataSetChanged()
                        workImageUploadDialog.dismiss()
                    } else
                        Toast.makeText(activity, "Please try again", Toast.LENGTH_SHORT).show()
                }
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
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }

    }

    private fun initialiseWorkImageUploadDialog() {
        workImageUploadDialog = ProgressDialog(activity)
        workImageUploadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        workImageUploadDialog.setTitle("Uploading data")
        workImageUploadDialog.setMessage("Please wait...")
        workImageUploadDialog.setCancelable(false)
    }

    private fun changeStatusToSolved(complaintId: String) {
        database.reference.child(COMPLAINTS).child(complaintId).child(STATUS)
                .setValue(STATUS_SOLVED).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        database.reference.child(COMPLAINTS).child(complaintId).child(SOLVED_BY)
                                .setValue(FirebaseAuth.getInstance().uid!!).addOnCompleteListener { task1 ->
                                    if (task1.isSuccessful)
                                        showSnackBar(activity, "Changed status successfully", false)
                                    else
                                        showSnackBar(
                                                activity,
                                                "Some Error occurred!!!\nPlease try again",
                                                false
                                        )
                                }
                    } else
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
                            binding.apply {
                                snackBar.show()
                                labelAddComplaintPhotos.visibility = View.VISIBLE
                                ivImages.visibility = View.VISIBLE
                                constraintLayout.visibility = View.VISIBLE
                                btnAddImage.visibility = View.VISIBLE
                                rvSelectImage.visibility = View.VISIBLE
                                btnAddWorkPhotos.visibility = View.VISIBLE
                            }
                            setChangeStatusSpinnerAdapter()
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
