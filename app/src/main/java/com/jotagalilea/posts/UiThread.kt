package com.jotagalilea.posts

import com.jotagalilea.posts.domain.executor.PostExecutionThread
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler


/**
 * MainThread (UI Thread) implementation based on a [Scheduler]
 * which will execute actions on the Android UI thread
 */
class UiThread : PostExecutionThread {

	override val scheduler: Scheduler
		get() = AndroidSchedulers.mainThread()
}