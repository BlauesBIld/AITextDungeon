package zeljko.dejan.rpginventorymanager

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ItemDao {
    @Query("SELECT * FROM items")
    fun getAllItems(): List<Item>

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

    @Query("SELECT * FROM items WHERE name LIKE :query")
    fun searchItemsThatContainQuery(query: String): List<Item>

    @Query("SELECT * FROM items ORDER BY name ASC")
    suspend fun getItemsSortedByNameAsc(): List<Item>
}
