package com.sophia.instag_blog_simple.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.sophia.instag_blog_simple.repository.PostRepository

class PostViewModel(private val repository: PostRepository) : ViewModel() {

    fun addPost(
        captionText: String,
        mImageUri: Uri,
        context: Context,
    ) {
        repository.addPost(captionText, mImageUri, context)
    }

}