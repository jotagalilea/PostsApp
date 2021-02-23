package com.jotagalilea.posts.model

import com.google.gson.annotations.SerializedName
import com.jotagalilea.posts.db.PostDBObject
import java.io.Serializable

/**
 * Clase modelo de un post.
 */
data class Post(
	@SerializedName("userId") var userId: Int,
	@SerializedName("id") var id: Int,
	@SerializedName("title") var title: String,
	@SerializedName("body") var body: String
): Serializable {

	/**
	 * Conversor del objeto post del dominio en objeto de BD.
	 * @return Post para la BD.
	 */
	fun asDBObject(): PostDBObject{
		return PostDBObject(
			userId = userId,
			id = id,
			title = title,
			body = body
		)
	}

}

fun MutableList<Post>.asDBObjects(): List<PostDBObject>{
	return map{
		PostDBObject(
			id = it.id,
			userId = it.userId,
			title = it.title,
			body = it.body
		)
	}
}