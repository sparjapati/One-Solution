package edu.vermaSanjay15907.oneSolution.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
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

        setSpinnerAdapters()
        return binding.root
    }

    private fun setSpinnerAdapters() {
        val stateAdapter = ArrayAdapter.createFromResource(
            activity,
            R.array.array_indian_states,
            R.layout.spinner_layout
        )

        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spState.adapter = stateAdapter

        binding.spState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedState = binding.spState.selectedItem.toString()
                Log.d("TAG", "onItemSelected: $selectedState")
                val parentId = parent!!.id
                if (parentId == binding.spState.id) {
                    val districtAdapter: ArrayAdapter<CharSequence>
                    when (selectedState) {
                        "Select Your State" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_haryana_districts,
                            R.layout.spinner_layout
                        )
                        "Andhra Pradesh" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_andhra_pradesh_districts,
                            R.layout.spinner_layout
                        )
                        "Arunachal Pradesh" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_arunachal_pradesh_districts,
                            R.layout.spinner_layout
                        )
                        "Assam" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_assam_districts,
                            R.layout.spinner_layout
                        )
                        "Bihar" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_bihar_districts,
                            R.layout.spinner_layout
                        )
                        "Chhattisgarh" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_chhattisgarh_districts,
                            R.layout.spinner_layout
                        )
                        "Goa" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_goa_districts,
                            R.layout.spinner_layout
                        )
                        "Gujarat" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_gujarat_districts,
                            R.layout.spinner_layout
                        )
                        "Haryana" -> {
                            districtAdapter = ArrayAdapter.createFromResource(
                                parent.context,
                                R.array.array_haryana_districts,
                                R.layout.spinner_layout
                            )
                        }
                        "Himachal Pradesh" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_himachal_pradesh_districts,
                            R.layout.spinner_layout
                        )
                        "Jharkhand" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_jharkhand_districts,
                            R.layout.spinner_layout
                        )
                        "Karnataka" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_karnataka_districts,
                            R.layout.spinner_layout
                        )
                        "Kerala" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_kerala_districts,
                            R.layout.spinner_layout
                        )
                        "Madhya Pradesh" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_madhya_pradesh_districts,
                            R.layout.spinner_layout
                        )
                        "Maharashtra" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_maharashtra_districts,
                            R.layout.spinner_layout
                        )
                        "Manipur" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_manipur_districts,
                            R.layout.spinner_layout
                        )
                        "Meghalaya" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_meghalaya_districts,
                            R.layout.spinner_layout
                        )
                        "Mizoram" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_mizoram_districts,
                            R.layout.spinner_layout
                        )
                        "Nagaland" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_nagaland_districts,
                            R.layout.spinner_layout
                        )
                        "Odisha" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_odisha_districts,
                            R.layout.spinner_layout
                        )
                        "Punjab" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_punjab_districts,
                            R.layout.spinner_layout
                        )
                        "Rajasthan" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_rajasthan_districts,
                            R.layout.spinner_layout
                        )
                        "Sikkim" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_sikkim_districts,
                            R.layout.spinner_layout
                        )
                        "Tamil Nadu" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_tamil_nadu_districts,
                            R.layout.spinner_layout
                        )
                        "Telangana" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_telangana_districts,
                            R.layout.spinner_layout
                        )
                        "Tripura" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_tripura_districts,
                            R.layout.spinner_layout
                        )
                        "Uttar Pradesh" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_uttar_pradesh_districts,
                            R.layout.spinner_layout
                        )
                        "Uttarakhand" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_uttarakhand_districts,
                            R.layout.spinner_layout
                        )
                        "West Bengal" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_west_bengal_districts,
                            R.layout.spinner_layout
                        )
                        "Andaman and Nicobar Islands" -> districtAdapter =
                            ArrayAdapter.createFromResource(
                                parent.context,
                                R.array
                                    .array_andaman_nicobar_districts,
                                R.layout.spinner_layout
                            )
                        "Chandigarh" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_chandigarh_districts,
                            R.layout.spinner_layout
                        )
                        "Dadra and Nagar Haveli" -> districtAdapter =
                            ArrayAdapter.createFromResource(
                                parent.context,
                                R.array
                                    .array_dadra_nagar_haveli_districts,
                                R.layout.spinner_layout
                            )
                        "Daman and Diu" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_daman_diu_districts,
                            R.layout.spinner_layout
                        )
                        "Delhi" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_delhi_districts,
                            R.layout.spinner_layout
                        )
                        "Jammu and Kashmir" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_jammu_kashmir_districts,
                            R.layout.spinner_layout
                        )
                        "Lakshadweep" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_lakshadweep_districts,
                            R.layout.spinner_layout
                        )

                        "Ladakh" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_ladakh_districts,
                            R.layout.spinner_layout
                        )
                        "Puducherry" -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_puducherry_districts,
                            R.layout.spinner_layout
                        )
                        else -> districtAdapter = ArrayAdapter.createFromResource(
                            parent.context,
                            R.array
                                .array_default_districts,
                            R.layout.spinner_layout
                        )
                    }
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spDistrict.adapter = districtAdapter
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
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
            if (rgGender.checkedRadioButtonId == -1) {
                showSnackBar(activity, "Please Select Your Gender", true)
                return false
            }
            if (spState.selectedItem.toString() == Konstants.DEFAULT_STATE) {
                showSnackBar(activity, "Please Enter Your state", true)
                return false
            }
            if (spDistrict.selectedItem.toString() == Konstants.DEFAULT_DISTRICT) {
                showSnackBar(activity, "Please Enter Your district", true)
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
            user.gender =
                rgGender.findViewById<RadioButton>(rgGender.checkedRadioButtonId).text.toString()
            user.address.country = "India"
            user.address.state = spState.selectedItem.toString()
            user.address.district = spDistrict.selectedItem.toString()
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