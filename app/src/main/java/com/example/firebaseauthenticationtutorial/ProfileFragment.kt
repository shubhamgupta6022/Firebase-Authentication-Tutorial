package com.example.firebaseauthenticationtutorial

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.firebaseauthenticationtutorial.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private val TAG = ProfileFragment::class.java.simpleName

    private lateinit var binding: FragmentProfileBinding
    private val REQUEST_IMAGE_CAPTURE = 100
    private lateinit var imageUri: Uri

    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val DEFAULT_IMAGE_URL = "https://picsum.photos/200"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser?.let { user ->
            Glide.with(this)
                .load(user.photoUrl)
                .into(binding.imageView)

            binding.editTextName.setText(user.displayName)
            binding.textEmail.text = user.email
            binding.textPhone.text =
                if (user.phoneNumber.isNullOrEmpty()) "Add Number" else user.phoneNumber

            if (user.isEmailVerified) {
                binding.textNotVerified.visibility = View.INVISIBLE
            } else {
                binding.textNotVerified.visibility = View.VISIBLE
            }
        }

        binding.imageView.setOnClickListener {
            takePictureIntent()
        }

        binding.buttonSave.setOnClickListener {
            val photo = when {
                ::imageUri.isInitialized -> imageUri
                currentUser?.photoUrl == null -> Uri.parse(DEFAULT_IMAGE_URL)
                else -> currentUser.photoUrl
            }
            val name = binding.editTextName.text.toString().trim()

            if (name.isEmpty()) {
                binding.editTextName.error = "name required"
                binding.editTextName.requestFocus()
                return@setOnClickListener
            }

            val updates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(photo)
                .build()

            binding.progressbar.visibility = View.VISIBLE

            currentUser?.updateProfile(updates)
                ?.addOnCompleteListener { task ->
                    binding.progressbar.visibility = View.INVISIBLE
                    if (task.isSuccessful) {
                        context?.toast("Profile Updated")
                    } else {
                        context?.toast(task.exception?.message!!)
                    }
                }
        }

        binding.textNotVerified.setOnClickListener {
            currentUser?.sendEmailVerification()?.addOnCompleteListener {
                if (it.isSuccessful) {
                    context?.toast("Verification email sent")
                } else {
                    context?.toast(it.exception?.message!!)
                }
            }
        }
    }

    private fun takePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(activity?.packageManager!!).also {
                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            uploadImageAndSaveUri(imageBitmap)
        }
    }

    private fun uploadImageAndSaveUri(bitmap: Bitmap) {
        val btos = ByteArrayOutputStream()
        val storageRef =
            FirebaseStorage.getInstance()
                .reference
                .child("pics/${FirebaseAuth.getInstance().currentUser?.uid}")

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, btos)
        val image = btos.toByteArray()
        val upload = storageRef.putBytes(image)

        binding.progressbarPic.visibility = View.VISIBLE
        upload.addOnCompleteListener { uploadTask ->
            binding.progressbarPic.visibility = View.INVISIBLE
            if (uploadTask.isSuccessful) {
                storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                    urlTask.result?.let {
                        imageUri = it
                        activity?.toast(imageUri.toString())
                        binding.imageView.setImageBitmap(bitmap)
                    }
                }
            } else {
                uploadTask.exception?.let {
                    activity?.toast(it.message!!)
                    Log.v(TAG, it.message!!)
                }
            }
        }

    }
}