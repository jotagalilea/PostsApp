package com.jotagalilea.posts.model

import com.google.gson.annotations.SerializedName

/**
 * Clase que guarda datos de geolocalización.
 */
data class Geolocation(
    @SerializedName("lat") var latitude: Float,
    @SerializedName("lng") var longitude: Float
)
