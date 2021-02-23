package com.jotagalilea.posts.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jotagalilea.posts.model.Address
import com.jotagalilea.posts.model.Company
import com.jotagalilea.posts.model.User


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
	fun asDomainModel(): User {
		return User(id, name, userName, email, address, phone, website, company)
	}
}

