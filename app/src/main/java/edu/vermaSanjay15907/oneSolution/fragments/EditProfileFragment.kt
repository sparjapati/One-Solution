package edu.vermaSanjay15907.oneSolution.fragments

import android.annotation.SuppressLint
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
import edu.vermaSanjay15907.oneSolution.utils.Konstants.DETAILS_OK
import edu.vermaSanjay15907.oneSolution.utils.Konstants.PROFILE_DETAILS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.USERS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.showSnackBar

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(layoutInflater, container, false)
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
                val res = validateDetails()
                if (res == DETAILS_OK) {
                    activity?.let { showSnackBar(it, res, false) }
                    item.isEnabled = false
                    updateUserDetails()
                } else {
                    activity?.let { showSnackBar(it, res, true) }
                }
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
                                activity?.let { it1 ->
                                    showSnackBar(
                                        it1,
                                        "Some Error Occurred while updating your profile"
                                    )
                                }
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


    private fun validateDetails(): String {
        return DETAILS_OK
    }
}