package com.jotagalilea.posts.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class PostResponse(
	@SerializedName("userId") var userId: Int,
	@SerializedName("id") var id: Int,
	@SerializedName("title") var title: String,
	@SerializedName("body") var body: String
)