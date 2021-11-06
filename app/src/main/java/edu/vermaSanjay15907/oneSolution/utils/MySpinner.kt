package edu.vermaSanjay15907.oneSolution.utils

import android.app.Activity
import android.app.ProgressDialog

class MySpinner(activity: Activity, title: String, message: String) {
    private var dialog: ProgressDialog = ProgressDialog(activity)

    init {
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog.setTitle(title)
        dialog.setMessage(message)
        dialog.setCancelable(false)
    }

    public fun startLoading() {
        dialog.show()
    }

    public fun dismissDialog() {
        dialog.dismiss()
    }
}