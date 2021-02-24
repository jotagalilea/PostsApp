package com.jotagalilea.posts.model

import com.google.gson.annotations.SerializedName
import com.jotagalilea.posts.db.CommentDBObject

/**
 * Clase que contiene los datos de un comentario.
 */
data class Comment(
    @SerializedName("id") var id: Int,
    @SerializedName("postId") var postId: Int,
    @SerializedName("name") var name: String,
    @SerializedName("email") var email: String,
    @SerializedName("body") var body: String
) {

    /**
     * Convierte el objeto de dominio a objeto de BD.
     */
    fun asDomainModel(): CommentDBObject{
        return CommentDBObject(
            id = this.id,
            postId = this.postId,
            name = this.name,
            email = this.email,
            body = this.body
        )
    }
}

/**
 * Extensión para convertir una lista de comentarios del dominio a objetos insertables en la base
 * de datos.
 */
fun List<Comment>.asDBObject(): List<CommentDBObject>{
    return map {
        it.asDomainModel()
    }
}
