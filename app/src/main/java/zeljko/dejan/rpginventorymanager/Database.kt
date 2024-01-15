package zeljko.dejan.rpginventorymanager

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Item::class], version = 2)
abstract class Database : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}
