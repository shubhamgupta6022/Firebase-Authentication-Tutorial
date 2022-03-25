package com.example.firebaseauthenticationtutorial.ui.fragments

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.example.firebaseauthenticationtutorial.R
import com.example.firebaseauthenticationtutorial.databinding.FragmentUpdateEmailBinding
import com.example.firebaseauthenticationtutorial.toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class UpdateEmailFragment : Fragment() {
    private lateinit var binding: FragmentUpdateEmailBinding
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_update_email, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutPassword.visibility = View.VISIBLE
        binding.layoutUpdateEmail.visibility = View.GONE

        binding.buttonAuthenticate.setOnClickListener {
            val password = binding.editTextPassword.text.toString().trim()

            if (password.isEmpty()) {
                binding.editTextPassword.error = "Password required"
                binding.editTextPassword.requestFocus()
                return@setOnClickListener
            }


            currentUser?.let { user ->
                val credential = EmailAuthProvider.getCredential(user.email!!, password)
                binding.progressbar.visibility = View.VISIBLE
                user.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        binding.progressbar.visibility = View.GONE
                        when {
                            task.isSuccessful -> {
                                binding.layoutPassword.visibility = View.GONE
                                binding.layoutUpdateEmail.visibility = View.VISIBLE
                            }
                            task.exception is FirebaseAuthInvalidCredentialsException -> {
                                binding.editTextPassword.error = "Invalid Password"
                                binding.editTextPassword.requestFocus()
                            }
                            else -> context?.toast(task.exception?.message!!)
                        }
                    }
            }
        }

        binding.buttonUpdate.setOnClickListener { view ->
            val email = binding.editTextEmail.text.toString().trim()

            if (email.isEmpty()) {
                binding.editTextEmail.error = "Email Required"
                binding.editTextEmail.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.editTextEmail.error = "Valid Email Required"
                binding.editTextEmail.requestFocus()
                return@setOnClickListener
            }

            binding.progressbar.visibility = View.VISIBLE
            currentUser?.let { user ->
                user.updateEmail(email)
                    .addOnCompleteListener { task ->
                        binding.progressbar.visibility = View.GONE
                        if (task.isSuccessful) {
                            val action = UpdateEmailFragmentDirections.actionEmailUpdated()
                            Navigation.findNavController(view).navigate(action)
                        } else {
                            context?.toast(task.exception?.message!!)
                        }
                    }

            }
        }

    }

}