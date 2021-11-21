package edu.vermaSanjay15907.oneSolution.fragments

import android.content.ActivityNotFoundException
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import edu.vermaSanjay15907.oneSolution.R
import edu.vermaSanjay15907.oneSolution.databinding.FragmentFeedbackBinding
import android.widget.Toast

import androidx.test.core.app.ApplicationProvider.getApplicationContext

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth


class FeedbackFragment : Fragment() {

    private lateinit var binding: FragmentFeedbackBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedbackBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.feedback_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.miSendFeedback) {
            val message = binding.etmessage.text.toString()
            if (message.isNotEmpty())
                sendEmail(message)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendEmail(message: String) {
        Toast.makeText(activity, "Sending email $message", Toast.LENGTH_SHORT).show()
        val emailIntent = Intent(Intent.ACTION_SENDTO)
//        emailIntent.type = "message/rfc822"
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("one.solution.help.nitkkr@gmail.com"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback from App")
        emailIntent.putExtra(
            Intent.EXTRA_TEXT,
            "From ${FirebaseAuth.getInstance().currentUser!!.uid}\nMessage:\n\t${message}"
        )
        try {
            startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"))
        } catch (e: Exception) {
            Toast.makeText(activity, "No Suitable app found on your app", Toast.LENGTH_SHORT).show()
        }

    }
}