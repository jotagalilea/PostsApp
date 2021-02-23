package com.jotagalilea.posts.model

import com.google.gson.annotations.SerializedName

data class Company(
    @SerializedName("name") var companyName: String,
    @SerializedName("catchPhrase") var catchPhrase: String,
    @SerializedName("bs") var bs: String,
)
