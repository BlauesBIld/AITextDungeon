package zeljko.dejan.rpginventorymanager

import android.app.Application
import androidx.room.Room

class Inventory : Application() {
    companion object {
        lateinit var database: InventoryDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext, InventoryDatabase::class.java, "inventory"
        ).allowMainThreadQueries().build()
    }
}