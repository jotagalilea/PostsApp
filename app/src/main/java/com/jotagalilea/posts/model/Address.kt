package com.jotagalilea.posts.model

import androidx.room.Embedded
import com.google.gson.annotations.SerializedName

/**
 * Clase que almacena los datos de la dirección de un usuario.
 */
data class Address(
    @SerializedName("street") var street: String,
    @SerializedName("suite") var suite: String,
    @SerializedName("city") var city: String,
    @SerializedName("zipcode") var zipcode: String,
    @SerializedName("geo") @Embedded var geolocation: Geolocation
)
