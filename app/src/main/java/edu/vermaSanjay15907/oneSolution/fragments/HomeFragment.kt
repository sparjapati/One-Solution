package edu.vermaSanjay15907.oneSolution.fragments

import android.content.Intent
import android.os.Bundle
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
import edu.vermaSanjay15907.oneSolution.utils.Konstants.COMPLAINTS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.PROFILE_DETAILS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.USERS

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var currUser: User? = null

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
            activity?.let { HomeActivityComplaintListAdapter(it, complaints) }
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvComplaints.layoutManager = layoutManager
        binding.rvComplaints.isNestedScrollingEnabled = false
        binding.rvComplaints.adapter = homeActivityComplaintListAdapter

        database.reference.child(USERS).child(auth.uid!!).child(PROFILE_DETAILS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    currUser = snapshot.getValue(User::class.java)
                    currUser?.apply {
                        if (isOfficer)
                            getOfficerComplaints(complaints, homeActivityComplaintListAdapter)
                        else
                            getUserComplaints(complaints, homeActivityComplaintListAdapter)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        binding
            .btnAddNewComplaint.setOnClickListener {
                Toast.makeText(activity, "Adding a new Complaint", Toast.LENGTH_SHORT).show()
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNewComplaintFragment())
            }
        return binding.root
    }

    private fun getUserComplaints(
        complaints: ArrayList<Complaint>,
        homeActivityComplaintListAdapter: HomeActivityComplaintListAdapter?
    ) {
        database.reference.child(COMPLAINTS)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    complaints.clear()
                    for (dataSnapshot in snapshot.children) {
                        dataSnapshot.getValue(Complaint::class.java)
                            ?.let { complaints.add(it) }
                    }
                    homeActivityComplaintListAdapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun User.getOfficerComplaints(
        complaints: ArrayList<Complaint>,
        homeActivityComplaintListAdapter: HomeActivityComplaintListAdapter?
    ) {
        database.reference.child(address.country).child(address.state)
            .child(address.district).child(address.cityOrVillage)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapshot in snapshot.children) {
                        complaints.clear()
                        val complaintId = dataSnapshot.getValue(String::class.java)
                        complaintId?.let {
                            database.reference.child(COMPLAINTS).child(complaintId)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(complaintSnapshot: DataSnapshot) {
                                        val complaint =
                                            complaintSnapshot.getValue(Complaint::class.java)
                                        complaint?.let {
                                            complaints.add(it)
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }
                                })
                        }
                        homeActivityComplaintListAdapter?.notifyDataSetChanged()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_fragment_menu, menu)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miSignOut -> {
                auth.signOut()
                Toast.makeText(activity, "Signed out successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(activity, LoginActivity::class.java))
                return true
            }
            R.id.aboutFragment -> {
                return NavigationUI.onNavDestinationSelected(
                    item,
                    requireView().findNavController()
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }
}