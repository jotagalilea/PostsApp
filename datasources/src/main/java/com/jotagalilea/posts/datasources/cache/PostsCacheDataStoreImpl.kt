package com.jotagalilea.posts.datasources.cache

import com.jotagalilea.posts.data.posts.source.PostsCacheDataStore
import com.jotagalilea.posts.datasources.cache.db.PostsDB
import com.jotagalilea.posts.datasources.cache.mapper.CachedCommentMapper
import com.jotagalilea.posts.datasources.cache.mapper.CachedPostMapper
import com.jotagalilea.posts.datasources.cache.mapper.CachedUserMapper
import com.jotagalilea.posts.model.domainmodel.Comment
import com.jotagalilea.posts.model.domainmodel.Post
import com.jotagalilea.posts.model.domainmodel.User
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single


class PostsCacheDataStoreImpl(
	private val postsDB: PostsDB,
	private val cachedPostMapper: CachedPostMapper,
	private val cachedUserMapper: CachedUserMapper,
	private val cachedCommentMapper: CachedCommentMapper,
	private val preferencesHelper: PreferencesHelper
): PostsCacheDataStore {

	companion object {
		private const val EXPIRATION_TIME = (60 * 10 * 1000).toLong()
	}


	override fun savePost(post: Post): Completable {
		return Completable.defer {
			postsDB.getDao().insertPost(cachedPostMapper.mapToCached(post))
			setLastCacheTime(System.currentTimeMillis())
			Completable.complete()
		}
	}


	override fun savePosts(posts: List<Post>): Completable {
		return Completable.defer {
			postsDB.getDao().insertAllPosts(cachedPostMapper.mapToCachedList(posts))
			setLastCacheTime(System.currentTimeMillis())
			Completable.complete()
		}
	}


	override fun clearPosts(): Completable {
		return Completable.defer {
			postsDB.getDao().clearPosts()
			Completable.complete()
		}
	}


	override fun saveUser(user: User): Completable {
		return Completable.defer {
			postsDB.getDao().insertUser(cachedUserMapper.mapToCached(user))
			setLastCacheTime(System.currentTimeMillis())
			Completable.complete()
		}
	}


	override fun saveUsers(users: List<User>): Completable {
		return Completable.defer {
			postsDB.getDao().insertAllUsers(cachedUserMapper.mapToCachedList(users))
			setLastCacheTime(System.currentTimeMillis())
			Completable.complete()
		}
	}


	override fun clearUsers(): Completable {
		return Completable.defer {
			postsDB.getDao().clearUsers()
			Completable.complete()
		}

	}


	override fun saveComments(comms: List<Comment>): Completable {
		return Completable.defer {
			postsDB.getDao().insertComments(cachedCommentMapper.mapToCachedList(comms))
			setLastCacheTime(System.currentTimeMillis())
			Completable.complete()
		}
	}


	override fun clearComments(): Completable {
		return Completable.defer {
			postsDB.getDao().clearComments()
			Completable.complete()
		}
	}


	override fun isValidCache(): Single<Boolean> {
		return Single.defer {
			val currentTime = System.currentTimeMillis()
			val lastUpdateTime = getLastCacheUpdateTimeMillis()
			val expired = currentTime - lastUpdateTime > EXPIRATION_TIME
			Single.just(postsDB.getDao().getAllPosts().isNotEmpty() && !expired)
		}
	}

	/**
	 * Retrieve a list of [Post] instances from the database.
	 */
	override fun getPostsList(): Single<List<Post>> {
		return Single.defer {
			Single.just(postsDB.getDao().getAllPosts())
		}.map {
			it.map { cachedPostMapper.mapFromCached(it) }
		}
	}

	override fun getPostsFromUser(id: Int): Single<List<Post>> {
		return Single.defer {
			Single.just(postsDB.getDao().getPostsFromUser(id))
		}.map {
			it.map { cachedPostMapper.mapFromCached(it) }
		}
	}

	override fun getUserWithID(id: Int): Single<User> {
		return Single.defer {
			Single.just(postsDB.getDao().getUserWithID(id))
		}.map { cachedUserMapper.mapFromCached(it) }
	}

	override fun getCommentsFromPost(id: Int): Single<List<Comment>> {
		return Single.defer {
			Single.just(postsDB.getDao().getCommentsFromPost(id))
		}.map {
			it.map { cachedCommentMapper.mapFromCached(it) }
		}
	}


	/**
	 * Set a point in time at when the cache was last updated.
	 */
	override fun setLastCacheTime(lastCache: Long) {
		preferencesHelper.lastCacheTime = lastCache
	}

	/**
	 * Get in millis, the last time the cache was accessed.
	 */
	private fun getLastCacheUpdateTimeMillis(): Long {
		return preferencesHelper.lastCacheTime
	}
}