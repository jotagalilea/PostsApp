package com.jotagalilea.posts.di

import com.jotagalilea.posts.BuildConfig
import com.jotagalilea.posts.UiThread
import com.jotagalilea.posts.common.errorhandling.ErrorBundleBuilder
import com.jotagalilea.posts.data.posts.repository.PostsRepositoryImpl
import com.jotagalilea.posts.data.posts.source.PostsCacheDataStore
import com.jotagalilea.posts.data.posts.source.PostsDataStoreFactory
import com.jotagalilea.posts.data.posts.source.PostsRemoteDataStore
import com.jotagalilea.posts.datasources.cache.PostsCacheDataStoreImpl
import com.jotagalilea.posts.datasources.cache.PreferencesHelper
import com.jotagalilea.posts.datasources.cache.db.PostsDB
import com.jotagalilea.posts.datasources.cache.mapper.CachedCommentMapper
import com.jotagalilea.posts.datasources.cache.mapper.CachedPostMapper
import com.jotagalilea.posts.datasources.cache.mapper.CachedUserMapper
import com.jotagalilea.posts.datasources.remote.PostsRemoteDataStoreImpl
import com.jotagalilea.posts.datasources.remote.PostsServiceFactory
import com.jotagalilea.posts.datasources.remote.mapper.CommentResponseMapper
import com.jotagalilea.posts.datasources.remote.mapper.PostResponseMapper
import com.jotagalilea.posts.datasources.remote.mapper.UserResponseMapper
import com.jotagalilea.posts.domain.executor.JobExecutor
import com.jotagalilea.posts.domain.executor.PostExecutionThread
import com.jotagalilea.posts.domain.executor.ThreadExecutor
import com.jotagalilea.posts.domain.post.interactor.GetCommentsFromPost
import com.jotagalilea.posts.domain.post.interactor.GetPostsFromUser
import com.jotagalilea.posts.domain.post.interactor.GetPostsList
import com.jotagalilea.posts.domain.post.interactor.GetUserWithId
import com.jotagalilea.posts.domain.post.repository.PostsRepository
import com.jotagalilea.posts.viewmodel.PostsErrorBundleBuilder
import com.jotagalilea.posts.viewmodel.PostsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * This file is where all Koin modules are defined.
 *
 * [The original template](https://github.com/bufferapp/clean-architecture-koin-boilerplate) was a
 * port of a [Dagger template](https://github.com/bufferapp/android-clean-architecture-boilerplate),
 * and the explanation of the dependency injection porting was explained in this
 * [post](https://overflow.buffer.com/2018/09/13/a-brief-look-at-koin-on-android/).
 */
val applicationModule = module(override = true) {

	// Preferences helper:
	single { PreferencesHelper(androidContext()) }

	// Mappers remotos y cacheados:
	factory { PostResponseMapper() }
	factory { UserResponseMapper() }
	factory { CommentResponseMapper() }
	factory { CachedPostMapper() }
	factory { CachedUserMapper() }
	factory { CachedCommentMapper() }

	// ThreadExecutor:
	single<ThreadExecutor> { JobExecutor() }
	single<PostExecutionThread> { UiThread() }

	// Base de datos y su dao:
	single { get<PostsDB>().getDatabase(androidContext()) }
	factory { get<PostsDB>().getDao() }

	// DataStore para datos remotos y cacheados:
	// IMPORTANT: Named qualifiers must be unique inside the module
	factory<PostsRemoteDataStore> { PostsRemoteDataStoreImpl(get(), get(), get(), get()) }
	factory<PostsCacheDataStore> { PostsCacheDataStoreImpl(get(), get(), get(), get(), get()) }
	factory { PostsDataStoreFactory(get(), get()) }

	// Repository:
	factory<PostsRepository> { PostsRepositoryImpl(get()) }
	factory { PostsServiceFactory.makePostsService(BuildConfig.DEBUG) }
}

val postsModule = module(override = true) {
	/*factory { PostsRecyclerAdapter() }
	factory { CommentsRecyclerAdapter() }
	 */
	factory { GetCommentsFromPost(get(), get(), get()) }
	factory { GetPostsFromUser(get(), get(), get()) }
	factory { GetPostsList(get(), get(), get()) }
	factory { GetUserWithId(get(), get(), get()) }
	factory<ErrorBundleBuilder>(named("postsErrorBundleBuilder")) { PostsErrorBundleBuilder() }
	viewModel { PostsViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
}