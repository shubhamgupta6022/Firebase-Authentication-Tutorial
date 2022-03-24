package com.example.firebaseauthenticationtutorial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.example.firebaseauthenticationtutorial.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mAuth = FirebaseAuth.getInstance()

        binding.buttonSignIn.setOnClickListener {
            val email = binding.textEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

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

            if (password.isEmpty() || password.length < 6) {
                binding.editTextPassword.error = "6 char password required"
                binding.editTextPassword.requestFocus()
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        binding.textViewRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private fun loginUser(email: String, password: String) {
        binding.progressbar.visibility = View.VISIBLE
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.progressbar .visibility = View.GONE
                if (task.isSuccessful) {
                    login()
                } else {
                    task.exception?.message?.let {
                        toast(it)
                    }
                }
            }
    }

    // If user is already logged in, redirect to home page
    override fun onStart() {
        super.onStart()
        mAuth.currentUser?.let {
            login()             // it will open the home page
        }
    }
}