package com.jotagalilea.posts.domain.post.interactor

import com.jotagalilea.posts.domain.executor.PostExecutionThread
import com.jotagalilea.posts.domain.executor.ThreadExecutor
import com.jotagalilea.posts.domain.interactorusecases.SingleUseCase
import com.jotagalilea.posts.domain.post.repository.PostsRepository
import com.jotagalilea.posts.model.domainmodel.Comment
import io.reactivex.rxjava3.core.Single

open class GetCommentsFromPost (
	private val repository: PostsRepository,
	threadExecutor: ThreadExecutor,
	postExecutionThread: PostExecutionThread
) : SingleUseCase<List<Comment>, Void>(threadExecutor, postExecutionThread) {

	private var postId: Int = -1


	fun setId(id : Int){
		postId = id
	}

	public override fun buildUseCaseObservable(params: Void?): Single<List<Comment>> {
		//TODO: En caso de no haber inicializado el id lanzar error.
		return repository.getCommentsFromPost(postId)
	}
}