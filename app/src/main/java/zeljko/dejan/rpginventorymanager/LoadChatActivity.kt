package zeljko.dejan.rpginventorymanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import zeljko.dejan.rpginventorymanager.database.Chat

class LoadChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_chat)

        val chatCards = mutableListOf(
            Chat("1", "Chat 1", "https://i.imgur.com/2zYQ5nT.png", 0, 0),
            Chat("1", "Chat 1", "https://i.imgur.com/2zYQ5nT.png", 0, 0),
        )

        val adapter = ChatCardAdapter(chatCards) { item ->
            // Handle item click
        }

        val recyclerView: RecyclerView = findViewById(R.id.chatsRecyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}