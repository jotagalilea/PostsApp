package com.jotagalilea.posts.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class CommentResponse(
	@SerializedName("id") var id: Int,
	@SerializedName("postId") var postId: Int,
	@SerializedName("name") var name: String,
	@SerializedName("email") var email: String,
	@SerializedName("body") var body: String
)