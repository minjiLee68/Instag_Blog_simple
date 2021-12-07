package com.sophia.instag_blog_simple

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sophia.instag_blog_simple.adapter.CommentsAdapter
import com.sophia.instag_blog_simple.adapter.PostAdapter
import com.sophia.instag_blog_simple.databinding.ActivityCommentsBinding
import com.sophia.instag_blog_simple.model.Comments
import com.sophia.instag_blog_simple.model.User
import com.sophia.instag_blog_simple.viewmodel.PostViewModel
import com.sophia.instag_blog_simple.viewmodel.PostViewModelFactory

class CommentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommentsBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserId: String
    private lateinit var postId: String
    private lateinit var commentList: MutableList<Comments>
    private lateinit var commentAdapter: CommentsAdapter

    private val viewmodel by viewModels<PostViewModel> {
        PostViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        postId = intent.getStringExtra("postId")!!
        commentList = mutableListOf()

        init()
        initRecyclerView()
        sendComment()
    }

    private fun init() {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser!!.uid
    }

    private fun initRecyclerView() {
        binding.recyclerView.let {
            commentAdapter = CommentsAdapter(commentList)
            it.setHasFixedSize(true)
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = commentAdapter
            viewmodel.getCommentId(commentList,postId).observe(this, { live ->
                commentAdapter.submitList(live)

                //notifyItemRangeChanged(positionStart: Int, itemCount: Int)
                //positionStart: 변경된 첫 번째 아이템의 위치
                //itemCount: 변경된 아이템의 개수
                commentAdapter.notifyItemRangeInserted(live.size,live.size)
                binding.recyclerView.smoothScrollToPosition(live.size -1)
            })
        }
    }

    private fun sendComment() {
        binding.send.setOnClickListener {
            viewmodel.sendComment(binding.commentTv.text.toString(), postId)
            binding.commentTv.text = null
        }
    }
}