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
	 */
	fun asDBObject(): PostDBObject{
		return PostDBObject(
			id = id,
			userId = userId,
			title = title,
			body = body
		)
	}

}

/**
 * Extensión para convertir una lista de posts del dominio a objetos de BD también en formato lista.
 */
fun List<Post>.asDBObjects(): List<PostDBObject>{
	return map{
		it.asDBObject()
	}
}