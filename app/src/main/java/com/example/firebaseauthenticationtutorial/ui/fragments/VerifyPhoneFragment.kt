package com.example.firebaseauthenticationtutorial.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.firebaseauthenticationtutorial.R
import com.example.firebaseauthenticationtutorial.databinding.FragmentVerifyPhoneBinding

class VerifyPhoneFragment : Fragment() {
    private lateinit var binding: FragmentVerifyPhoneBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_verify_phone, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutPhone.visibility = View.VISIBLE
        binding.layoutVerification.visibility = View.INVISIBLE

        binding.buttonSendVerification.setOnClickListener {
            binding.layoutPhone.visibility = View.GONE
            binding.layoutVerification.visibility = View.VISIBLE
        }
    }
}