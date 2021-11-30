package com.sophia.instag_blog_simple.model

import androidx.annotation.NonNull
import com.google.firebase.firestore.Exclude

data class PostLikes(
    @get:Exclude
    var postId: String = "",
) {
    fun withId(@NonNull id: String): PostLikes {
        this.postId = id
        return this
    }
}
