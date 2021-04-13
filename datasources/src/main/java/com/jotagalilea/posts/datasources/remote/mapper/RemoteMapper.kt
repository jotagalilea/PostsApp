package com.jotagalilea.posts.datasources.remote.mapper

interface RemoteMapper<T, V> {
	fun mapFromRemote(type: T): V
	fun mapFromRemoteList(typeList: List<T>): List <V>
}