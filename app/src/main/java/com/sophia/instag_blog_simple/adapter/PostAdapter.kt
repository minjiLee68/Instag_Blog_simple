package com.sophia.instag_blog_simple.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sophia.instag_blog_simple.databinding.PostItemBinding
import com.sophia.instag_blog_simple.model.Post

class PostAdapter(private val mList: List<Post>) : ListAdapter<Post, PostAdapter.PostViewHolder>(

    object : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = true

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = true
    }

) {
    inner class PostViewHolder(private val binding: PostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder =
        PostViewHolder(PostItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {

    }
}