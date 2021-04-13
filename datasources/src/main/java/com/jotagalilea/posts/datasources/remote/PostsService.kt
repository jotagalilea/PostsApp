package com.jotagalilea.posts.datasources.remote

import com.jotagalilea.posts.datasources.remote.model.CommentResponse
import com.jotagalilea.posts.datasources.remote.model.PostResponse
import com.jotagalilea.posts.datasources.remote.model.UserResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Endpoints de los servicios web utilizados en la aplicación.
 */
interface PostsService {

	/**
	 * Variables utilizadas en las requests.
	 */
	companion object{
		const val BASE_URL = "http://jsonplaceholder.typicode.com/"
	}


	class PostsResponse {
		lateinit var items: List<PostResponse>
	}
	class UsersResponse {
		lateinit var items: List<UserResponse>
	}
	class CommentsResponse {
		lateinit var items: List<CommentResponse>
	}



	/**
	 * Petición de un conjunto de posts.
	 * @param offset Desplazamiento.
	 * @param limit Límite de elementos.
	 */
	@GET("posts/")
	fun getPostsList(): Single<PostsResponse>

	/**
	 * Petición de los datos de un usuario dado su ID.
	 * @param userID ID del usuario.
	 */
	@GET("users/")
	fun getUserWithId(@Query("id") userID: Int): Single<UserResponse>

	/**
	 * Petición de los comentarios de un post.
	 * @param postId ID del post al que pertenecen.
	 */
	@GET("comments/")
	fun getCommentsOfPost(@Query("postId") postId: Int): Single<CommentsResponse>

}