package edu.vermaSanjay15907.oneSolution.fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import edu.vermaSanjay15907.oneSolution.adapters.SelectImageRecyclerViewAdapter
import edu.vermaSanjay15907.oneSolution.databinding.FragmentNewComplaintBinding
import edu.vermaSanjay15907.oneSolution.models.Complaint
import edu.vermaSanjay15907.oneSolution.utils.Konstants.COMPLAINTS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.COMPLAINTS_BY_LOCATIONS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.COMPLAINT_IMAGES
import edu.vermaSanjay15907.oneSolution.utils.Konstants.GET_IMAGE_REQUEST_CODE
import edu.vermaSanjay15907.oneSolution.utils.Konstants.TAG
import edu.vermaSanjay15907.oneSolution.utils.Konstants.USERS
import java.util.*
import kotlin.collections.ArrayList

class NewComplaintFragment : Fragment() {


    private lateinit var binding: FragmentNewComplaintBinding
    private var complaint = Complaint()
    private var imagesUris = ArrayList<Uri>()
    private var imagesUrls = ArrayList<String>()
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

        initialiseDialog()
        setImageRecyclerViewAdapter()

        binding.btnAddImage.setOnClickListener {
            getImages()
        }
        binding.btnSubmit.setOnClickListener {
            binding.btnSubmit.isEnabled = false
            extractComplaintDetails()
            submitComplaint()
        }
        return binding.root
    }

    private fun submitComplaint() {
        val key = database.reference.child(COMPLAINTS).push().key
        if (key != null) {
            complaint.complaintId = key
            if (imagesUris.size >= 0) {
                val image = imagesUris[0]
                val imageReference =
                    storage.reference.child(COMPLAINT_IMAGES).child(auth.uid!!)
                imageReference.child(image.lastPathSegment.toString()).putFile(image)
                    .addOnCompleteListener { imageUploadTask ->
                        if (imageUploadTask.isSuccessful) {
                            Log.d(TAG, "submitComplaint: image uploaded")
                            imageReference.downloadUrl.addOnCompleteListener { urlUri ->
                                if (imageUploadTask.isSuccessful) {
                                    complaint.images = urlUri.toString()
                                    uploadComplaint(key)
                                } else
                                    Log.d(TAG, "submitComplaint: Error while downloading url")
                            }
                        } else
                            Log.d(TAG, "submitComplaint: Some error occurred while uploading image")
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
                } else
                    Log.d(
                        TAG,
                        "submitComplaint: some error occurred while uploading complaint "
                    )
            }
    }

    private fun linkToUser(key: String?) {
        database.reference.child(USERS).child(auth.uid!!)
            .child(COMPLAINTS).child(Date().time.toString())
            .setValue(key)
            .addOnCompleteListener { userLinkTask ->
                if (userLinkTask.isSuccessful)
                    Log.d(
                        TAG,
                        "submitComplaint: linked to user successfully"
                    )
                else
                    Log.d(
                        TAG,
                        "submitComplaint: linked to user failure"
                    )
            }
    }

    private fun linkToComplaintsByLocations(key: String?) {
        database.reference.child(COMPLAINTS_BY_LOCATIONS)
            .child(complaint.address.country)
            .child(complaint.address.state)
            .child(complaint.address.district)
            .child(complaint.address.cityOrVillage)
            .setValue(key)
            .addOnCompleteListener { byLocationTask ->
                if (byLocationTask.isSuccessful)
                    Log.d(
                        TAG,
                        "submitComplaint: linked to location wise successfully"
                    )
                else
                    Log.d(
                        TAG,
                        "submitComplaint: linked to location wise failed"
                    )
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
        dialog.setTitle("Loading data")
        dialog.setMessage("Please wait...")
        dialog.setCancelable(false)
    }

    private fun onComplaintSubmittedSuccessfully() {
        Log.d(TAG, "Complaint submitted")
        findNavController().navigate(NewComplaintFragmentDirections.actionNewComplaintFragmentToHomeFragment())
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GET_IMAGE_REQUEST_CODE) {
            if (data != null) {
                data.data?.let { imagesUris.add(it) }
                binding.rvSelectImage.adapter?.notifyDataSetChanged()
            }
        }
    }
}