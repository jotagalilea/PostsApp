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

	//TODO: Decir que los voy pidiendo uno a uno para no pedirlos todos de golpe (lo mismo con los comentarios).
	@GET("users/")
	fun getUserWithId(@Query("id") userID: Int): Call<List<User>>

	@GET("comments/")
	fun getCommentsOfPost(@Query("postId") postId: Int): Call<List<Comment>>

}