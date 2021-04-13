package com.jotagalilea.posts.model.domainmodel

import java.io.Serializable

/**
 * Clase que contiene los datos de un comentario.
 */
data class Comment(
	var id: Int,
	var postId: Int,
	var name: String,
	var email: String,
	var body: String
) : Serializable