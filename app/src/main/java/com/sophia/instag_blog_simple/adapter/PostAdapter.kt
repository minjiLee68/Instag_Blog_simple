package com.sophia.instag_blog_simple.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sophia.instag_blog_simple.R
import com.sophia.instag_blog_simple.databinding.PostItemBinding
import com.sophia.instag_blog_simple.model.Post

class PostAdapter(private val mList: List<Post>) : ListAdapter<Post, PostAdapter.PostViewHolder>(
    object : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem.user == newItem.user && oldItem.image == newItem.image
    }

) {
    inner class PostViewHolder(private val binding: PostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        private var auth: FirebaseAuth = FirebaseAuth.getInstance()

        @SuppressLint("UseCompatLoadingForDrawables")
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
            //likebtn
            val postId = post.postId
            val currentUserId = auth.currentUser!!.uid
            binding.likedClick.setOnClickListener {
                firestore.collection("Posts/$postId/Likes").document(currentUserId).get()
                    .addOnCompleteListener { task ->
                        if (task.result != null) {
                            val likesMap: HashMap<String, Any> = HashMap()
                            likesMap["timestamp"] = FieldValue.serverTimestamp()
                            firestore.collection("Posts/$postId/Likes").document(currentUserId).set(likesMap)
                        } else {
                            Log.d("tag","error")
                        }
                    }
                firestore.collection("Posts/$postId/Likes").document(currentUserId)
                    .addSnapshotListener { value, _ ->
                        if (value != null) {
                            binding.likedClick.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_after_like))
                        } else {
                            binding.likedClick.setImageDrawable(itemView.context.getDrawable(R.drawable.before_liked))
                        }
                    }
//                firestore.collection("Posts/$postId/Likes").document(currentUserId).get()
//                    .addOnCompleteListener { task ->
//                        if (task.result!!.exists()) {
//                            val likesMap: HashMap<String, Any> = HashMap()
//                            likesMap["timestamp"] = FieldValue.serverTimestamp()
//                            firestore.collection("Posts/$postId/Likes").document(currentUserId).set(likesMap)
//                        } else {
//                            firestore.collection("Posts/$postId/Likes").document(currentUserId).delete()
//                            Log.d("tag",currentUserId)
//                        }
//                    }
//                firestore.collection("Posts/$postId/Likes").document(currentUserId).addSnapshotListener { value, error ->
//                        if (value!!.exists()) {
//                            binding.likedClick.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_after_like))
//                        } else {
//                            binding.likedClick.setImageDrawable(itemView.context.getDrawable(R.drawable.before_liked))
//                        }
//                }
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