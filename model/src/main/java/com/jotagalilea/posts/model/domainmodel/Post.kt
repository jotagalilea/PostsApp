package com.jotagalilea.posts.model.domainmodel

import java.io.Serializable

/**
 * Clase modelo de un post.
 */
data class Post(
	var userId: Int,
	var id: Int,
	var title: String,
	var body: String
): Serializable