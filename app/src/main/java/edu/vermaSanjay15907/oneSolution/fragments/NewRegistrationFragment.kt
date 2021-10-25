package edu.vermaSanjay15907.oneSolution.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.vermaSanjay15907.oneSolution.activities.HomeActivity
import edu.vermaSanjay15907.oneSolution.databinding.FragmentNewRegistrationBinding
import edu.vermaSanjay15907.oneSolution.models.User
import edu.vermaSanjay15907.oneSolution.utils.Konstants.TAG

class NewRegistrationFragment : Fragment() {


    private lateinit var binding: FragmentNewRegistrationBinding
    private lateinit var phoneNumber: String
    private lateinit var activity: Activity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewRegistrationBinding.inflate(layoutInflater, container, false)
        activity = requireActivity()
        val args = arguments?.let { NewRegistrationFragmentArgs.fromBundle(it) }
        phoneNumber = args!!.phoneNumber
        binding.etRegisteredNumber.text = phoneNumber

        binding.btnSignUp.setOnClickListener {
            val user = User()
            binding.apply {
                user.fname = etFirstName.text.toString()
                user.lname = etLastName.text.toString()
                user.mobileNumber = etRegisteredNumber.text.toString()
                user.gender = etGender.text.toString()
                user.address.country = "India"
                user.address.state = etState.text.toString()
                user.address.district = etDistrict.text.toString()
                user.address.cityOrVillage = etCity.text.toString()
                user.address.nearByLocation = etNearByLocation.text.toString()
                user.isOfficer = binding.swIsOfficer.isChecked
            }
            Log.d(TAG, "onCreateView: Regsitration finished")
            startActivity(Intent(activity, HomeActivity::class.java))
            activity.finish()
        }

        return binding.root
    }
}