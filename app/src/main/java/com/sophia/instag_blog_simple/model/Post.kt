package com.sophia.instag_blog_simple.model

import androidx.annotation.NonNull
import com.google.firebase.firestore.Exclude


data class Post(
    var image: String = "",
    var user: String = "",
    var caption: String = "",
    var time: String= "",
    @Exclude
    var postId: String = ""
) {
    fun withId(@NonNull id: String): Post{
        this.postId = id
        return this
    }
}
