package com.jotagalilea.posts.datasources.remote.mapper

import com.jotagalilea.posts.datasources.remote.model.PostResponse
import com.jotagalilea.posts.model.domainmodel.Post


class PostResponseMapper: RemoteMapper<PostResponse, Post> {

	override fun mapFromRemote(type: PostResponse): Post {
		return Post(
			id = type.id,
			userId = type.userId,
			title = type.title,
			body = type.body
		)
	}

	override fun mapFromRemoteList(typeList: List<PostResponse>): List<Post> {
		return typeList.map { mapFromRemote(it) }
	}


}