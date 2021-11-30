package com.sophia.instag_blog_simple.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sophia.instag_blog_simple.databinding.CommentItemBinding
import com.sophia.instag_blog_simple.model.Comments
import com.sophia.instag_blog_simple.model.Post

class CommentsAdapter(private val commentsList: List<Comments>) :
    ListAdapter<Comments, CommentsAdapter.CommentViewHolder>(

        object : DiffUtil.ItemCallback<Comments>() {
            override fun areItemsTheSame(oldItem: Comments, newItem: Comments): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Comments, newItem: Comments): Boolean =
                oldItem.time == newItem.time && oldItem.user == newItem.user

        }

    ) {

    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    inner class CommentViewHolder(private val binding: CommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comments: Comments) {
            binding.userName.text = comments.user
            binding.commentTv.text = comments.comment

            val userId = Post().postId

            firestore.collection("Users").document(userId).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.result!!.exists()) {
                            val name = task.result!!.getString("name").toString()
                            val imageUrl = task.result!!.getString("image").toString()

                            setUserName(name)
                            setCircleImageView(imageUrl)
                        }
                    }
                }
        }

        fun setUserName(userName: String) {
            binding.userName.text = userName
        }

        fun setCircleImageView(profile: String) {
            Glide.with(itemView.context).load(profile).into(binding.profile)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder =
        CommentViewHolder(
            CommentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comments = commentsList[position]
        holder.bind(comments)
    }
}