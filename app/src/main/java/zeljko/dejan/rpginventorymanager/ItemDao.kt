package zeljko.dejan.rpginventorymanager

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ItemDao {
    @Query("SELECT * FROM items")
    fun getAllItems(): List<Item>

    //select distinct category from items
    @Query("SELECT DISTINCT category FROM items")
    fun getAllCategories(): List<String>

    @Insert
    fun insertItem(item: Item): Long

    @Query("SELECT * FROM items WHERE name = :itemName")
    fun getItemByName(itemName: String): Item

    @Query("DELETE FROM items WHERE name = :itemName")
    fun deleteItemByName(itemName: String)

    @Update
    fun updateItem(currentItem: Item)
}
