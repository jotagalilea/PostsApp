package com.jotagalilea.posts.common.errorhandling

interface ErrorBundleBuilder {

	fun build(throwable: Throwable, appAction: AppAction): ErrorBundle

}