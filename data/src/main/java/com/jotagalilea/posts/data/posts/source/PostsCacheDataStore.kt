package com.jotagalilea.posts.data.posts.source

import com.jotagalilea.posts.model.domainmodel.Comment
import com.jotagalilea.posts.model.domainmodel.Post
import com.jotagalilea.posts.model.domainmodel.User
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single


interface PostsCacheDataStore: PostsDataStore{

	fun savePost(post: Post): Completable
	fun savePosts(posts: List<Post>): Completable
	fun clearPosts(): Completable
	fun saveUser(user: User): Completable
	fun saveUsers(users: List<User>): Completable
	fun clearUsers(): Completable
	fun saveComments(comms: List<Comment>): Completable
	fun clearComments(): Completable
	fun isValidCache(): Single<Boolean>
	fun setLastCacheTime(lastCache: Long)
	//fun getPostsFromUser(id: Int): Single<List<Post>>

}