package zeljko.dejan.rpginventorymanager

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey
    var name: String,
    var icon: Int
) {
    var quantity: Int = 0
    var properties: MutableMap<String, String> = mutableMapOf()
    var categories: MutableSet<String> = mutableSetOf()

    companion object {
        val allCategories: MutableList<String> = mutableListOf()

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
