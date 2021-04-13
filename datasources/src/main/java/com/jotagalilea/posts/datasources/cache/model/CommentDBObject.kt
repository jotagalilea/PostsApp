package com.jotagalilea.posts.datasources.cache.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


/**
 * Objeto de base de datos para almacenar comentarios.
 */
@Entity(
    tableName = "Comments",
    foreignKeys = [
        ForeignKey(
            entity = PostDBObject::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CommentDBObject(
    @PrimaryKey
    @ColumnInfo(index = true)
    var id: Int,
    @ColumnInfo(index = true)
    var postId: Int,
    var name: String,
    var email: String,
    var body: String
){
    //TODO: Faltaría cargarme lo que ya no use de toda la app.
    /**
     * Convierte el objeto de BD a objeto de dominio.
     */
    /*fun asDomainModel(): com.jotagalilea.posts.model.model.Comment {
        return com.jotagalilea.posts.model.model.Comment(
			id = this.id,
			postId = this.postId,
			name = this.name,
			email = this.email,
			body = this.body
		)
    }*/
}

/*/**
 * Extensión para convertir una lista de comentarios obtenida de la base de datos a objetos
 * del dominio de la app también como lista.
 */
fun List<CommentDBObject>.asDomainModel(): List<com.jotagalilea.posts.model.model.Comment>{
    return map{
        it.asDomainModel()
    }
}
*/