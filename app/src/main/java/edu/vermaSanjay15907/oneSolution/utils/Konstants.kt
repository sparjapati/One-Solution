package edu.vermaSanjay15907.oneSolution.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import edu.vermaSanjay15907.oneSolution.R


object Konstants {
    const val TAG = "parjapat"
    const val STATUS_PENDING = "status_pending"
    const val STATUS_SOLVED = "status_solved"
    const val DETAILS_OK = "details_ok"
    const val USERS = "users"
    const val COMPLAINTS = "complaints"
    const val COMPLAINT_IMAGES = "complaint_images"
    const val COMPLAINTS_BY_LOCATIONS = "complaints_by_locations"
    const val PROFILE_DETAILS = "profile_details"
    const val GET_IMAGE_REQUEST_CODE = 100

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