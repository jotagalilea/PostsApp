package com.jotagalilea.posts.datasources.cache.mapper

interface CacheMapper<T, V> {
	fun mapFromCached(type: T): V
	fun mapFromCachedList(typeList: List<T>): List <V>
	fun mapToCached(type: V): T
	fun mapToCachedList(typeList: List<V>): List <T>
}