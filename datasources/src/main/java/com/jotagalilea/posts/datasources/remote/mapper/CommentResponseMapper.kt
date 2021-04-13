package com.jotagalilea.posts.datasources.remote.mapper

import com.jotagalilea.posts.datasources.remote.model.CommentResponse
import com.jotagalilea.posts.model.domainmodel.Comment


class CommentResponseMapper: RemoteMapper<CommentResponse, Comment> {

	override fun mapFromRemote(type: CommentResponse): Comment {
		return Comment(
			id = type.id,
			postId = type.postId,
			name = type.name,
			email = type.email,
			body = type.body
		)
	}

	override fun mapFromRemoteList(typeList: List<CommentResponse>): List<Comment> {
		return typeList.map { mapFromRemote(it) }
	}
}