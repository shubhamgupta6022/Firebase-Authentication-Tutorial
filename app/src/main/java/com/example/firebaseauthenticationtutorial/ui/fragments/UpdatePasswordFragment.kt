package com.example.firebaseauthenticationtutorial.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.example.firebaseauthenticationtutorial.R
import com.example.firebaseauthenticationtutorial.databinding.FragmentUpdatePasswordBinding
import com.example.firebaseauthenticationtutorial.toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class UpdatePasswordFragment : Fragment() {
    private lateinit var binding: FragmentUpdatePasswordBinding
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_update_password, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutPassword.visibility = View.VISIBLE
        binding.layoutUpdatePassword.visibility = View.GONE

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
                                binding.layoutUpdatePassword.visibility = View.VISIBLE
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

        binding.buttonUpdate.setOnClickListener {

            val password = binding.editTextNewPassword.text.toString().trim()

            if (password.isEmpty() || password.length < 6) {
                binding.editTextNewPassword.error = "Atleast 6 char password required"
                binding.editTextNewPassword.requestFocus()
                return@setOnClickListener
            }

            if (password != binding.editTextNewPasswordConfirm.text.toString().trim()) {
                binding.editTextNewPasswordConfirm.error = "Password did not match"
                binding.editTextNewPasswordConfirm.requestFocus()
                return@setOnClickListener
            }

            currentUser?.let { user ->
                binding.progressbar.visibility = View.VISIBLE
                user.updatePassword(password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val action = UpdatePasswordFragmentDirections.actionPasswordUpdated()
                            Navigation.findNavController(it).navigate(action)
                            context?.toast("Password Updated")
                        } else {
                            context?.toast(task.exception?.message!!)
                        }
                    }
            }
        }


    }

}