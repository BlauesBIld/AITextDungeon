package zeljko.dejan.rpginventorymanager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.loadButton).setOnClickListener {
            val intent = Intent(this, LoadChatActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.createButton).setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
    }
}
