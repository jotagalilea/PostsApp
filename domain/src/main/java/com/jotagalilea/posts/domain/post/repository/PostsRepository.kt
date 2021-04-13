package com.jotagalilea.posts.domain.post.repository

import com.jotagalilea.posts.model.domainmodel.Comment
import com.jotagalilea.posts.model.domainmodel.Post
import com.jotagalilea.posts.model.domainmodel.User
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single


interface PostsRepository {

	// Get
	fun getPostsList(): Single<List<Post>>
	fun getUserWithId(userID: Int): Single<User>
	fun getPostsFromUser(userID: Int): Single<List<Post>>
	fun getCommentsFromPost(postId: Int): Single<List<Comment>>

	// Save
	fun savePost(post: Post): Completable
	fun savePosts(postList: List<Post>): Completable
	fun saveUsers(userList: List<User>): Completable
	fun saveUser(user: User): Completable
	fun saveComments(commentList: List<Comment>): Completable

	// Clear
	fun clearPosts(): Completable
	fun clearUsers(): Completable
	fun clearComments(): Completable

}