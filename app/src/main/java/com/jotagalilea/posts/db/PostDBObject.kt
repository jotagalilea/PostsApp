package com.jotagalilea.posts.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.jotagalilea.posts.model.Post


/**
 * Objeto de BD para almacenar posts.
 */
@Entity(
	tableName = "Posts",
	foreignKeys = [
		ForeignKey(
			entity = UserDBObject::class,
			parentColumns = ["id"],
			childColumns = ["userId"],
			onDelete = ForeignKey.CASCADE
		)
	]
)
data class PostDBObject(
	@PrimaryKey
	@ColumnInfo(index = true)
	var id: Int,
	@ColumnInfo(index = true)
	var userId: Int,
	var title: String,
	var body: String
) {

	/**
	 * Convierte el objeto de BD a objeto de dominio.
	 */
	fun asDomainModel(): Post{
		return Post(
			id = this.id,
			userId = this.userId,
			title = this.title,
			body = this.body
		)
	}

}


/**
 * Extensión para convertir una lista de posts obtenida de la base de datos a objetos
 * del dominio de la app en forma de Map, usando el ID como clave.
 */
fun List<PostDBObject>.asDomainModelMap(): Map<Int, Post>{
	return map {
		it.id to it.asDomainModel()
	}.toMap()
}