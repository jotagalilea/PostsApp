package com.jotagalilea.posts.datasources.remote.model

import com.google.gson.annotations.SerializedName
import com.jotagalilea.posts.model.domainmodel.Address
import com.jotagalilea.posts.model.domainmodel.Company

data class UserResponse (
	@SerializedName("id") var id: Int,
	@SerializedName("name") var name: String,
	@SerializedName("username") var userName: String,
	@SerializedName("email") var email: String,
	@SerializedName("address") var address: Address,
	@SerializedName("phone") var phone: String,
	@SerializedName("website") var website: String,
	@SerializedName("company") var company: Company
)