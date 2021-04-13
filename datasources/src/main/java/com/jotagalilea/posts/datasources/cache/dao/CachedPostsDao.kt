package com.jotagalilea.posts.datasources.cache.dao

import androidx.room.*
import com.jotagalilea.posts.datasources.cache.model.CommentDBObject
import com.jotagalilea.posts.datasources.cache.model.PostDBObject
import com.jotagalilea.posts.datasources.cache.model.UserDBObject


/**
 * Interfaz para el DAO de la base de datos.
 */
@Dao
interface CachedPostsDao {
	/**
	 * Consulta para obtener todos los posts.
	 */
	@Query("SELECT * FROM posts ORDER BY id")
	fun getAllPosts(): List<PostDBObject>

	/**
	 * Consulta para obtener los posts hechos por un usuario.
	 * @param userID ID del usuario.
	 */
	@Query("SELECT * FROM posts WHERE userId=:userID")
	fun getPostsFromUser(userID: Int): List<PostDBObject>

	/**
	 * Consulta para obtener los datos de un usuario.
	 * @param userID ID del usuario.
	 */
	@Query("SELECT * FROM users WHERE id=:userID")
	fun getUserWithID(userID: Int): UserDBObject

	/**
	 * Consulta para obtener los comentarios hechos en un post.
	 * @param postId ID del post.
	 */
	@Query("SELECT * FROM comments WHERE postId=:postId")
	fun getCommentsFromPost(postId: Int): List<CommentDBObject>

	/**
	 * Inserción de un post en BD.
	 * @param item Post a insertar.
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertPost(item: PostDBObject)

	/**
	 * Inserción de un usuario en BD.
	 * @param item Usuario a insertar.
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertUser(item: UserDBObject)

	/**
	 * Inserción de una lista de posts en BD.
	 * @param item Lista de posts a insertar.
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAllPosts(items: List<PostDBObject>)

	/**
	 * Inserción de una lista de usuarios en BD.
	 * @param item Lista de usuarios a insertar.
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAllUsers(items: List<UserDBObject>)

	/**
	 * Inserción de una lista de comentarios en BD.
	 * @param item Lista de comentarios a insertar.
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertComments(items: List<CommentDBObject>)


	// Aunque en esta app es probable que algunos delete no tengan sentido, lo dejo hecho.

	@Query("DELETE from Posts")
	fun clearPosts()

	@Query("DELETE from Users")
	fun clearUsers()

	@Query("DELETE from Comments")
	fun clearComments()
}