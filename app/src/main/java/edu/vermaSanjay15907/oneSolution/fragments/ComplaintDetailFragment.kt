package edu.vermaSanjay15907.oneSolution.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import edu.vermaSanjay15907.oneSolution.adapters.ComplaintImagesAdapter
import edu.vermaSanjay15907.oneSolution.databinding.FragmentComplaintDetailBinding

class ComplaintDetailFragment : Fragment() {
    private lateinit var binding: FragmentComplaintDetailBinding
    private lateinit var complaintId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentComplaintDetailBinding.inflate(layoutInflater, container, false)

        var args = arguments?.let { ComplaintDetailFragmentArgs.fromBundle(it) }
        complaintId = args!!.complaintId

        val images = ArrayList<String>()
        val adapter = ComplaintImagesAdapter(requireActivity(), images)
        val layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvImages.layoutManager = layoutManager
        binding.rvImages.isNestedScrollingEnabled = false
        binding.rvImages.adapter = adapter

        return binding.root

    }
}