package edu.vermaSanjay15907.oneSolution.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import edu.vermaSanjay15907.oneSolution.R
import edu.vermaSanjay15907.oneSolution.models.User


object Konstants {
    const val TAG = "parjapat"
    const val STATUS = "status"
    const val STATUS_PENDING = "status_pending"
    const val STATUS_SOLVED = "status_solved"
    const val DETAILS_OK = "details_ok"
    const val SOLVED_BY = "solvedBy"
    const val USERS = "users"
    const val COMPLAINTS = "complaints"
    const val COMPLAINT_IMAGES = "complaint_images"
    const val COMPLAINTS_BY_LOCATIONS = "complaints_by_locations"
    const val PROFILE_DETAILS = "profile_details"
    const val GET_IMAGE_REQUEST_CODE = 100
    const val WORK_DOCUMENTS = "workImages"
    const val DEFAULT_STATE = "Select Your State"
    const val DEFAULT_DISTRICT = "Select Your District"
    const val BLANK = ""

    val databaseReference by lazy {
        FirebaseDatabase.getInstance().reference
    }

    val complaintsReference by lazy {
        databaseReference.child(COMPLAINTS)
    }

    val complaintByLocationReference by lazy {
        databaseReference.child(COMPLAINTS_BY_LOCATIONS)
    }

    val usersReference by lazy {
        databaseReference.child(USERS)
    }

    val auth by lazy {
        FirebaseAuth.getInstance()
    }

    val currUser by lazy {
        var user: User? = null
        auth.uid?.let {
            usersReference.child(it).child(PROFILE_DETAILS)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        user = snapshot.getValue(User::class.java)
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
        user
    }

    val storageReference by lazy {
        FirebaseStorage.getInstance().reference
    }

    val complaintsImagesReference by lazy {
        storageReference.child(COMPLAINT_IMAGES)
    }

    val initialImagesReference by lazy {
        complaintsImagesReference.child(COMPLAINT_IMAGES)
    }

    val workDocumentReference by lazy {
        complaintsImagesReference.child(WORK_DOCUMENTS)
    }

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showSnackBar(
        activity: Activity,
        message: String,
        isError: Boolean = true
    ) {
        val snackBar =
            Snackbar.make(
                activity.findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_LONG
            )
        val color = if (isError)
            ContextCompat.getColor(activity, R.color.colorSnackbarError)
        else
            ContextCompat.getColor(activity, R.color.colorSnackbarSuccess)
        snackBar.view.setBackgroundColor(color)
        snackBar.show()
    }
}

//fun View.isUserInteractionEnabled(enabled: Boolean) {
//    isUserInteractionEnabled(enabled)
//    if (this is ViewGroup && this.childCount > 0) {
//        this.children.forEach {
//            it.isUserInteractionEnabled(enabled)
//        }
//    }
//}