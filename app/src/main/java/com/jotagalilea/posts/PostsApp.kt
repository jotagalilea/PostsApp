package com.jotagalilea.posts

import android.app.Application
import com.jotagalilea.posts.common.log.TimberLogImplementation
import com.jotagalilea.posts.di.applicationModule
import com.jotagalilea.posts.di.mainModule
import com.jotagalilea.posts.di.postsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PostsApp : Application() {

	override fun onCreate() {
		super.onCreate()

		// start Koin!
		startKoin {
			// Android context
			androidContext(this@PostsApp)
			// modules
			modules(listOf(applicationModule, mainModule, postsModule))
		}

		// Initialize logging library
		TimberLogImplementation.init()
	}
}