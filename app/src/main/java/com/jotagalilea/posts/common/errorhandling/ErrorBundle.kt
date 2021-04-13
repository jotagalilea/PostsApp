package com.jotagalilea.posts.common.errorhandling

import androidx.annotation.StringRes

/**
 * Interface to represent a wrapper around an [Exception] to manage errors in app layer.
 */
data class ErrorBundle(
	val throwable: Throwable,
	@param:StringRes
	val stringId: Int, // String identifier to be used by presentation layer to show multi-language error messages
	val debugMessage: String, // Debug message for logging
	val appAction: AppAction,
	val appError: AppError
)