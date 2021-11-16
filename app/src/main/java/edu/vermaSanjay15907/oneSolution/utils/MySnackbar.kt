package edu.vermaSanjay15907.oneSolution.utils

import android.app.Activity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import edu.vermaSanjay15907.oneSolution.R

class MySnackBar(activity: Activity, text: String, isError: Boolean) {
    private var snackBar: Snackbar =
        Snackbar.make(activity.findViewById(R.id.content), text, Snackbar.LENGTH_LONG)

    init {
        snackBar.view.setBackgroundColor(
            if (isError)
                ContextCompat.getColor(
                    activity,
                    R.color.colorSnackbarError
                ) else
                ContextCompat.getColor(
                    activity,
                    R.color.colorSnackbarSuccess
                )
        )
    }

    fun show()
    {
        snackBar.show()
    }
}