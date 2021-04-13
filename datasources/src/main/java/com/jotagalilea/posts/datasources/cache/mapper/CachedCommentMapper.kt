package com.jotagalilea.posts.datasources.cache.mapper

import com.jotagalilea.posts.datasources.cache.model.CommentDBObject
import com.jotagalilea.posts.model.domainmodel.Comment


open class CachedCommentMapper: CacheMapper<CommentDBObject, Comment> {
	override fun mapFromCached(type: CommentDBObject): Comment {
		return Comment(
			id = type.id,
			postId = type.postId,
			name = type.name,
			email = type.email,
			body = type.body
		)
	}


	override fun mapFromCachedList(typeList: List<CommentDBObject>): List<Comment> {
		return typeList.map { mapFromCached(it) }
	}


	override fun mapToCached(type: Comment): CommentDBObject {
		return CommentDBObject(
			id = type.id,
			postId = type.postId,
			name = type.name,
			email = type.email,
			body = type.body
		)
	}


	override fun mapToCachedList(typeList: List<Comment>): List<CommentDBObject> {
		return typeList.map { mapToCached(it) }
	}

}