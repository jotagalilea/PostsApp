package com.jotagalilea.posts.apiclient

import com.jotagalilea.posts.model.Comment
import com.jotagalilea.posts.model.Post
import com.jotagalilea.posts.model.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Endpoints de los servicios web utilizados en la aplicación.
 */
interface Endpoints {

	/**
	 * Variables utilizadas en las requests.
	 */
	companion object{
		const val BASE_URL = "http://jsonplaceholder.typicode.com/"
	}


	/**
	 * Petición de un conjunto de posts.
	 * @param offset Desplazamiento.
	 * @param limit Límite de elementos.
	 */
	@GET("posts/")
	fun getPostsList(): Call<List<Post>>

	/**
	 * Petición de los datos de un usuario dado su ID.
	 * @param userID ID del usuario.
	 */
	@GET("users/")
	fun getUserWithId(@Query("id") userID: Int): Call<List<User>>

	/**
	 * Petición de los comentarios de un post.
	 * @param postId ID del post al que pertenecen.
	 */
	@GET("comments/")
	fun getCommentsOfPost(@Query("postId") postId: Int): Call<List<Comment>>

}