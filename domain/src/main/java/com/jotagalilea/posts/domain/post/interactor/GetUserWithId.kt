package com.jotagalilea.posts.domain.post.interactor

import com.jotagalilea.posts.domain.executor.PostExecutionThread
import com.jotagalilea.posts.domain.executor.ThreadExecutor
import com.jotagalilea.posts.domain.interactorusecases.SingleUseCase
import com.jotagalilea.posts.domain.post.repository.PostsRepository
import com.jotagalilea.posts.model.domainmodel.User
import io.reactivex.rxjava3.core.Single

open class GetUserWithId (
	private val repository: PostsRepository,
	threadExecutor: ThreadExecutor,
	postExecutionThread: PostExecutionThread
) : SingleUseCase<User, Void>(threadExecutor, postExecutionThread) {


	private var userId: Int = -1


	fun setId(id : Int){
		userId = id
	}


	public override fun buildUseCaseObservable(params: Void?): Single<User> {
		return repository.getUserWithId(userId)
	}
}