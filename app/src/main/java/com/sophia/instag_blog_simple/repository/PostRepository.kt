package com.sophia.instag_blog_simple.repository

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sophia.instag_blog_simple.interfaced.CallAnotherActivityNavigator
import com.sophia.instag_blog_simple.model.Post
import com.sophia.instag_blog_simple.model.User
import java.text.SimpleDateFormat
import java.util.*

class PostRepository(context: Context) {

    private var storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var Uid: String = auth.currentUser?.uid.toString()
    private var progressBar: ProgressBar = ProgressBar(context)

    private val now = System.currentTimeMillis()
    private val time = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN).format(now)

    private val mPostData = MutableLiveData<List<Post>>()

    fun getPostData(): LiveData<List<Post>> = mPostData

    fun setUser(name: String, image: String) {
        val user = User(name, image)
        firestore.collection("Users").document(Uid).set(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.visibility = View.VISIBLE
                }
            }
    }

    fun addPost(
        captionText: String,
        mImageUri: Uri,
        context: Context,
        navigator: CallAnotherActivityNavigator
    ) {
        if (captionText.isNotEmpty()) {
            val postRef = storageReference.child("post_images")
                .child("${FieldValue.serverTimestamp()}.jpg")
            postRef.putFile(mImageUri).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressBar.visibility = View.INVISIBLE
                    postRef.downloadUrl.addOnSuccessListener { uri ->
                        val post = Post(uri.toString(), Uid, captionText, time)
                        firestore.collection("Posts").document().set(post)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    navigator.callActivity()
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

    fun putDataInList(postList: MutableList<Post>): LiveData<List<Post>> {
        firestore.collection("Posts").addSnapshotListener { value, _ ->
            if (value != null) {
                for (dc: DocumentChange in value.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val postId = dc.document.id
                        val post = dc.document.toObject(Post::class.java).withId(postId)
                        post.user = dc.document.getString("user")!!
                        post.image = dc.document.getString("image")!!
                        post.caption = dc.document.getString("caption")!!
                        post.time = time

                        postList.add(post)
                        mPostData.value = postList
                    }
                }
            }
        }
        return mPostData
    }
}