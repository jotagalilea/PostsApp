package com.jotagalilea.posts.db

import android.content.Context
import androidx.room.*


/**
 * Base de datos con Room.
 */
@Database(entities = [
	PostDBObject::class,
	CommentDBObject::class,
	UserDBObject::class
], version = 1)
abstract class PostsDB: RoomDatabase() {
	abstract fun getDao(): PostsDao
}

/**
 * Interfaz para el DAO de la base de datos.
 */
@Dao
interface PostsDao {
	/**
	 * Consulta para obtener todos los posts.
	 */
	@Query("SELECT * FROM posts ORDER BY id")
	suspend fun getAllPosts(): List<PostDBObject>

	/**
	 * Consulta para obtener los posts hechos por un usuario.
	 * @param userID ID del usuario.
	 */
	@Query("SELECT * FROM posts WHERE userId=:userID")
	suspend fun getPostsFromUser(userID: Int): List<PostDBObject>

	/**
	 * Consulta para obtener los datos de un usuario.
	 * @param userID ID del usuario.
	 */
	@Query("SELECT * FROM users WHERE id=:userID")
	suspend fun getUserWithID(userID: Int): UserDBObject

	/**
	 * Consulta para obtener los comentarios hechos en un post.
	 * @param postId ID del post.
	 */
	@Query("SELECT * FROM comments WHERE postId=:postId")
	suspend fun getCommentsFromPost(postId: Int): List<CommentDBObject>

	/**
	 * Inserción de un post en BD.
	 * @param item Post a insertar.
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertPost(item: PostDBObject)

	/**
	 * Inserción de un usuario en BD.
	 * @param item Usuario a insertar.
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertUser(item: UserDBObject)

	/**
	 * Inserción de una lista de posts en BD.
	 * @param item Lista de posts a insertar.
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAllPosts(items: List<PostDBObject>)

	/**
	 * Inserción de una lista de usuarios en BD.
	 * @param item Lista de usuarios a insertar.
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAllUsers(items: List<UserDBObject>)

	/**
	 * Inserción de una lista de comentarios en BD.
	 * @param item Lista de comentarios a insertar.
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertComments(items: List<CommentDBObject>)
}

/**
 * Instancia de la base de datos.
 */
private lateinit var INSTANCE: PostsDB


/**
 * Obtiene la instancia de la base de datos. Si no existe se crea. Implementación básica según la
 * guía de Room.
 * @param context Contexto de la aplicación.
 * @return Instancia de la BD.
 */
fun getDatabase(context: Context): PostsDB {
	synchronized(PostsDB::class.java) {
		if (!::INSTANCE.isInitialized) {
			INSTANCE = Room.databaseBuilder(context.applicationContext,
				PostsDB::class.java,
				"postsDB").build()
		}
	}
	return INSTANCE
}
