package edu.vermaSanjay15907.oneSolution.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.vermaSanjay15907.oneSolution.activities.HomeActivity
import edu.vermaSanjay15907.oneSolution.databinding.FragmentLoginBinding
import edu.vermaSanjay15907.oneSolution.utils.Konstants.USERS
import edu.vermaSanjay15907.oneSolution.utils.Konstants.hideKeyboard
import java.util.concurrent.TimeUnit

const val TIME_OUT = 60L

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var activity: Activity

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        activity = requireActivity()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.tilPhoneNumber.setEndIconOnClickListener {
            sendOtp(binding.tilPhoneNumber.prefixText.toString() + binding.etPhoneNumber.text.toString())
        }

        binding.btnLogin.setOnClickListener {
            hideKeyboard(activity)
            verifyOtp(binding.etOtp.text.toString())
        }

        return binding.root
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            onOtpSent()
            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
        }

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            val code = credential.smsCode
            if (code != null) {
                onOtpReceived(code)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            if (e is FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(activity, "Some Error occurred!!!", Toast.LENGTH_SHORT).show()
            } else if (e is FirebaseTooManyRequestsException) {
                Toast.makeText(activity, "Too many request sent", Toast.LENGTH_SHORT)
                    .show()
                onInvalidOtpEntered()
            }
        }
    }

    private fun onOtpReceived(code: String) {
        binding.etOtp.setText(code)
        hideKeyboard(activity)
    }

    private fun sendOtp(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(TIME_OUT, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun onOtpSent() {
        Toast.makeText(activity, "OTP sent Successfully", Toast.LENGTH_SHORT).show()
        binding.etOtp.requestFocus()
    }

    private fun verifyOtp(code: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful)
                onVerificationCompleted(task)
            else
                onInvalidOtpEntered()
        }
    }

    private fun onVerificationCompleted(task: Task<AuthResult>) {
        Toast.makeText(activity, "Verification Successful", Toast.LENGTH_SHORT).show()
        val uid = task.result?.user?.uid!!
        database.reference.child(USERS).child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())
                        navigateToHomeFragment()
                    else
                        navigateToNewRegistrationFragment()

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun navigateToNewRegistrationFragment() {
        findNavController().navigate(
            LoginFragmentDirections.actionLoginFragmentToNewRegistrationFragment(
                binding.tilPhoneNumber.prefixText.toString() + binding.etPhoneNumber.text.toString()
            )
        )
    }

    private fun navigateToHomeFragment() {
        val intent = Intent(activity, HomeActivity::class.java)
        startActivity(intent)
        activity.finish()
    }

    private fun onInvalidOtpEntered() {
        binding.etOtp.setTextColor(Color.RED)
    }

    
}