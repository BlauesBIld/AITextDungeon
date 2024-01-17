package zeljko.dejan.rpginventorymanager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import zeljko.dejan.rpginventorymanager.database.Chat

class LoadChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_chat)

        val chatCards = mutableListOf<Chat>()

        chatCards.addAll(AITextDungeon.database.chatDao().getAllChats())

        val adapter = ChatCardAdapter(chatCards) { item ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("chat_id", item.id)
            startActivity(intent)
        }

        val recyclerView: RecyclerView = findViewById(R.id.chatsRecyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}