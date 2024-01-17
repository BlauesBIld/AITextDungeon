package zeljko.dejan.rpginventorymanager

import android.app.Application
import androidx.room.Room
import zeljko.dejan.rpginventorymanager.database.Database

class AITextDungeon : Application() {
    companion object {
        val database: Database by lazy {
            Room.databaseBuilder(
                AITextDungeon.instance.applicationContext,
                Database::class.java,
                "aitextdungeon.db"
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
