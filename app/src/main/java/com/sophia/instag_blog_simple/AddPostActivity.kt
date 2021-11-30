package com.sophia.instag_blog_simple

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sophia.instag_blog_simple.databinding.ActivityAddPostBinding
import com.sophia.instag_blog_simple.interfaced.CallAnotherActivityNavigator
import com.sophia.instag_blog_simple.model.Post
import com.sophia.instag_blog_simple.viewmodel.PostViewModel
import com.sophia.instag_blog_simple.viewmodel.PostViewModelFactory
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.text.SimpleDateFormat
import java.util.*

class AddPostActivity : AppCompatActivity(), CallAnotherActivityNavigator {

    private lateinit var binding: ActivityAddPostBinding
    private lateinit var mImageUri: Uri
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var Uid: String

    private val viewmodel by viewModels<PostViewModel> {
        PostViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        addPost()
    }

    private fun init() {
        storageReference = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        Uid = auth.currentUser?.uid.toString()
    }

    private fun addPost() {
        binding.ivAddImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.putExtra("crop", true) //기존 코드에 이 줄 추가!
            fileterActivityLauncher.launch(intent)
            binding.ivAddBtn.visibility = View.GONE
        }
        binding.saveBtn.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            viewmodel.addPost(binding.captionText.text.toString(),mImageUri,applicationContext,this)
        }
    }

    private fun cropImage(uri: Uri) {
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this)
    }

    private val fileterActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(it.resultCode) {
                RESULT_OK -> {
                    it.data?.data?.let { uri->
                        cropImage(uri)
                    }
                    mImageUri = it.data?.data!!
                    binding.ivAddImage.setImageURI(mImageUri)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(it.data)
                    if (it.resultCode == Activity.RESULT_OK) {
                        result.uri?.let {
                            binding.ivAddImage.setImageBitmap(result.bitmap)
                            binding.ivAddImage.setImageURI(result.uri)
                            mImageUri = result.uri
                        }
                    } else if(it.resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Toast.makeText(this, result.error.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
//            if (it.resultCode == RESULT_OK && it.data?.data != null) {
//                cropImage(it.data?.data!!)
//                mImageUri = it.data?.data!!
//                binding.ivAddImage.setImageURI(mImageUri)
//            }
//            if (it.resultCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//                val result: CropImage.ActivityResult = CropImage.getActivityResult(it.data)
//                cropImage(it.data?.data!!)
//                if (it.resultCode == RESULT_OK) {
//                    result.uri?.let {
//                        binding.ivAddImage.setImageBitmap(result.bitmap)
//                        binding.ivAddImage.setImageURI(result.uri)
//                        mImageUri = result.uri
//                    }
//                } else if (it.resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                    Toast.makeText(this, result.error.message, Toast.LENGTH_SHORT).show()
//                }
//            }
        }

    override fun callActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}