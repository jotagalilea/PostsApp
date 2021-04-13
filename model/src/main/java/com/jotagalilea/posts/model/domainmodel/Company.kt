package com.jotagalilea.posts.model.domainmodel

import com.google.gson.annotations.SerializedName

/**
 * Clase que contiene datos de una compañía.
 */
data class Company(
    @SerializedName("name") var companyName: String,
    @SerializedName("catchPhrase") var catchPhrase: String,
    @SerializedName("bs") var bs: String,
)
