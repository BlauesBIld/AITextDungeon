package zeljko.dejan.rpginventorymanager

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "items")
@TypeConverters(Converters::class)
data class Item(
    @PrimaryKey
    var name: String,
    var icon: Int
) {
    var quantity: Int = 0
    var properties: MutableMap<String, String> = mutableMapOf()
    var category: String = ""

    companion object {
        val allCategories: MutableList<String> = mutableListOf()
        val defaultCategories: List<String>
            get() = listOf("Armor", "Weapon", "Potion", "Food")


        val icons = arrayOf(
            R.drawable.item_ic_bow,
            R.drawable.item_ic_bread,
            R.drawable.item_ic_cape,
            R.drawable.item_ic_key,
            R.drawable.item_ic_potion,
            R.drawable.item_ic_necklace,
            R.drawable.item_ic_pouch,
            R.drawable.item_ic_robe,
            R.drawable.item_ic_staff,
            R.drawable.item_ic_sword,
            R.drawable.item_ic_scroll,
            R.drawable.item_ic_armor
        )

        init {
            addDefaultCategories()
        }

        private fun addDefaultCategories() {
            allCategories.addAll(listOf("Armor", "Weapon", "Potion", "Food"))
        }

        fun addCategory(category: String) {
            if (category !in allCategories) {
                allCategories.add(category)
            }
        }

        fun updateCategoriesFromDatabase(newCategories: List<String>) {
            allCategories.clear()
            allCategories.addAll(newCategories)
        }
    }
}
