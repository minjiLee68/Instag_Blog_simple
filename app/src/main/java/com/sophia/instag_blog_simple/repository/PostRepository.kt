package com.sophia.instag_blog_simple.repository

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sophia.instag_blog_simple.model.Post
import java.util.*

class PostRepository(context: Context) {

    private var storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var Uid: String = auth.currentUser?.uid.toString()
    private var isPhotoSelected: Boolean = false
    private var progressBar: ProgressBar = ProgressBar(context)


    private fun addPost(
        captionText: String,
        mImageUri: Uri,
        context: Context,
        image: String,
        user: String,
        caption: String,
        time: Date
    ) {
        progressBar.visibility = View.VISIBLE
        if (captionText.isNotEmpty()) {
            val postRef = storageReference.child("post_images")
                .child("${FieldValue.serverTimestamp()}.jpg")
            postRef.putFile(mImageUri).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressBar.visibility = View.INVISIBLE
                    postRef.downloadUrl.addOnSuccessListener {
                        val post = Post(image, user, caption, time)
                        firestore.collection("Posts").add(post)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    progressBar.visibility = View.INVISIBLE
                                }
                            }
                    }
                } else {
                    Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            progressBar.visibility = View.INVISIBLE
            Toast.makeText(context, "이미지를 추가하고 캡션을 작성하세요.", Toast.LENGTH_SHORT).show()
        }
    }
}