package zeljko.dejan.rpginventorymanager

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import zeljko.dejan.rpginventorymanager.database.Chat
import zeljko.dejan.rpginventorymanager.database.Message
import java.util.UUID

class ChatActivity : AppCompatActivity() {
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter
    private lateinit var currentChatTitleTextView: TextView
    private lateinit var menuButton: ImageButton

    private var chatState = ChatState.AWAITING_DESCRIPTION
    private var chatServiceState: ChatServiceState? = null
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

        val menuButton = findViewById<ImageButton>(R.id.menuButton)
        menuButton.setOnClickListener { showPopupMenu(it) }

        setupMessageInputListenerAndSendButtonIcon()
        initializeMessages()
        setupKeyboardVisibilityListener()
    }

    private fun showPopupMenu(anchor: View) {
        val popupView = layoutInflater.inflate(R.layout.custom_popup_menu, null)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            showAsDropDown(anchor)
            elevation = 10f
            isOutsideTouchable = true
        }


        if (chatId != null) {
            popupView.findViewById<TextView>(R.id.rename_chat).setOnClickListener {
                showRenameDialog()
                popupWindow.dismiss()
            }

            popupView.findViewById<TextView>(R.id.delete_chat).setOnClickListener {
                showConfirmationDialog()
                popupWindow.dismiss()
            }
        } else {
            popupView.findViewById<TextView>(R.id.rename_chat).apply {
                isEnabled = false
                setTextColor(ContextCompat.getColor(context, R.color.neutralColorDisabled))
            }

            popupView.findViewById<TextView>(R.id.delete_chat).apply {
                isEnabled = false
                setTextColor(ContextCompat.getColor(context, R.color.redDisabled))
            }
        }
    }

    private fun showConfirmationDialog() {
        val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle("Delete Chat")
            .setMessage("Are you sure you want to delete this chat?")
            .setPositiveButton("Delete", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        val messageView = dialog.findViewById<TextView>(android.R.id.message)
        messageView?.setTextColor(ContextCompat.getColor(this, R.color.white))

        val deleteButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        deleteButton.setTextColor(ContextCompat.getColor(this, R.color.textRed))

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            callDeleteChatService()
            dialog.dismiss()
        }
    }

    private fun showRenameDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_rename_chat, null)
        val editTextChatName = dialogView.findViewById<EditText>(R.id.editTextChatName)

        val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle("Rename Chat")
            .setView(dialogView)
            .setPositiveButton("Rename", null)
            .setNegativeButton("Cancel", null)
            .create()

        editTextChatName.text.append(currentTitle)

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val newChatName = editTextChatName.text.toString().trim()
            if (newChatName.isNotEmpty() && isValidTitle(newChatName)) {
                renameChat(newChatName)
                dialog.dismiss()
            } else {
                editTextChatName.error = "Please enter a valid chat name!"
            }
        }
    }

    private fun renameChat(newName: String) {
        currentChatTitleTextView.text = newName
        currentTitle = newName
        val chat = AITextDungeon.database.chatDao().getChatById(chatId.toString())
        chat.title = newName
        AITextDungeon.database.chatDao().updateChat(chat)
    }

    private fun initializeMessages() {
        chatId = intent.getStringExtra("chat_id")
        val messages: MutableList<Message> = mutableListOf()

        if (chatId != null) {
            loadChatInformationAndMessagesFromDatabase(messages)
            chatState = ChatState.IN_PROGRESS
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

        handleCurrentChatServiceState()

        messagesRecyclerView.scrollToPosition(adapter.itemCount - 1)
    }

    private fun handleCurrentChatServiceState() {
        when (chatServiceState) {
            ChatServiceState.WAITING_FOR_USER_INPUT -> {
                enableTextFieldAndSendButton()
            }

            ChatServiceState.WAITING_FOR_RESPONSE -> {
                disableTextFieldAndSendButton()
                displayDummyMessage()
                checkEverySecondIfDatabaseEntryIsUpdated()
            }

            ChatServiceState.ERROR_CREATING_CHAT -> {
                disableTextFieldAndSendButton()
                displayDummyMessage()
                updateLastAIMessageText("Failed to get response from server")
                setSendButtonToRetryAndSetOnclickListenerToCallCreateChatService()
            }

            ChatServiceState.ERROR_GETTING_NEXT_MESSAGE -> {
                val lastUserText = AITextDungeon.database.messageDao()
                    .getMessagesByAuthor(chatId.toString(), ChatConstants.PLAYER_NAME)
                    .lastOrNull()?.text ?: ""

                disableTextFieldAndSendButton()
                displayDummyMessage()
                updateLastAIMessageText("Failed to get response from server")
                setSendButtonToRetryAndSetOnclickListenerToCallGetNextMessage(lastUserText)
            }

            else -> {}
        }
    }

    private fun checkEverySecondIfDatabaseEntryIsUpdated() {
        CoroutineScope(Dispatchers.Main).launch {
            var currentChatServiceState = ChatServiceState.WAITING_FOR_RESPONSE

            while (currentChatServiceState == ChatServiceState.WAITING_FOR_RESPONSE) {
                val chat = AITextDungeon.database.chatDao().getChatById(chatId.toString())
                currentChatServiceState = chat.chatServiceState
                delay(1000)
            }

            Log.i(
                "ChatActivity",
                "checkEverySecondIfDatabaseEntryIsUpdated: " + currentChatServiceState
            )

            when (currentChatServiceState) {
                ChatServiceState.WAITING_FOR_USER_INPUT -> {
                    enableTextFieldAndSendButton()
                    val lastMessage = AITextDungeon.database.messageDao()
                        .getMessagesByAuthor(chatId.toString(), ChatConstants.AI_NAME)
                        .lastOrNull()
                    updateLastAIMessageText(
                        lastMessage?.text ?: "Failed to get response from server"
                    )
                }

                ChatServiceState.ERROR_CREATING_CHAT -> {
                    disableTextFieldAndSendButton()
                    displayDummyMessage()
                    updateLastAIMessageText("Failed to get response from server")
                    setSendButtonToRetryAndSetOnclickListenerToCallCreateChatService()
                }

                ChatServiceState.ERROR_GETTING_NEXT_MESSAGE -> {
                    val lastUserText = AITextDungeon.database.messageDao()
                        .getMessagesByAuthor(chatId.toString(), ChatConstants.PLAYER_NAME)
                        .lastOrNull()?.text ?: ""

                    disableTextFieldAndSendButton()
                    displayDummyMessage()
                    updateLastAIMessageText("Failed to get response from server")
                    setSendButtonToRetryAndSetOnclickListenerToCallGetNextMessage(lastUserText)
                }


                else -> {}
            }
        }
    }


    private fun loadChatInformationAndMessagesFromDatabase(messages: MutableList<Message>) {
        val chat = AITextDungeon.database.chatDao().getChatById(chatId.toString())
        currentTitle = chat.title
        currentDescription = chat.description
        currentChatTitleTextView.text = currentTitle

        chatServiceState = chat.chatServiceState

        messages.add(
            Message(
                UUID.randomUUID().toString(),
                chatId.toString(),
                -1,
                ChatConstants.AI_NAME,
                ChatConstants.FIRST_MESSAGE
            )
        )
        messages.add(
            Message(
                UUID.randomUUID().toString(),
                chatId.toString(),
                -1,
                ChatConstants.PLAYER_NAME,
                currentDescription
            )
        )
        messages.add(
            Message(
                UUID.randomUUID().toString(),
                chatId.toString(),
                -1,
                ChatConstants.AI_NAME,
                "Please enter a title for your adventure."
            )
        )
        messages.add(
            Message(
                UUID.randomUUID().toString(),
                chatId.toString(),
                -1,
                ChatConstants.PLAYER_NAME,
                currentTitle
            )
        )

        messages.addAll(
            AITextDungeon.database.messageDao().getMessagesForChat(chatId.toString())
        )
    }

    private fun setupMessageInputListenerAndSendButtonIcon() {
        val inputField = findViewById<EditText>(R.id.inputMessage)
        val sendButton = findViewById<ImageButton>(R.id.sendButton)

        sendButton.setImageResource(R.drawable.ic_send)
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
                displayMessage(
                    ChatConstants.AI_NAME,
                    "Please enter a title for your adventure. (max. 20 characters, only letters, numbers, spaces, and punctuation)"
                )
                chatState = ChatState.AWAITING_TITLE
            }

            ChatState.AWAITING_TITLE -> {
                displayMessage(ChatConstants.PLAYER_NAME, userInput)
                if (isValidTitle(userInput)) {
                    currentTitle = userInput
                    currentChatTitleTextView.text = currentTitle
                    displayMessage(
                        ChatConstants.AI_NAME,
                        "Generating world... \n\nKeep in mind that generating messages takes a while."
                    )
                    displayDummyMessage()
                    createNewChatInDatabaseAndDisplayIntroMessage()
                    chatState = ChatState.IN_PROGRESS
                } else {
                    displayMessage(
                        ChatConstants.AI_NAME,
                        "Please enter a valid title (max 20 characters, only letters, numbers, spaces, and punctuation)."
                    )
                }
            }

            ChatState.IN_PROGRESS -> {
                displayMessage(ChatConstants.PLAYER_NAME, userInput)
                setLastTimePlayedTimeStampToNow()
                displayDummyMessage()
                processUserInputAndDisplayAIMessage(userInput)
            }
        }
    }

    private fun createNewChatInDatabaseAndDisplayIntroMessage() {
        disableTextFieldAndSendButton()
        CoroutineScope(Dispatchers.Main).launch {
            val threadId = ChatService.callCreateChatService(currentTitle, currentDescription)
            threadId?.let {
                val newChat = Chat(
                    UUID.randomUUID().toString(),
                    currentTitle,
                    currentDescription,
                    threadId,
                    "tbd",
                    System.currentTimeMillis(),
                    System.currentTimeMillis(),
                    ChatServiceState.WAITING_FOR_RESPONSE
                )
                AITextDungeon.database.chatDao().insertChat(newChat)
                chatId = newChat.id

                displayIntroMessage()
                enableTextFieldAndSendButton()
            } ?: run {
                setChatServiceState(ChatServiceState.ERROR_CREATING_CHAT)
                setSendButtonToRetryAndSetOnclickListenerToCallCreateChatService()
                updateLastAIMessageText("Failed to get response from server")
            }
        }
    }

    private fun displayIntroMessage() {
        val threadId = AITextDungeon.database.chatDao().getChatById(chatId.toString()).threadId
        CoroutineScope(Dispatchers.Main).launch {
            setChatServiceState(ChatServiceState.WAITING_FOR_RESPONSE)
            val message = ChatService.callGetNextMessage(threadId)
            message?.let {
                val messageEntity = updateLastAIMessageText(
                    message
                )

                if (chatId != null && chatServiceState != ChatServiceState.ERROR_CREATING_CHAT && chatServiceState != ChatServiceState.ERROR_CREATING_CHAT) {
                    AITextDungeon.database.messageDao().insertMessage(messageEntity)
                }
                setChatServiceState(ChatServiceState.WAITING_FOR_USER_INPUT)
            } ?: run {
                setChatServiceState(ChatServiceState.ERROR_GETTING_NEXT_MESSAGE)
                setSendButtonToRetryAndSetOnclickListenerToCallGetNextMessage(threadId)
                updateLastAIMessageText("Failed to get response from server")
            }
        }
    }

    private fun isValidTitle(title: String): Boolean {
        return title.length <= 20 && title.matches(Regex("[A-Za-z0-9 .,?!]+"))
    }

    private fun displayMessage(author: String, text: String) {
        val textTrimmed = text.trim()

        val newMessage = Message(
            UUID.randomUUID().toString(),
            chatId ?: "-1",
            System.currentTimeMillis(),
            author,
            textTrimmed
        )
        adapter.addMessage(newMessage)

        if (chatId != null) {
            AITextDungeon.database.messageDao().insertMessage(newMessage)
        }

        messagesRecyclerView.scrollToPosition(adapter.itemCount - 1)
    }

    private fun displayDummyMessage() {
        val newMessage = Message(
            "-1",
            "-1",
            System.currentTimeMillis(),
            ChatConstants.AI_NAME,
            ""
        )
        adapter.addMessage(newMessage)

        messagesRecyclerView.scrollToPosition(adapter.itemCount - 1)
    }

    private fun updateLastAIMessageText(text: String): Message {
        val newMessage = Message(
            UUID.randomUUID().toString(),
            chatId ?: "-1",
            System.currentTimeMillis(),
            ChatConstants.AI_NAME,
            text
        )
        adapter.updateLastAIMessage(newMessage)

        messagesRecyclerView.scrollToPosition(adapter.itemCount - 1)

        return newMessage
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

    private fun processUserInputAndDisplayAIMessage(input: String) {
        val threadId = AITextDungeon.database.chatDao().getChatById(chatId.toString()).threadId
        CoroutineScope(Dispatchers.Main).launch {
            setChatServiceState(ChatServiceState.WAITING_FOR_RESPONSE)
            disableTextFieldAndSendButton()
            val message = ChatService.callSendMessageAndGetResponse(threadId, input)
            message?.let {
                val messageEntity = updateLastAIMessageText(
                    message
                )

                AITextDungeon.database.messageDao().insertMessage(messageEntity)

                enableTextFieldAndSendButton()
                setChatServiceState(ChatServiceState.WAITING_FOR_USER_INPUT)
            } ?: run {
                setChatServiceState(ChatServiceState.ERROR_GETTING_NEXT_MESSAGE)
                setSendButtonToRetryAndSetOnclickListenerToCallGetNextMessage(input)
                updateLastAIMessageText("Failed to get response from server")
            }
        }
    }

    private fun setChatServiceState(newChatServiceState: ChatServiceState) {
        chatServiceState = newChatServiceState
        val chat = AITextDungeon.database.chatDao().getChatById(chatId.toString())
        chat.chatServiceState = newChatServiceState
        AITextDungeon.database.chatDao().updateChat(chat)
    }

    private fun setSendButtonToRetryAndSetOnclickListenerToCallGetNextMessage(input: String) {
        val sendButton = findViewById<ImageButton>(R.id.sendButton)
        sendButton.isEnabled = true
        sendButton.setImageResource(R.drawable.ic_retry)
        sendButton.setOnClickListener {
            setupMessageInputListenerAndSendButtonIcon()
            updateLastAIMessageText("")
            processUserInputAndDisplayAIMessage(input)
        }
    }


    private fun setSendButtonToRetryAndSetOnclickListenerToCallCreateChatService() {
        val sendButton = findViewById<ImageButton>(R.id.sendButton)
        sendButton.isEnabled = true
        sendButton.setImageResource(R.drawable.ic_retry)
        sendButton.setOnClickListener {
            setupMessageInputListenerAndSendButtonIcon()
            updateLastAIMessageText("")
            createNewChatInDatabaseAndDisplayIntroMessage()
        }
    }

    private fun callDeleteChatService() {
        val threadId = AITextDungeon.database.chatDao().getChatById(chatId.toString()).threadId
        CoroutineScope(Dispatchers.Main).launch {
            val success = ChatService.callDeleteChatService(threadId)
            if (success) {
                Log.i("ChatActivity", "callDeleteChatService: Successfully deleted chat")
            } else {
                throw Exception("Failed to delete chat")
            }
        }
        AITextDungeon.database.chatDao().deleteChatById(chatId.toString())
        AITextDungeon.database.messageDao().deleteMessagesForChat(chatId.toString())
        chatId = null
        finish()
    }

    private fun setLastTimePlayedTimeStampToNow() {
        if (chatId != null) {
            val lastPlayedTimeStamp = System.currentTimeMillis()
            val chat = AITextDungeon.database.chatDao().getChatById(chatId.toString())
            chat.lastPlayedTimeStamp = lastPlayedTimeStamp
            AITextDungeon.database.chatDao().updateChat(chat)
        }
    }

    private fun disableTextFieldAndSendButton() {
        val inputField = findViewById<EditText>(R.id.inputMessage)
        val sendButton = findViewById<ImageButton>(R.id.sendButton)

        inputField.isEnabled = false
        sendButton.isEnabled = false
    }

    private fun enableTextFieldAndSendButton() {
        val inputField = findViewById<EditText>(R.id.inputMessage)
        val sendButton = findViewById<ImageButton>(R.id.sendButton)

        inputField.isEnabled = true
        sendButton.isEnabled = true
    }
}