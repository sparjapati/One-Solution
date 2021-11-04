package edu.vermaSanjay15907.oneSolution.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.vermaSanjay15907.oneSolution.R
import edu.vermaSanjay15907.oneSolution.activities.LoginActivity
import edu.vermaSanjay15907.oneSolution.adapters.HomeActivityComplaintListAdapter
import edu.vermaSanjay15907.oneSolution.databinding.FragmentHomeBinding
import edu.vermaSanjay15907.oneSolution.models.Complaint
import edu.vermaSanjay15907.oneSolution.models.User
import edu.vermaSanjay15907.oneSolution.utils.Konstants
import edu.vermaSanjay15907.oneSolution.utils.Konstants.COMPLAINTS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.COMPLAINTS_BY_LOCATIONS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.PROFILE_DETAILS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.USERS

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var currUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val complaints = ArrayList<Complaint>()

        val homeActivityComplaintListAdapter =
            setComplaintListAdapter(complaints)

        database.reference.child(USERS).child(auth.uid!!).child(PROFILE_DETAILS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    currUser = snapshot.getValue(User::class.java)!!
                    if (currUser.isOfficer) {
                        getOfficerComplaints(currUser, complaints, homeActivityComplaintListAdapter)
                    } else
                        getUserComplaints(complaints, homeActivityComplaintListAdapter)

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        binding.btnAddNewComplaint.setOnClickListener {
            Toast.makeText(activity, "Adding a new Complaint", Toast.LENGTH_SHORT).show()
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNewComplaintFragment())
        }
        return binding.root
    }

    private fun setComplaintListAdapter(complaints: ArrayList<Complaint>): HomeActivityComplaintListAdapter {
        val homeActivityComplaintListAdapter =
            HomeActivityComplaintListAdapter(requireActivity(), complaints)
        val layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvComplaints.layoutManager = layoutManager
        binding.rvComplaints.isNestedScrollingEnabled = false
        binding.rvComplaints.adapter = homeActivityComplaintListAdapter
        return homeActivityComplaintListAdapter
    }

    private fun getUserComplaints(
        complaints: ArrayList<Complaint>,
        homeActivityComplaintListAdapter: HomeActivityComplaintListAdapter?
    ) {
//        database.reference.child(COMPLAINTS).addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (dataSnapshot in snapshot.children) {
//                    val complaint = dataSnapshot.getValue(Complaint::class.java)
//                    if (complaint != null) {
//                        complaints.add(complaint)
//                        homeActivityComplaintListAdapter?.notifyDataSetChanged()
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })

        database.reference.child(USERS).child(auth.uid!!).child(COMPLAINTS)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(complaintsIdSnapshot: DataSnapshot) {
                    complaints.clear()
                    for (complaintIdDataSnapshot in complaintsIdSnapshot.children) {
                        val cid = complaintIdDataSnapshot.value as String
                        FirebaseDatabase.getInstance().reference.child(COMPLAINTS).child(cid)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(complaintSnapshot: DataSnapshot) {
                                    val complaint =
                                        complaintSnapshot.getValue(Complaint::class.java)
                                    if (complaint != null) {
                                        complaints.add(complaint)
                                        homeActivityComplaintListAdapter?.notifyDataSetChanged()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun getOfficerComplaints(
        currUser: User,
        complaints: ArrayList<Complaint>,
        homeActivityComplaintListAdapter: HomeActivityComplaintListAdapter
    ) {
        Log.d(Konstants.TAG, "getOfficerComplaints: Receiving officer complaints")
        currUser.address.apply {
            database.reference.child(COMPLAINTS_BY_LOCATIONS).child(country).child(state)
                .child(district).child(cityOrVillage)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(allComplaints: DataSnapshot) {
                        for (c in allComplaints.children) {
                            val complaintId = c.value.toString()
                            database.reference.child(COMPLAINTS).child(complaintId)
                                .addListenerForSingleValueEvent(
                                    object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val complaint = snapshot.getValue(Complaint::class.java)
                                            complaint?.let {
                                                complaints.add(it)
                                                homeActivityComplaintListAdapter.notifyDataSetChanged()
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                        }
                                    })
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_fragment_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miSignOut -> {
                auth.signOut()
                Toast.makeText(activity, "Signed out successfully", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(activity, LoginActivity::class.java))
                return true
            }
            R.id.aboutFragment -> {
                return NavigationUI.onNavDestinationSelected(
                    item,
                    requireView().findNavController()
                )
            }
            R.id.editProfileFragment -> {
                return NavigationUI.onNavDestinationSelected(
                    item,
                    requireView().findNavController()
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }
}