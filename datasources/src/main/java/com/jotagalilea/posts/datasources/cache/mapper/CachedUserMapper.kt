package com.jotagalilea.posts.datasources.cache.mapper

import com.jotagalilea.posts.datasources.cache.model.UserDBObject
import com.jotagalilea.posts.model.domainmodel.User


//TODO: Quizá debería cambiar el nombre de estas clases... ¿y métodos?
open class CachedUserMapper: CacheMapper<UserDBObject, User> {

	//TODO: ¿Qué tal "mapDBOtoCached()" y "mapCachedToDBO()"?
	//		Así en el remote podría poner "mapRemoteToDBO()", y en otro mapper "mapRemoteToCached()".
	override fun mapFromCached(type: UserDBObject): User {
		return User(
			type.id,
			type.name,
			type.userName,
			type.email,
			type.address,
			type.phone,
			type.website,
			type.company
		)
	}

	override fun mapFromCachedList(typeList: List<UserDBObject>): List<User> {
		return typeList.map { mapFromCached(it) }
	}

	override fun mapToCached(type: User): UserDBObject {
		return UserDBObject(
			id = type.id,
			name = type.name,
			userName = type.userName,
			email = type.email,
			address = type.address,
			phone = type.phone,
			website = type.website,
			company  = type.company)
	}

	override fun mapToCachedList(typeList: List<User>): List<UserDBObject> {
		return typeList.map { mapToCached(it) }
	}
}