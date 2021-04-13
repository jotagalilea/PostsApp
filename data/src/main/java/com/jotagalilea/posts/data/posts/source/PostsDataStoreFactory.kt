package com.jotagalilea.posts.data.posts.source

open class PostsDataStoreFactory(
	private val postsCacheDataStore: PostsCacheDataStore,
	private val postsRemoteDataStore: PostsRemoteDataStore
) {

	//TODO: Estudiar cuándo es o no válido lo que hay cacheado, y cuando habría que hacer la llamada
	//		con true o false.
	open fun retrieveDataStore(isValidCache: Boolean): PostsDataStore{
		if (isValidCache) {
			return retrieveCacheDataStore()
		}
		return retrieveRemoteDataStore()
	}


	open fun retrieveCacheDataStore(): PostsCacheDataStore {
		return postsCacheDataStore
	}


	open fun retrieveRemoteDataStore(): PostsRemoteDataStore {
		return postsRemoteDataStore
	}

}