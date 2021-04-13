package com.jotagalilea.posts.datasources.cache.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jotagalilea.posts.model.domainmodel.Address
import com.jotagalilea.posts.model.domainmodel.Company


/**
 * Objeto de base de datos para almacenar usuarios.
 */
@Entity(tableName = "Users")
data class UserDBObject(
	@PrimaryKey
	@ColumnInfo(index = true)
	var id: Int,
	var name: String,
	var userName: String,
	var email: String,
	@Embedded var address: Address,
	var phone: String,
	var website: String,
	@Embedded var company: Company
) {

	/*/**
	 * Convierte el objeto de BD a objeto de dominio.
	 */
	fun asDomainModel(): com.jotagalilea.posts.model.model.User {
		return com.jotagalilea.posts.model.model.User(
			id,
			name,
			userName,
			email,
			address,
			phone,
			website,
			company
		)
	}

	 */
}

