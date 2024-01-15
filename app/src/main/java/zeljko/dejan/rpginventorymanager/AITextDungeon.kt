package zeljko.dejan.rpginventorymanager

import android.app.Application
import androidx.room.Room

class AITextDungeon : Application() {
    companion object {
        val database: Database by lazy {
            Room.databaseBuilder(
                AITextDungeon.instance.applicationContext,
                Database::class.java,
                "inventory.db"
            )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }

        lateinit var instance: AITextDungeon
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
