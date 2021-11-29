package com.sophia.instag_blog_simple.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

        fun bind(post: Post) {
            binding.userNameText.text = post.user
            binding.dateTv.text = post.time.toString()
            binding.captionTv.text = post.caption
            Glide.with(itemView.context).load(post.image).into(binding.postImage)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder =
        PostViewHolder(
            PostItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val postList = mList[position]
        holder.bind(postList)
    }
}