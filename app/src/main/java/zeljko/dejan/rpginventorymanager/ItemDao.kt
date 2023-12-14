package zeljko.dejan.rpginventorymanager

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItemDao {
    @Query("SELECT * FROM items")
    fun getAllItems(): List<Item>

    //select distinct category from items
    @Query("SELECT DISTINCT category FROM items")
    fun getAllCategories(): List<String>

    @Insert
    fun insertItem(item: Item): Long
}
