package zeljko.dejan.rpginventorymanager.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class Chat(
    @PrimaryKey
    val id: String,
    val title: String,
    val coverImage: String,
    val creationDate: Long,
    val lastPlayedDate: Long
)