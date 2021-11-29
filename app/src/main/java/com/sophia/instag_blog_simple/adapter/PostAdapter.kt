package com.sophia.instag_blog_simple.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.sophia.instag_blog_simple.databinding.PostItemBinding
import com.sophia.instag_blog_simple.model.Post
import com.sophia.instag_blog_simple.model.User
import com.sophia.instag_blog_simple.viewmodel.PostViewModel

class PostAdapter(private val mList: List<Post>): ListAdapter<Post, PostAdapter.PostViewHolder>(
        object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = true

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = true
        }

    ) {
    inner class PostViewHolder(private val binding: PostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

        fun setPost(post: Post) {
            val uid = post.user
            binding.dateTv.text = post.time
            binding.captionTv.text = post.caption
            Glide.with(itemView.context).load(post.image).into(binding.postImage)

            firestore.collection("Users").document(uid).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.result!!.exists()) {
                            val name = task.result!!.getString("name")
                            val imageUrl = task.result!!.getString("image")
                            setPostUserProfile(imageUrl.toString())
                            setPostUserName(name.toString())
                        }
                    }
                }
        }

        private fun setPostUserProfile(profile: String) {
            Glide.with(itemView.context).load(profile).into(binding.profile)
        }
        private fun setPostUserName(userName: String) {
            binding.userNameText.text = userName
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
        holder.setPost(postList)
    }
}