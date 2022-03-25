package com.example.firebaseauthenticationtutorial.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.example.firebaseauthenticationtutorial.R
import com.example.firebaseauthenticationtutorial.databinding.FragmentVerifyPhoneBinding
import com.example.firebaseauthenticationtutorial.toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class VerifyPhoneFragment : Fragment() {
    private lateinit var binding: FragmentVerifyPhoneBinding
    private var verificationId: String? = null
    private var TAG= VerifyPhoneFragment::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_verify_phone, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutPhone.visibility = View.VISIBLE
        binding.layoutVerification.visibility = View.INVISIBLE

        binding.buttonSendVerification.setOnClickListener {
            val phone = binding.editTextPhone.text.toString().trim()

            if (phone.isEmpty() || phone.length != 10) {
                binding.editTextPhone.error = "Enter a valid phone"
                binding.editTextPhone.requestFocus()
                return@setOnClickListener
            }

            val phoneNumber = '+' + binding.ccp.selectedCountryCode + phone

            PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(
                    phoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    requireActivity(),
                    phoneAuthCallbacks
                )

            binding.layoutPhone.visibility = View.GONE
            binding.layoutVerification.visibility = View.VISIBLE
        }

        binding.buttonVerify.setOnClickListener {
            val code = binding.editTextCode.text.toString().trim()

            if(code.isEmpty()){
                binding.editTextCode.error = "Code required"
                binding.editTextCode.requestFocus()
                return@setOnClickListener
            }

            verificationId?.let{
                val credential = PhoneAuthProvider.getCredential(it, code)
                addPhoneNumber(credential)
            }
        }
        
    }

    private val phoneAuthCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                addPhoneNumber(phoneAuthCredential)
            }

            override fun onVerificationFailed(exception: FirebaseException) {
                context?.toast(exception.message!!)
                Log.v(TAG, exception.message!!)
            }


            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                this@VerifyPhoneFragment.verificationId = verificationId
            }
        }

    private fun addPhoneNumber(phoneAuthCredential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().currentUser?.updatePhoneNumber(phoneAuthCredential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    context?.toast("Phone added")
                    val action = VerifyPhoneFragmentDirections.actionPhoneVerified()
                    Navigation.findNavController(binding.buttonVerify).navigate(action)
                } else {
                    context?.toast(task.exception?.message!!)
                }
            }
    }
}