package edu.vermaSanjay15907.oneSolution.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import edu.vermaSanjay15907.oneSolution.R

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun showSnackBar(message: String, isError: Boolean = true) {
        val snackbar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view

        if (isError) {
            snackbarView.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorSnackbarError
                )
            )
        } else
            snackbarView.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorSnackbarSuccess
                )
            )

        snackbar.show()
    }


}