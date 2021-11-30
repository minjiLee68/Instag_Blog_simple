package com.sophia.instag_blog_simple

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sophia.instag_blog_simple.databinding.ActivityCommentsBinding
import com.sophia.instag_blog_simple.model.Comments

class CommentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommentsBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserId: String
    private lateinit var postId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postId = intent.getStringExtra("postId")!!
        init()
        sendComment()
    }

    private fun init() {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser!!.uid
    }

    private fun initRecyclerView() {
        binding.recyclerView.let {
            it.setHasFixedSize(true)
            it.layoutManager = LinearLayoutManager(this)
        }
    }

    private fun sendComment() {
        binding.send.setOnClickListener {
            val comment = binding.commentTv.text.toString()
            if (comment.isNotEmpty()) {
                val time = FieldValue.serverTimestamp()
                val comments = Comments(comment,time,currentUserId)
                firestore.collection("Posts/$postId/Comments").add(comments)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                        } else {
                            Toast.makeText(this,task.exception?.message,Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}