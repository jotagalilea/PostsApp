package com.jotagalilea.posts.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.jotagalilea.posts.model.Comment


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
)

fun List<CommentDBObject>.asDomainModel(): List<Comment>{
    return map{
        Comment(it.id, it.postId, it.name, it.email, it.body)
    }
}
