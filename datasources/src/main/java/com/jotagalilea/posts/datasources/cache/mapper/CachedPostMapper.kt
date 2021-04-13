package com.jotagalilea.posts.datasources.cache.mapper

import com.jotagalilea.posts.datasources.cache.model.PostDBObject
import com.jotagalilea.posts.model.domainmodel.Post


open class CachedPostMapper: CacheMapper<PostDBObject, Post> {

	override fun mapFromCached(type: PostDBObject): Post {
		return Post(
			id = type.id,
			userId = type.userId,
			title = type.title,
			body = type.body
		)
	}

	override fun mapFromCachedList(typeList: List<PostDBObject>): List<Post> {
		return typeList.map { mapFromCached(it) }
	}

	override fun mapToCached(type: Post): PostDBObject {
		return PostDBObject(
			id = type.id,
			userId = type.userId,
			title = type.title,
			body = type.body
		)
	}


	override fun mapToCachedList(typeList: List<Post>): List<PostDBObject> {
		return typeList.map { mapToCached(it) }
	}
}