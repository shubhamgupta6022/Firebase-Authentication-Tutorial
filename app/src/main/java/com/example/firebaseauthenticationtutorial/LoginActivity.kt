package com.example.firebaseauthenticationtutorial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val registerButton=findViewById<TextView>(R.id.text_view_register)
        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }
}