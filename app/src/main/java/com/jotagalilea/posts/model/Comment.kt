package com.jotagalilea.posts.model

import com.google.gson.annotations.SerializedName
import com.jotagalilea.posts.db.CommentDBObject

data class Comment(
    @SerializedName("id") var id: Int,
    @SerializedName("postId") var postId: Int,
    @SerializedName("name") var name: String,
    @SerializedName("email") var email: String,
    @SerializedName("body") var body: String
)

fun List<Comment>.asDBObject(): List<CommentDBObject>{
    return map {
        CommentDBObject(it.id, it.postId, it.name, it.email, it.body)
    }
}
