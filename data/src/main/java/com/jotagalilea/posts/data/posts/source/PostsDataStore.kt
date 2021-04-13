package com.jotagalilea.posts.data.posts.source

import com.jotagalilea.posts.model.domainmodel.Comment
import com.jotagalilea.posts.model.domainmodel.Post
import com.jotagalilea.posts.model.domainmodel.User
import io.reactivex.rxjava3.core.Single


interface PostsDataStore {

	fun getPostsList(): Single<List<Post>>
	fun getPostsFromUser(userId: Int): Single<List<Post>>
	fun getUserWithID(id: Int): Single<User>
	fun getCommentsFromPost(id: Int): Single<List<Comment>>
}