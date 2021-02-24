package com.jotagalilea.posts.model

import com.google.gson.annotations.SerializedName
import com.jotagalilea.posts.db.UserDBObject

/**
 * Clase que contiene los datos de un usuario.
 */
data class User(
	@SerializedName("id") var id: Int,
	@SerializedName("name") var name: String,
	@SerializedName("username") var userName: String,
	@SerializedName("email") var email: String,
	@SerializedName("address") var address: Address,
	@SerializedName("phone") var phone: String,
	@SerializedName("website") var website: String,
	@SerializedName("company") var company: Company,
) {

	/**
	 * Convierte un usuario en objeto de BD.
	 */
	fun asDBObject(): UserDBObject{
		return UserDBObject(
			id = this.id,
			name = this.name,
			userName = this.userName,
			email = this.email,
			address = this.address,
			phone = this.phone,
			website = this.website,
			company  = this.company)
	}
}
