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
	@Query("SELECT * FROM posts ORDER BY id")
	suspend fun getAllPosts(): List<PostDBObject>

	@Query("SELECT * FROM posts WHERE userId=:userID")
	suspend fun getPostsFromUser(userID: Int): List<PostDBObject>

	@Query("SELECT * FROM users WHERE id=:userID")
	suspend fun getUserWithID(userID: Int): UserDBObject?

	@Query("SELECT * FROM comments WHERE postId=:postId")
	suspend fun getCommentsFromPost(postId: Int): List<CommentDBObject>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertPost(item: PostDBObject)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertUser(item: UserDBObject)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAllPosts(items: List<PostDBObject>)

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
