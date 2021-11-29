package com.sophia.instag_blog_simple.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sophia.instag_blog_simple.interfaced.CallAnotherActivityNavigator
import com.sophia.instag_blog_simple.model.Post
import com.sophia.instag_blog_simple.model.User
import com.sophia.instag_blog_simple.repository.PostRepository

class PostViewModel(private val repository: PostRepository) : ViewModel() {

    fun addPost(
        captionText: String,
        mImageUri: Uri,
        context: Context,
        navigator: CallAnotherActivityNavigator
    ) {
        repository.addPost(captionText, mImageUri, context,navigator)
    }

    fun putDataInList(postList: MutableList<Post>): LiveData<List<Post>> {
        repository.putDataInList(postList)
        return repository.getPostData()
    }

    fun setUser(name: String, image: String) {
        repository.setUser(name, image)
    }

}