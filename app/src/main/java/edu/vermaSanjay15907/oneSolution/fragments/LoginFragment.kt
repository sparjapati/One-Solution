package edu.vermaSanjay15907.oneSolution.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import edu.vermaSanjay15907.oneSolution.databinding.FragmentLoginBinding
import edu.vermaSanjay15907.oneSolution.utils.Konstants.TAG

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var activity: Activity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        activity = requireActivity()


        binding.btnLogin.setOnClickListener {
            verifyOtp(binding.etOtp.text.toString())
        }

        binding.tilPhoneNumber.setEndIconOnClickListener {
            sendOtp(binding.tilPhoneNumber.prefixText.toString() + binding.etPhoneNumber.text.toString())
        }

        return binding.root
    }

    private fun sendOtp(phoneNumber: String) {
        Log.d(TAG, "otp sent")
    }

    private fun verifyOtp(code: String) {
        Log.d(TAG, "verifyOtp: verification complete")
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToNewRegistrationFragment(binding.etPhoneNumber.text.toString()))
    }
}