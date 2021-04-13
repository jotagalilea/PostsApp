package com.jotagalilea.posts.common.viewmodel

sealed class CommonEvent {

    object Unauthorized : CommonEvent()
}