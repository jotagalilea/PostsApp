package com.jotagalilea.posts.datasources.remote.errorhandling

import com.jotagalilea.posts.model.exception.HTTPException
import io.reactivex.rxjava3.annotations.NonNull
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException


object RemoteExceptionMapper {

	fun getException(@NonNull throwable: Throwable): Exception {

		return when (throwable) {
			// Map Retrofit HTTP exception
			is HttpException -> {
				val statusCode = throwable.code()
				val responseBody = throwable.response()?.errorBody()
				responseBody?.let {
					try {
						HTTPException(responseBody.string(), throwable, statusCode)
					} catch (e: IOException) {
						Timber.e(e, "Unable to extract Retrofit HTTP exception body")
						IllegalArgumentException("Retrofit HTTP exception cannot be parsed")
					}
				} ?: IllegalArgumentException("Retrofit HTTP exception cannot be parsed")
			}
			// Return other exceptions as-is
			is Exception -> throwable
			// Wrap any throwable but non-exception classes with a exception
			else -> ThrowableWrapperException(throwable)
		}
	}
}