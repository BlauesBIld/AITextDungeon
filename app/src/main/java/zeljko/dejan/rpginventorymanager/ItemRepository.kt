package zeljko.dejan.rpginventorymanager

object ItemRepository {
    private val items: MutableList<Item> = mutableListOf()

    fun addItem(item: Item) {
        items.add(item)
    }

    fun getItems(): MutableList<Item> {
        return items
    }
}
