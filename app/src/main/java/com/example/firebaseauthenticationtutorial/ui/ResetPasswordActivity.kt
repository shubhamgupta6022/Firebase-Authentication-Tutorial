package com.example.firebaseauthenticationtutorial.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.example.firebaseauthenticationtutorial.R
import com.example.firebaseauthenticationtutorial.databinding.ActivityResetPasswordBinding
import com.example.firebaseauthenticationtutorial.toast
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonResetPassword.setOnClickListener {
            val email = binding.textEmail.text.toString().trim()

            if (email.isEmpty()) {
                binding.textEmail.error = "Email Required"
                binding.textEmail.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.textEmail.error = "Valid Email Required"
                binding.textEmail.requestFocus()
                return@setOnClickListener
            }

            binding.progressbar.visibility = View.VISIBLE

            FirebaseAuth.getInstance()
                .sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    binding.progressbar.visibility = View.GONE
                    if (task.isSuccessful) {
                        this.toast("Check your email")
                    } else {
                        this.toast(task.exception?.message!!)
                    }
                }
        }
    }
}