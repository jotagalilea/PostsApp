package com.jotagalilea.posts.model

import com.google.gson.annotations.SerializedName
import com.jotagalilea.posts.db.UserDBObject

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
    fun asDBObject(): UserDBObject{
        return UserDBObject(id, name, userName, email, address, phone, website, company)
    }
}
