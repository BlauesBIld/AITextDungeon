package zeljko.dejan.rpginventorymanager.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE chatId = :chatId")
    fun getMessagesForChat(chatId: String): List<Message>

    @Insert
    fun insertMessage(message: Message)

    @Query("SELECT * FROM messages WHERE chatId = :chatId AND author = :author")
    fun getMessagesByAuthor(chatId: String, author: String): List<Message>
}