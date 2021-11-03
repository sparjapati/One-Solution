package edu.vermaSanjay15907.oneSolution.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.vermaSanjay15907.oneSolution.R
import edu.vermaSanjay15907.oneSolution.databinding.FragmentEditProfileBinding
import edu.vermaSanjay15907.oneSolution.models.User
import edu.vermaSanjay15907.oneSolution.utils.Konstants
import edu.vermaSanjay15907.oneSolution.utils.Konstants.PROFILE_DETAILS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.USERS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.showSnackBar

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding

    private lateinit var activity: Activity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(layoutInflater, container, false)
        activity = requireActivity()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_profile_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btnSave -> {
                if (validateDetails())
                    updateUserDetails()
            }
        }
        return true
    }

    private fun updateUserDetails() {
        val newUserDetails = getUserDetails()
        FirebaseAuth.getInstance().uid?.let {
            FirebaseDatabase.getInstance().reference.child(USERS).child(it).child(
                PROFILE_DETAILS
            ).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currUser = snapshot.getValue(User::class.java)
                    if (currUser != null) {
                        newUserDetails.isOfficer = currUser.isOfficer
                        FirebaseDatabase.getInstance().reference.child(USERS).child(it).child(
                            PROFILE_DETAILS
                        ).setValue(newUserDetails).addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                onUpdateSuccessfully()
                            } else {
                                showSnackBar(
                                    activity,
                                    "Some Error Occurred while updating your profile"
                                )
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
        onUpdateSuccessfully()
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

    private fun getUserDetails(): User {
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
        }
        return user
    }

    private fun onUpdateSuccessfully() {
        activity?.let { it1 ->
            showSnackBar(
                it1,
                "Updated Successfully"
            )
        }
        findNavController().navigate(EditProfileFragmentDirections.actionEditProfileFragmentToHomeFragment())
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Konstants.GET_IMAGE_REQUEST_CODE) {
            if (data != null) {
                data.data?.let {
                }
            }
        }
    }
}