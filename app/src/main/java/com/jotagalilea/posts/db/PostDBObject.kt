package com.jotagalilea.posts.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.jotagalilea.posts.model.Post


@Entity(
	tableName = "posts",
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


	fun asDomainModel(): Post{
		return Post(
			id = this.id,
			userId = this.userId,
			title = this.title,
			body = this.body
		)
	}

}



fun List<PostDBObject>.asDomainModelMap(): Map<Int, Post>{
	return map {
		it.id to Post(
			id = it.id,
			userId = it.userId,
			title = it.title,
			body = it.body
		)
	}.toMap()
}