package zeljko.dejan.rpginventorymanager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    foreignKeys = [ForeignKey(
        entity = Chat::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("chatId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Message(
    @PrimaryKey
    val messageId: String,

    @ColumnInfo(name = "chatId")
    val chatId: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "author")
    val author: String,

    @ColumnInfo(name = "text")
    val text: String
)