package zeljko.dejan.rpginventorymanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import zeljko.dejan.rpginventorymanager.database.Message

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        // Example messages
        val sampleMessages = listOf(
            Message(
                "1",
                "chatId1",
                System.currentTimeMillis(),
                "System",
                "Welcome to the dungeon!"
            ),
            Message("2", "chatId1", System.currentTimeMillis(), "Player", "Thanks, ready to start!")
            // Add more messages as needed for testing
        )

        val recyclerView: RecyclerView = findViewById(R.id.chatRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter =
            MessageAdapter(sampleMessages)
    }
}