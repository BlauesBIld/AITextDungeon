package zeljko.dejan.rpginventorymanager.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import zeljko.dejan.rpginventorymanager.ChatServiceState

@Entity(tableName = "chats")
data class Chat(
    @PrimaryKey
    val id: String,
    var title: String,
    val description: String,
    val threadId: String,
    val coverImage: String,
    val creationTimeStamp: Long,
    var lastPlayedTimeStamp: Long,
    var chatServiceState: ChatServiceState
)