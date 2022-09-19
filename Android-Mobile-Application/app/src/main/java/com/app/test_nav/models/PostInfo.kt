package com.app.test_nav.models

import com.google.firebase.firestore.auth.User
import java.sql.Timestamp

data class PostInfo(
    var postId:String="",
    var postTitle:String="",
    var postDate:Number = 0,
    var postDescription: String,
    var postLocation: String,
    var imageUrl: String="",
    var user: UserModel? = null) {
}