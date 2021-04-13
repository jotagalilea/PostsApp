package com.jotagalilea.posts.model.domainmodel

import androidx.room.TypeConverter
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Clase que guarda datos de geolocalización.
 */
data class Geolocation(
    @SerializedName("lat") var latitude: Float,
    @SerializedName("lng") var longitude: Float
): Serializable {

    @TypeConverter
    fun toDBconverter(): String{
        return String.format("(%.2f, %.2f)", latitude, longitude)
    }
}
