package com.sophia.instag_blog_simple.adapter

import android.util.Log
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

    inner class CommentViewHolder(private val binding: CommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comments: Comments) {
            binding.userName.text = comments.user
            binding.commentTv.text = comments.comment
            Glide.with(itemView.context).load(comments.userProfile).into(binding.profile)
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