package com.jotagalilea.posts.data.posts.repository

import com.jotagalilea.posts.data.posts.source.PostsDataStoreFactory
import com.jotagalilea.posts.domain.post.repository.PostsRepository
import com.jotagalilea.posts.model.domainmodel.Comment
import com.jotagalilea.posts.model.domainmodel.Post
import com.jotagalilea.posts.model.domainmodel.User
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single


class PostsRepositoryImpl(
	private val factory: PostsDataStoreFactory
): PostsRepository {

	/**
	 * Busca la lista de posts decidiendo si tiene que llamar al remoto o a la BD.
	 */
	override fun getPostsList(): Single<List<Post>> {
		return factory.retrieveCacheDataStore().isValidCache()
			.flatMap { cached ->
				// Get data store based on whether cached data is valid
				val postsDataStore = factory.retrieveDataStore(cached)
				val postsListSource = if (cached) {
					// Getting data from cache
					postsDataStore.getPostsList()
				} else {
					// Getting data from remote, so result is cached
					postsDataStore.getPostsList()
						.flatMap { postsList ->
							// Once the result have been retrieved, save it to cache and return it
							savePosts(postsList).toSingle { postsList }
						}
				}

				postsListSource
			}
	}


	override fun getUserWithId(userID: Int): Single<User> {
		return factory.retrieveCacheDataStore().isValidCache()
			.flatMap { cached ->
				// Get data store based on whether cached data is valid
				val postsDataStore = factory.retrieveDataStore(cached)
				val userSource = if (cached) {
					// Getting data from cache
					postsDataStore.getUserWithID(userID)
				} else {
					// Getting data from remote, so result is cached
					postsDataStore.getUserWithID(userID)
						.flatMap { user ->
							// Once the result have been retrieved, save it to cache and return it
							saveUser(user).toSingle { user }
						}
				}

				userSource
			}
	}


	override fun getPostsFromUser(userID: Int): Single<List<Post>> {
		return factory.retrieveCacheDataStore().isValidCache()
			.flatMap { cached ->
				// Get data store based on whether cached data is valid
				val postsDataStore = factory.retrieveDataStore(cached)
				val postsSource = if (cached) {
					// Getting data from cache
					postsDataStore.getPostsFromUser(userID)
				} else {
					// Getting data from remote, so result is cached
					postsDataStore.getPostsFromUser(userID)
						.flatMap { posts ->
							// Once the result have been retrieved, save it to cache and return it
							savePosts(posts).toSingle { posts }
						}
				}

				postsSource
			}
	}


	override fun getCommentsFromPost(postId: Int): Single<List<Comment>> {
		return factory.retrieveCacheDataStore().isValidCache()
			.flatMap { cached ->
				// Get data store based on whether cached data is valid
				val postsDataStore = factory.retrieveDataStore(cached)
				val commentsSource = if (cached) {
					// Getting data from cache
					postsDataStore.getCommentsFromPost(postId)
				} else {
					// Getting data from remote, so result is cached
					postsDataStore.getCommentsFromPost(postId)
						.flatMap { comments ->
							// Once the result have been retrieved, save it to cache and return it
							saveComments(comments).toSingle { comments }
						}
				}

				commentsSource
			}
	}


	//TODO: Repasar si los save y clear están bien.
	override fun savePost(post: Post): Completable {
		return factory.retrieveCacheDataStore().savePost(post)
	}

	override fun savePosts(postList: List<Post>): Completable {
		return factory.retrieveCacheDataStore().savePosts(postList)
	}

	override fun saveUsers(userList: List<User>): Completable {
		return factory.retrieveCacheDataStore().saveUsers(userList)
	}

	override fun saveUser(user: User): Completable {
		return factory.retrieveCacheDataStore().saveUser(user)
	}

	override fun saveComments(commentList: List<Comment>): Completable {
		return factory.retrieveCacheDataStore().saveComments(commentList)
	}

	override fun clearPosts(): Completable {
		return factory.retrieveCacheDataStore().clearPosts()
	}

	override fun clearUsers(): Completable {
		return factory.retrieveCacheDataStore().clearUsers()
	}

	override fun clearComments(): Completable {
		return factory.retrieveCacheDataStore().clearComments()
	}

}