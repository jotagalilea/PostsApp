package com.jotagalilea.posts.common.widget.error

import com.jotagalilea.posts.common.errorhandling.ErrorBundle

interface ErrorListener {

    fun onRetry(errorBundle: ErrorBundle)
}