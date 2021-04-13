package com.jotagalilea.posts.datasources.remote

import com.jotagalilea.posts.data.posts.source.PostsRemoteDataStore
import com.jotagalilea.posts.datasources.remote.errorhandling.RemoteExceptionMapper
import com.jotagalilea.posts.datasources.remote.mapper.CommentResponseMapper
import com.jotagalilea.posts.datasources.remote.mapper.PostResponseMapper
import com.jotagalilea.posts.datasources.remote.mapper.UserResponseMapper
import com.jotagalilea.posts.model.domainmodel.Comment
import com.jotagalilea.posts.model.domainmodel.Post
import com.jotagalilea.posts.model.domainmodel.User
import io.reactivex.rxjava3.core.Single


class PostsRemoteDataStoreImpl(
	private val postsService: PostsService,
	private val postsResponseMapper: PostResponseMapper,
	private val usersResponseMapper: UserResponseMapper,
	private val commentsResponseMapper: CommentResponseMapper,
): PostsRemoteDataStore {

	override fun getPostsList(): Single<List<Post>> {
		return postsService.getPostsList()
			.onErrorResumeNext {throwable ->
				Single.error(RemoteExceptionMapper.getException(throwable))
			}
			.map { it.items }
			.map { posts ->
				postsResponseMapper.mapFromRemoteList(posts)
			}
	}

	override fun getUserWithID(id: Int): Single<User> {
		return postsService.getUserWithId(id)
			.onErrorResumeNext { throwable ->
				Single.error(RemoteExceptionMapper.getException(throwable))
			}
			.map { user ->
				usersResponseMapper.mapFromRemote(user)
			}
	}

	override fun getCommentsFromPost(id: Int): Single<List<Comment>> {
		return postsService.getCommentsOfPost(id)
			.onErrorResumeNext {throwable ->
				Single.error(RemoteExceptionMapper.getException(throwable))
			}
			.map { it.items }
			.map { comments ->
				commentsResponseMapper.mapFromRemoteList(comments)
			}
	}
}