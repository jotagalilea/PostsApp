package com.jotagalilea.posts.common.viewmodel

import androidx.lifecycle.ViewModel
import com.jotagalilea.posts.common.viewmodel.CommonEvent.Unauthorized
import com.jotagalilea.posts.model.exception.HTTPException
import io.reactivex.rxjava3.observers.DisposableCompletableObserver
import io.reactivex.rxjava3.observers.DisposableSingleObserver

abstract class CommonEventsViewModel : ViewModel() {

    lateinit var commonLiveEvent: SingleLiveEvent<CommonEvent>

    abstract class CompletableRemoteInterceptor(private val commonLiveEvent: SingleLiveEvent<CommonEvent>) : DisposableCompletableObserver() {

        abstract override fun onComplete()

        override fun onError(e: Throwable) {
            when (e) {
                is HTTPException -> {
                    val errorCode = e.statusCode
                    if (errorCode == 401) {
                        commonLiveEvent.value = Unauthorized
                    } else {
                        onRegularError(e)
                    }
                }
                else -> onRegularError(e)
            }
        }

        abstract fun onRegularError(e: Throwable)
    }

    abstract class SingleRemoteInterceptor<R>(private val commonLiveEvent: SingleLiveEvent<CommonEvent>) : DisposableSingleObserver<R>() {

        abstract override fun onSuccess(t: R)

        override fun onError(e: Throwable) {
            when (e) {
                is HTTPException -> {
                    val errorCode = e.statusCode
                    if (errorCode == 401) {
                        commonLiveEvent.value = Unauthorized
                    } else {
                        onRegularError(e)
                    }
                }
                else -> onRegularError(e)
            }
        }

        abstract fun onRegularError(e: Throwable)
    }
}