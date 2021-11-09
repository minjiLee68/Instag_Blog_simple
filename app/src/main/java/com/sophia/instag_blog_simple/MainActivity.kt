package com.sophia.instag_blog_simple

import android.Manifest
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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.sophia.instag_blog_simple.databinding.ActivityMainBinding
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mImageUri: Uri
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var downloadUri: Uri
    private lateinit var Uid: String
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        circleImageClick()
        requestPermissions()
    }

    private fun init() {
        storageReference = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        Uid = auth.currentUser!!.uid
    }

    private fun circleImageClick() {
        binding.profile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            intent.putExtra("crop", true) //기존 코드에 이 줄 추가!
            fileterActivityLauncher.launch(intent)
        }
    }

    private fun saveBtnClick() {
        binding.saveBtn.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val name = binding.nickName.text.toString()

            if (name.isNotEmpty()) {
                val imageRef = storageReference.child("Profile").child("$name.jpg")
                imageRef.putFile(mImageUri)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            saveToFireStore(task, name, imageRef)
                        } else {
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "사진을 선택하고 이름을 적어주세요.", Toast.LENGTH_SHORT).show()
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun saveToFireStore(
        task: Task<UploadTask.TaskSnapshot>,
        name: String,
        imageRef: StorageReference
    ) {
        imageRef.downloadUrl.addOnSuccessListener {
            downloadUri = it
            val map: HashMap<String, Any> = HashMap()
            map["name"] = name
            map["image"] = downloadUri.toString()

            firestore.collection("Users").document().set(map)
                .addOnCompleteListener {
                if (task.isSuccessful) {
                    Toast.makeText(this,"프로필 셋팅 완료",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cropImage(uri: Uri) {
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this)
    }

    private val fileterActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK && it.data?.data != null) {
                cropImage(it.data?.data!!)
                mImageUri = it.data?.data!!
                binding.profile.setImageURI(mImageUri)
            }
            if (it.resultCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result: CropImage.ActivityResult = CropImage.getActivityResult(it.data)
                if (it.resultCode == RESULT_OK) {
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

    private fun requestPermissions() {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            Log.d("권한요청", "$it")
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
}