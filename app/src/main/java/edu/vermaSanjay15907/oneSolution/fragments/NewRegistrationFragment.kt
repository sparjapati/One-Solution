package edu.vermaSanjay15907.oneSolution.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import edu.vermaSanjay15907.oneSolution.activities.HomeActivity
import edu.vermaSanjay15907.oneSolution.databinding.FragmentNewRegistrationBinding
import edu.vermaSanjay15907.oneSolution.models.User
import edu.vermaSanjay15907.oneSolution.utils.Konstants.PROFILE_DETAILS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.TAG
import edu.vermaSanjay15907.oneSolution.utils.Konstants.USERS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.showSnackBar

class NewRegistrationFragment : Fragment() {
    private lateinit var binding: FragmentNewRegistrationBinding
    private lateinit var phoneNumber: String
    private lateinit var activity: Activity
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initialisation(container)

        val args = arguments?.let { NewRegistrationFragmentArgs.fromBundle(it) }
        phoneNumber = args!!.phoneNumber
        binding.etRegisteredNumber.setText(phoneNumber)

        binding.btnSignUp.setOnClickListener {
            if (validateDetails()) {
                binding.btnSignUp.isEnabled = false
                val user = getUserDetails()
                registerUser(user)
            }
        }

        return binding.root
    }

    private fun validateDetails(): Boolean {
        binding.apply {
            val BLANK = ""
            if (etFirstName.text.toString() == BLANK) {
                showSnackBar(activity, "Please Enter First Name", true)
                etFirstName.requestFocus()
                return false
            }
            if (etLastName.text.toString() == BLANK) {
                showSnackBar(activity, "Please Enter Last Name", true)
                etLastName.requestFocus()
                return false
            }
            if (rgGender.checkedRadioButtonId == -1) {
                showSnackBar(activity, "Please Select Your Gender", true)
                return false
            }
            if (etState.text.toString() == BLANK) {
                showSnackBar(activity, "Please Enter Your state", true)
                etState.requestFocus()
                return false
            }
            if (etDistrict.text.toString() == BLANK) {
                showSnackBar(activity, "Please Enter Your district", true)
                etDistrict.requestFocus()
                return false
            }
            if (etCity.text.toString() == BLANK) {
                showSnackBar(activity, "Please Enter Your city/village name", true)
                etCity.requestFocus()
                return false
            }
        }
        return true
    }

    private fun initialisation(container: ViewGroup?) {
        binding = FragmentNewRegistrationBinding.inflate(layoutInflater, container, false)
        activity = requireActivity()
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
    }

    private fun getUserDetails(): User {
        val user = User()
        binding.apply {
            user.fname = etFirstName.text.toString()
            user.lname = etLastName.text.toString()
            user.mobileNumber = etRegisteredNumber.text.toString()
            user.gender =
                rgGender.findViewById<RadioButton>(rgGender.checkedRadioButtonId).text.toString()
            user.address.country = "India"
            user.address.state = etState.text.toString()
            user.address.district = etDistrict.text.toString()
            user.address.cityOrVillage = etCity.text.toString()
            user.address.nearByLocation = etNearByLocation.text.toString()
            user.isOfficer = binding.swIsOfficer.isChecked
        }
        return user
    }

    private fun registerUser(user: User) {
        val uid = auth.uid!!
        database.reference.child(USERS).child(uid).child(PROFILE_DETAILS).setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    onRegistrationSuccessful()
                else
                    onRegistrationFailure()
            }
    }

    private fun onRegistrationFailure() {
        Toast.makeText(activity, "Registration Failed\n Please try again...", Toast.LENGTH_SHORT)
            .show()
        binding.btnSignUp.isEnabled = true
    }

    private fun onRegistrationSuccessful() {
        Log.d(TAG, "onCreateView: Registration finished")
        startActivity(Intent(activity, HomeActivity::class.java))
        activity.finish()
    }
}