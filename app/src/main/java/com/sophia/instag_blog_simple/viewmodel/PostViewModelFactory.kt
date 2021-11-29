package com.sophia.instag_blog_simple.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sophia.instag_blog_simple.repository.PostRepository
import java.lang.IllegalArgumentException

class PostViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val repository = PostRepository(context)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            return PostViewModel(repository) as T
        }
        throw IllegalArgumentException("")
    }
}