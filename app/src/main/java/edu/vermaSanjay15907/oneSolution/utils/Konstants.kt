package edu.vermaSanjay15907.oneSolution.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager


object Konstants {
    const val TAG = "parjapat"
    const val STATUS_PENDING = "status_pending"
    const val STATUS_SOLVED = "status_solved"
    const val USERS = "users"
    const val COMPLAINTS = "complaints"
    const val COMPLAINT_IMAGES="complaint_images"
    const val COMPLAINTS_BY_LOCATIONS="complaints_by_locations"
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
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
    }
}