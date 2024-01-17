package zeljko.dejan.rpginventorymanager

import android.graphics.Rect
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import zeljko.dejan.rpginventorymanager.database.Message
import java.util.UUID

enum class ChatState {
    AWAITING_DESCRIPTION,
    AWAITING_TITLE,
    IN_PROGRESS
}

class ChatActivity : AppCompatActivity() {
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter
    private lateinit var currentChatTitleTextView: TextView

    private var chatState = ChatState.AWAITING_DESCRIPTION
    private var chatId: String? = null

    private var currentDescription: String = ""
    private var currentTitle: String = ""

    private var isKeyboardOpened = false

    object ChatConstants {
        const val AI_NAME: String = "Narrator"
        const val PLAYER_NAME: String = "You"
        const val FIRST_MESSAGE =
            "Hello! To start your new adventure, please write a short description about what kind of adventure you want to have. I will generate all the missing details."
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        messagesRecyclerView = findViewById(R.id.messagesRecyclerView)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        currentChatTitleTextView = findViewById(R.id.chatTitle)

        initializeMessages()
        setupMessageInputListener()
        setupKeyboardVisibilityListener()
    }

    private fun initializeMessages() {
        chatId = intent.getStringExtra("chat_id")
        val messages: MutableList<Message> = mutableListOf()

        if (chatId != null) {
            messages.addAll(
                AITextDungeon.database.messageDao().getMessagesForChat(chatId.toString())
            )
        } else {
            messages.add(
                Message(
                    UUID.randomUUID().toString(),
                    "-1",
                    System.currentTimeMillis(),
                    ChatConstants.AI_NAME,
                    ChatConstants.FIRST_MESSAGE
                )
            )
        }

        adapter = MessageAdapter(messages)
        messagesRecyclerView.adapter = adapter
    }

    private fun setupMessageInputListener() {
        val inputField = findViewById<EditText>(R.id.inputMessage)
        val sendButton = findViewById<ImageButton>(R.id.sendButton)

        sendButton.setOnClickListener {
            val userInput = inputField.text.toString()
            if (!userInput.isEmpty()) {
                handleUserInput(userInput)
                inputField.text.clear()
            }
        }
    }

    private fun handleUserInput(userInput: String) {
        when (chatState) {
            ChatState.AWAITING_DESCRIPTION -> {
                currentDescription = userInput
                displayMessage(ChatConstants.PLAYER_NAME, userInput)
                displayMessage(ChatConstants.AI_NAME, "Please enter a title for your adventure.")
                chatState = ChatState.AWAITING_TITLE
            }

            ChatState.AWAITING_TITLE -> {
                displayMessage(ChatConstants.PLAYER_NAME, userInput)
                if (isValidTitle(userInput)) {
                    currentTitle = userInput
                    currentChatTitleTextView.text = currentTitle
                    createNewChatInDatabase()
                    displayInitialStoryMessage()
                    chatState = ChatState.IN_PROGRESS
                } else {
                    displayMessage(
                        ChatConstants.AI_NAME,
                        "Please enter a valid title (max 40 characters, only letters, numbers, spaces, and punctuation)."
                    )
                }
            }

            ChatState.IN_PROGRESS -> {
                val response = processUserInput(userInput)
                displayMessage(ChatConstants.PLAYER_NAME, userInput)
                displayMessage(ChatConstants.AI_NAME, response)
            }
        }
    }

    private fun displayInitialStoryMessage() {
        displayMessage(
            ChatConstants.AI_NAME,
            processUserInput("")
        )
    }

    private fun createNewChatInDatabase() {

    }

    private fun isValidTitle(title: String): Boolean {
        return title.length <= 40 && title.matches(Regex("[A-Za-z0-9 .,?!]+"))
    }

    private fun displayMessage(author: String, text: String) {
        val newMessage = Message(
            UUID.randomUUID().toString(),
            chatId ?: "-1",
            System.currentTimeMillis(),
            author,
            text
        )
        adapter.addMessage(newMessage)
        messagesRecyclerView.scrollToPosition(adapter.itemCount - 1)
    }

    private fun setupKeyboardVisibilityListener() {
        val rootView = findViewById<RecyclerView>(R.id.messagesRecyclerView)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height

            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) {
                if (!isKeyboardOpened) {
                    isKeyboardOpened = true
                    messagesRecyclerView.scrollToPosition(adapter.itemCount - 1)
                }
            } else if (isKeyboardOpened) {
                isKeyboardOpened = false
            }
        }
    }

    private fun processUserInput(input: String): String {
        // Implement the logic to send the input to the AI and get a response
        // This is a placeholder; actual implementation will depend on how you integrate with the AI
        return "AI response to '$input'"
    }
}

