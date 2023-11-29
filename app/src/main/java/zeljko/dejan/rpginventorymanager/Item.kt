package zeljko.dejan.rpginventorymanager

class Item(var name: String, var icon: Int) {
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
