package com.sophia.instag_blog_simple

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.sophia.instag_blog_simple.databinding.ActivitySetUpBinding
import com.sophia.instag_blog_simple.interfaced.CallAnotherActivityNavigator
import com.sophia.instag_blog_simple.viewmodel.PostViewModel
import com.sophia.instag_blog_simple.viewmodel.PostViewModelFactory
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class SetUpActivity : AppCompatActivity(), CallAnotherActivityNavigator {

    private lateinit var binding: ActivitySetUpBinding
    private lateinit var mImageUri: Uri
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var downloadUri: Uri
    private lateinit var Uid: String
    private var isPhotoSelected: Boolean = false

    private val viewmodel by viewModels<PostViewModel> {
        PostViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        circleImageClick()
        requestPermissions()
        saveBtnClick()
        getProfileInFor()
    }

    private fun init() {
        storageReference = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        Uid = auth.currentUser?.uid.toString()
    }

    private fun circleImageClick() {
        binding.profile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            intent.putExtra("crop", true) //?????? ????????? ??? ??? ??????!
            fileterActivityLauncher.launch(intent)
        }
    }

    private fun getProfileInFor() {
        firestore.collection("Users").document(Uid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result!!.exists()) {
                        val name = task.result!!.getString("name")
                        val imageUrl = task.result!!.getString("image")
                        binding.nickName.setText(name)
                        mImageUri = Uri.parse(imageUrl)
                        Glide.with(this).load(imageUrl).into(binding.profile)
                    }
                }
            }
    }

    private fun saveBtnClick() {
        binding.saveBtn.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val name = binding.nickName.text.toString()
            val imageRef = storageReference.child("Profile_pics").child("$Uid.jpg")
            if (isPhotoSelected) {
                if (name.isNotEmpty()) {
                    val imageRef = storageReference.child("Profile").child("$name.jpg")
                    imageRef.putFile(mImageUri)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                saveToFireStore(task, name, imageRef)
                            } else {
                                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                } else {
                    Toast.makeText(this, "????????? ???????????? ????????? ???????????????.", Toast.LENGTH_SHORT).show()
                }
            } else {
                saveToFireStore(null, name, imageRef)
            }
        }
    }

    private fun saveToFireStore(
        task: Task<UploadTask.TaskSnapshot>?,
        name: String,
        imageRef: StorageReference
    ) {
        if (task != null) {
            imageRef.downloadUrl.addOnSuccessListener {
                downloadUri = it
            }
        } else {
            downloadUri = mImageUri
        }

        viewmodel.setUser(name, downloadUri.toString(), this)
    }

    private fun cropImage(uri: Uri) {
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this)
    }

    private val fileterActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                RESULT_OK -> {
                    it.data?.data?.let { uri ->
                        cropImage(uri)
                    }
                    mImageUri = it.data?.data!!
                    binding.profile.setImageURI(mImageUri)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(it.data)
                    if (it.resultCode == Activity.RESULT_OK) {
                        result.uri?.let {
                            binding.profile.setImageBitmap(result.bitmap)
                            binding.profile.setImageURI(result.uri)
                            mImageUri = result.uri
                        }
                    } else if (it.resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Toast.makeText(this, result.error.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    private fun requestPermissions() {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            Log.d("????????????", "$it")
        }.launch(PERMISSIONS_REQUESTED)
    }

    companion object {
        private const val PERMISSION_READ_EXTEDNAL_STORAGE =
            Manifest.permission.READ_EXTERNAL_STORAGE
        private const val PERMISSION_WRITE_EXTEDNAL_STORAGE =
            Manifest.permission.WRITE_EXTERNAL_STORAGE

        private val PERMISSIONS_REQUESTED: Array<String> = arrayOf(
            PERMISSION_READ_EXTEDNAL_STORAGE,
            PERMISSION_WRITE_EXTEDNAL_STORAGE
        )
    }

    override fun callActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}