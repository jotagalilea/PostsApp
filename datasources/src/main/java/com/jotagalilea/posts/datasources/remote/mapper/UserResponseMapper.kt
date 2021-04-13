package com.jotagalilea.posts.datasources.remote.mapper

import com.jotagalilea.posts.datasources.remote.model.UserResponse
import com.jotagalilea.posts.model.domainmodel.User


class UserResponseMapper : RemoteMapper<UserResponse, User> {

	override fun mapFromRemote(type: UserResponse): User {
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

	override fun mapFromRemoteList(typeList: List<UserResponse>): List<User> {
		return typeList.map { mapFromRemote(it) }
	}
}