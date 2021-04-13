package com.jotagalilea.posts.datasources.remote.errorhandling

class ThrowableWrapperException : Exception {

	constructor() : super()

	constructor(cause: Throwable) : super(cause)
}
