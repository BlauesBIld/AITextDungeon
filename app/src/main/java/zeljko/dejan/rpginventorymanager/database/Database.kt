package zeljko.dejan.rpginventorymanager.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Item::class, Chat::class, Message::class], version = 4)
abstract class Database : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
}
