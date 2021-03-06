package com.sophia.instag_blog_simple.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sophia.instag_blog_simple.interfaced.CallAnotherActivityNavigator
import com.sophia.instag_blog_simple.model.Comments
import com.sophia.instag_blog_simple.model.Post
import com.sophia.instag_blog_simple.model.User
import com.sophia.instag_blog_simple.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostViewModel(private val repository: PostRepository) : ViewModel() {

    fun addPost(
        captionText: String,
        mImageUri: Uri,
        context: Context,
        navigator: CallAnotherActivityNavigator
    ) {
        repository.addPost(captionText, mImageUri, context, navigator)
    }

    fun putDataInList(postList: MutableList<Post>): LiveData<List<Post>> {
        repository.putDataInList(postList)
        return repository.getPostData()
    }

    fun setUser(name: String, image: String, navigator: CallAnotherActivityNavigator) {
        repository.setUser(name, image, navigator)
    }

    fun sendComment(comments: String, postId: String) {
        repository.sendComment(comments, postId)
    }

    fun getCommentId(
        commentList: MutableList<Comments>,
        postId: String
    ): LiveData<List<Comments>> {
        repository.getCommentId(commentList, postId)
        return repository.getCommentData()
    }

}