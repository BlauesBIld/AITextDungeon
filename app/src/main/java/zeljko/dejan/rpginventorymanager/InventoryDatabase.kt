package zeljko.dejan.rpginventorymanager

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Item::class], version = 1)
abstract class InventoryDatabase : RoomDatabase() {
    abstract val dao: ItemDao
}
