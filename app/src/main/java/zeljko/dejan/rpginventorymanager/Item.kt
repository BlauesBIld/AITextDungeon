package zeljko.dejan.rpginventorymanager

class Item(val name: String, val icon: Int){
    var quantity: Int = 0;
    var properties: MutableMap<String, String> = mutableMapOf()
    var categories: MutableSet<String> = mutableSetOf()
}
