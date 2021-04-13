package com.jotagalilea.posts.datasources.cache.db

import android.content.Context
import androidx.room.*
import com.jotagalilea.posts.datasources.cache.dao.CachedPostsDao
import com.jotagalilea.posts.datasources.cache.model.CommentDBObject
import com.jotagalilea.posts.datasources.cache.model.PostDBObject
import com.jotagalilea.posts.datasources.cache.model.UserDBObject
import com.jotagalilea.posts.model.domainmodel.Geolocation


/**
 * Base de datos con Room.
 */
@Database(entities = [
	PostDBObject::class,
	CommentDBObject::class,
	UserDBObject::class
], version = 1)
@TypeConverters(Geolocation::class)
abstract class PostsDB: RoomDatabase() {
	/**
	 * Instancia de la base de datos.
	 */
	private lateinit var INSTANCE: PostsDB
	// TODO: Preguntar por el lock, ¿Por qué es mejor usar un Any?:
	private val lock = Any()

	abstract fun getDao(): CachedPostsDao


	/**
	 * Obtiene la instancia de la base de datos. Si no existe se crea. Implementación básica según la
	 * guía de Room.
	 * @param context Contexto de la aplicación.
	 * @return Instancia de la BD.
	 */
	fun getDatabase(context: Context): PostsDB {
		// synchronized(PostsDB::class.java) {
		synchronized(lock) {
			if (!::INSTANCE.isInitialized) {
				INSTANCE = Room.databaseBuilder(context.applicationContext,
					PostsDB::class.java,
					"postsDB").build()
			}
		}
		return INSTANCE
	}
}
