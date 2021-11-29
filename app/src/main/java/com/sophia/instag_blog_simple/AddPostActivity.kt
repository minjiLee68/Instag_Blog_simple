package com.sophia.instag_blog_simple

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sophia.instag_blog_simple.databinding.ActivityAddPostBinding
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class AddPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPostBinding
    private lateinit var mImageUri: Uri
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var Uid: String

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
        binding.ivAddBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            intent.putExtra("crop", true) //기존 코드에 이 줄 추가!
            fileterActivityLauncher.launch(intent)
            binding.ivAddBtn.visibility = View.GONE
        }
        binding.saveBtn.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val caption = binding.captionText.text.toString()
            if (caption.isNotEmpty()) {
                val postRef = storageReference.child("post_images")
                    .child("${FieldValue.serverTimestamp()}.jpg")
                postRef.putFile(mImageUri).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        binding.progressBar.visibility = View.INVISIBLE
                        postRef.downloadUrl.addOnSuccessListener { uri ->
                            val postMap = HashMap<String, Any>()
                            postMap["image"] = uri.toString()
                            postMap["user"] = Uid
                            postMap["caption"] = caption
                            postMap["time"] = FieldValue.serverTimestamp()

                            firestore.collection("Posts").add(postMap)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        binding.progressBar.visibility = View.INVISIBLE
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    }
                                }
                        }
                    } else {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                binding.progressBar.visibility = View.INVISIBLE
                Toast.makeText(this, "이미지를 추가하고 캡션을 작성하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cropImage(uri: Uri) {
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1,1)
            .setMinCropResultSize(512, 512)
            .start(this)
    }

    private val fileterActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK && it.data?.data != null) {
                cropImage(it.data?.data!!)
                mImageUri = it.data?.data!!
                binding.ivAddImage.setImageURI(mImageUri)
            }
            if (it.resultCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result: CropImage.ActivityResult = CropImage.getActivityResult(it.data)
                if (it.resultCode == RESULT_OK) {
                    result.uri?.let {
                        binding.ivAddImage.setImageBitmap(result.bitmap)
                        binding.ivAddImage.setImageURI(result.uri)
                        mImageUri = result.uri
                    }
                } else if (it.resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(this, result.error.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
}