package com.sophia.instag_blog_simple.model

import com.google.firebase.firestore.FieldValue

data class Comments(
    var comment: String,
    var time: FieldValue,
    var user: String
)
