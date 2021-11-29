package com.sophia.instag_blog_simple

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.sophia.instag_blog_simple.adapter.PostAdapter
import com.sophia.instag_blog_simple.databinding.ActivityMainBinding
import com.sophia.instag_blog_simple.model.Post
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var postList: MutableList<Post>
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setButtonLogout()
        addPhotoFeed()
    }

    private fun initRecyclerView() {
        postAdapter = PostAdapter(postList)
        binding.recyclerView.let {
            it.setHasFixedSize(true)
            it.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        }
    }

    private fun putDataInList() {
        if (auth.currentUser != null) {

        }
    }

    private fun addPhotoFeed() {
        binding.ivCamera.setOnClickListener {
            startActivity(Intent(this, AddPostActivity::class.java))
        }
    }

    private fun setButtonLogout() {
        binding.ivLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            auth.signOut()
        }
    }
}