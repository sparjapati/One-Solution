package edu.vermaSanjay15907.oneSolution.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import edu.vermaSanjay15907.oneSolution.R
import edu.vermaSanjay15907.oneSolution.adapters.SelectImageRecyclerViewAdapter
import edu.vermaSanjay15907.oneSolution.databinding.FragmentNewComplaintBinding
import edu.vermaSanjay15907.oneSolution.models.Complaint
import edu.vermaSanjay15907.oneSolution.models.User
import edu.vermaSanjay15907.oneSolution.utils.Konstants.COMPLAINTS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.COMPLAINTS_BY_LOCATIONS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.COMPLAINT_IMAGES
import edu.vermaSanjay15907.oneSolution.utils.Konstants.GET_IMAGE_REQUEST_CODE
import edu.vermaSanjay15907.oneSolution.utils.Konstants.PROFILE_DETAILS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.TAG
import edu.vermaSanjay15907.oneSolution.utils.Konstants.USERS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.showSnackBar
import java.util.*
import kotlin.collections.ArrayList

class NewComplaintFragment : Fragment() {


    private lateinit var binding: FragmentNewComplaintBinding
    private var complaint = Complaint()
    private var imagesUris = ArrayList<Uri>()
    private var imagesUrls = ArrayList<String>()
    private lateinit var activity: Activity
    private lateinit var dialog: ProgressDialog

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage


    override fun onResume() {
        super.onResume()
        activity?.actionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewComplaintBinding.inflate(layoutInflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        activity = requireActivity()

        initialiseDialog()
        setImageRecyclerViewAdapter()

        binding.apply {
            btnAddImage.setOnClickListener {
                getImages()
            }
            btnSubmit.setOnClickListener {
                dialog.show()
                binding.btnSubmit.isEnabled = false
                extractComplaintDetails()
                submitComplaint()
            }
            etFirstName.setOnClickListener { immutableField() }
            etLastName.setOnClickListener { immutableField() }
            etCountry.setOnClickListener { immutableField() }
            etState.setOnClickListener { immutableField() }
            etDistrict.setOnClickListener { immutableField() }
        }

        setInitialData()
        return binding.root
    }

    public fun immutableField() {
        showSnackBar(
            activity,
            "Sorry, You can't change this field, It's value is referenced from your profile",
            true
        )
    }

    private fun setInitialData() {
        database.reference.child(USERS).child(auth.uid.toString()).child(PROFILE_DETAILS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    Log.d(TAG, "NewComplaintFragment : ${auth.uid}")
                    Log.d(TAG, "NewComplaintFragment: retrieving complaining user data $user")
                    user?.apply {
                        binding.apply {
                            etFirstName.setText(fname)
                            etLastName.setText(lname)
                            etCountry.setText(address.country)
                            etState.setText(address.state)
                            etDistrict.setText(address.district)
                            etCity.setText(address.cityOrVillage)
                            etRegisteredNumber.setText(mobileNumber)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun submitComplaint() {
        val key = database.reference.child(COMPLAINTS).push().key
        if (key != null) {
            complaint.complaintId = key
            if (imagesUris.size > 0) {
                val image = imagesUris[0]
                val imageReference =
                    storage.reference.child(COMPLAINT_IMAGES).child(key).child(COMPLAINT_IMAGES)
                        .child(image.lastPathSegment.toString())

                imageReference.putFile(image)
                    .addOnCompleteListener { imageUploadTask ->
                        if (imageUploadTask.isSuccessful) {
                            Log.d(TAG, "submitComplaint: image uploaded")
                            imageReference.downloadUrl.addOnSuccessListener { urlUri ->
                                Log.d(TAG, "submitComplaint: $urlUri")
                                complaint.submittedImages = urlUri.toString()
                                uploadComplaint(key)
                            }
                        } else {
                            Log.d(TAG, "submitComplaint: Some error occurred while uploading image")
                            onComplaintSubmissionFailure()
                        }
                    }
            } else
                uploadComplaint(key)
        }
    }

    private fun uploadComplaint(key: String?) {
        database.reference.child(COMPLAINTS).child(key!!).setValue(complaint)
            .addOnCompleteListener { uploadComplaintTask ->
                if (uploadComplaintTask.isSuccessful) {
                    linkToComplaintsByLocations(key)
                    linkToUser(key)
                    onComplaintSubmittedSuccessfully()
                } else {
                    onComplaintSubmissionFailure()
                }
            }
    }

    private fun onComplaintSubmissionFailure() {
        Log.d(
            TAG,
            "submitComplaint: some error occurred while uploading complaint "
        )

        val snackBar =
            activity?.let {
                Snackbar.make(
                    it.findViewById(android.R.id.content),
                    "Some Error Occurred!!!\nPlease try again",
                    Snackbar.LENGTH_LONG
                )
            }
        activity?.let {
            ContextCompat.getColor(
                it,
                R.color.colorSnackbarError
            )
        }?.let {
            snackBar?.view?.setBackgroundColor(
                it
            )
        }
        snackBar?.show()
    }

    private fun linkToUser(key: String?) {
        database.reference.child(USERS).child(auth.uid!!)
            .child(COMPLAINTS).child(complaint.date)
            .setValue(key)
            .addOnCompleteListener { userLinkTask ->
                if (userLinkTask.isSuccessful) {
                    Log.d(
                        TAG,
                        "submitComplaint: linked to user successfully"
                    )
                } else {
                    Log.d(
                        TAG,
                        "submitComplaint: linked to user failure"
                    )
                }
            }
    }

    private fun linkToComplaintsByLocations(key: String?) {
        database.reference.child(COMPLAINTS_BY_LOCATIONS)
            .child(complaint.address.country)
            .child(complaint.address.state)
            .child(complaint.address.district)
            .child(complaint.address.cityOrVillage)
            .child(complaint.date)
            .setValue(key)
            .addOnCompleteListener { byLocationTask ->
                if (byLocationTask.isSuccessful)
                    Log.d(
                        TAG,
                        "submitComplaint: linked to location wise successfully"
                    )
                else {
                    Log.d(
                        TAG,
                        "submitComplaint: linked to location wise failed"
                    )
                }
            }
    }

    private fun setImageRecyclerViewAdapter() {
        val selectImageRecyclerViewAdapter =
            SelectImageRecyclerViewAdapter(requireActivity(), imagesUris)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvSelectImage.layoutManager = layoutManager
        binding.rvSelectImage.adapter = selectImageRecyclerViewAdapter
    }

    private fun extractComplaintDetails() {
        complaint.apply {
            complainedBy = auth.uid!!
            address.country = binding.etCountry.text.toString()
            address.state = binding.etState.text.toString()
            address.district = binding.etDistrict.text.toString()
            address.cityOrVillage = binding.etCity.text.toString()
            address.nearByLocation = binding.etNearByLocation.text.toString()
            description = binding.etDescription.text.toString()
            date = Date().time.toString()
        }
    }

    private fun getImages() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, GET_IMAGE_REQUEST_CODE)
    }

    private fun initialiseDialog() {
        dialog = ProgressDialog(activity)
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog.setTitle("Uploading data")
        dialog.setMessage("Please wait...")
        dialog.setCancelable(false)
    }

    private fun onComplaintSubmittedSuccessfully() {
        Log.d(TAG, "Complaint submitted")
        dialog.dismiss()
        val snackBar =
            activity?.let {
                Snackbar.make(
                    it.findViewById(android.R.id.content),
                    "Complaint Submitted Successfully",
                    Snackbar.LENGTH_LONG
                )
            }
        activity?.let {
            ContextCompat.getColor(
                it,
                R.color.colorSnackbarSuccess
            )
        }?.let {
            snackBar?.view?.setBackgroundColor(
                it
            )
        }
        snackBar?.show()
        findNavController().navigate(NewComplaintFragmentDirections.actionNewComplaintFragmentToHomeFragment())
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GET_IMAGE_REQUEST_CODE) {
            if (data != null) {
                data.data?.let {
                    imagesUris.add(it)
                    binding.rvSelectImage.adapter?.notifyDataSetChanged()
                }
            }
        }
    }
}