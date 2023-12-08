package zeljko.dejan.rpginventorymanager

import android.app.Application
import androidx.room.Room

class Inventory : Application() {
    companion object {
        val database: InventoryDatabase by lazy {
            Room.databaseBuilder(Inventory.instance.applicationContext,
                InventoryDatabase::class.java,
                "inventory.db")
                .allowMainThreadQueries()
                .build()
        }

        lateinit var instance: Inventory
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
