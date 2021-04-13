package com.jotagalilea.posts.domain.post.interactor

import com.jotagalilea.posts.domain.executor.PostExecutionThread
import com.jotagalilea.posts.domain.executor.ThreadExecutor
import com.jotagalilea.posts.domain.interactorusecases.SingleUseCase
import com.jotagalilea.posts.domain.post.repository.PostsRepository
import com.jotagalilea.posts.model.domainmodel.Post
import io.reactivex.rxjava3.core.Single

open class GetPostsFromUser(
	private val repository: PostsRepository,
	threadExecutor: ThreadExecutor,
	postExecutionThread: PostExecutionThread
) : SingleUseCase<List<Post>, Void>(threadExecutor, postExecutionThread) {

	private var userId: Int = -1


	fun setId(id : Int){
		userId = id
	}


	public override fun buildUseCaseObservable(params: Void?): Single<List<Post>> {
		return repository.getPostsFromUser(userId)
	}
}