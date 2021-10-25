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
import edu.vermaSanjay15907.oneSolution.adapters.SelectImageRecyclerViewAdapter
import edu.vermaSanjay15907.oneSolution.databinding.FragmentNewComplaintBinding
import edu.vermaSanjay15907.oneSolution.models.Complaint
import edu.vermaSanjay15907.oneSolution.utils.Konstants.GET_IMAGE_REQUEST_CODE
import edu.vermaSanjay15907.oneSolution.utils.Konstants.TAG
import java.util.*
import kotlin.collections.ArrayList

class NewComplaintFragment : Fragment() {


    private lateinit var binding: FragmentNewComplaintBinding
    private var complaint = Complaint()
    private var imagesUris = ArrayList<Uri>()
    private var imagesUrls = ArrayList<String>()
    private lateinit var dialog: ProgressDialog


    override fun onResume() {
        super.onResume()
        activity?.actionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewComplaintBinding.inflate(layoutInflater, container, false)
        dialog = ProgressDialog(activity)
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog.setTitle("Loading data")
        dialog.setMessage("Please wait...")
        dialog.setCancelable(false)

        binding.btnAddImage.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, GET_IMAGE_REQUEST_CODE)
        }

        binding.btnSubmit.setOnClickListener {
            binding.btnSubmit.isEnabled = false
            complaint.apply {
//                complainedBy = auth.uid!!
                address.country = binding.etCountry.text.toString()
                address.state = binding.etState.text.toString()
                address.district = binding.etDistrict.text.toString()
                address.cityOrVillage = binding.etCity.text.toString()
                address.nearByLocation = binding.etNearByLocation.text.toString()
                description = binding.etDescription.text.toString()
                date = Date().time.toString()
            }
            Log.d(TAG, "onCreateView: Complaint added")
            onComplaintSubmitted()
        }

        val selectImageRecyclerViewAdapter =
            SelectImageRecyclerViewAdapter(requireActivity(), imagesUris)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvSelectImage.layoutManager = layoutManager
        binding.rvSelectImage.adapter = selectImageRecyclerViewAdapter

        return binding.root
    }

    private fun onComplaintSubmitted() {
        binding.btnSubmit.isEnabled = true
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