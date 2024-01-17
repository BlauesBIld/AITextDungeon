package zeljko.dejan.rpginventorymanager.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats")
    fun getAllChats(): List<Chat>

    @Insert
    fun insertChat(chat: Chat)

    @Query("SELECT * FROM chats WHERE id = :chatId")
    fun getChatById(chatId: String): Chat

    @Query("SELECT * FROM chats WHERE title LIKE :query")
    fun searchChatsThatContainQuery(query: String): List<Chat>

    @Query("SELECT * FROM chats ORDER BY title ASC")
    suspend fun getChatsSortedByNameAsc(): List<Chat>
}