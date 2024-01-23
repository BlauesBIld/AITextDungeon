package zeljko.dejan.rpginventorymanager

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import zeljko.dejan.rpginventorymanager.database.Message

class MessageAdapter(private val messages: MutableList<Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private var aiMessageIndex = 5

    private val funnyLoadingMessages = arrayOf(
        "Calling the police",
        "Asking David Kupert",
        "Cooking",
        "Let me cook",
        "Bing chilling",
        "Generating Cities",
        "Giving NPCs brains",
        "Grinding gears",
        "Unraveling the fabric of the matrix",
        "Looking for who asked",
        "Calculating",
        "Gathering bytes and bits",
        "Conversing with virtual sages",
        "Navigating the data labyrinth",
        "Consulting the AI elders",
        "Deciphering cryptic codes",
        "Activating the word forge",
        "Mining diamonds",
        "Weaving tales",
        "Harvesting crops",
        "Stealing ideas from pokemon",
        "Piecing together digital narratives",
        "Constructing story scaffolds",
        "Synthesizing narrative elements",
        "Generating whimsical wonders",
        "Fishing fish",
        "Assembling story strands",
        "Sculpting story",
        "Thinking of a funny loading message",
        "Delving into digital depths",
        "Eliciting electronic epics",
        "Fishing for phrases",
        "Grinding the gears of imagination",
        "Composing songs",
        "Igniting idea engines",
        "Juggling deeze nuts",
        "Knitting a sweater",
        "Building houses",
        "Molding the mythos",
        "Digging tunnels",
        "Chewing bubblegum",
        "Picking flowers",
        "Getting platinum trophies",
        "Raging",
        "Creating personalities",
        "Sculpting characters",
        "Locking chests",
        "Avoiding eye contact",
        "Looking for a job",
        "X-raying chests",
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.chat_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)

        if (message.author == ChatActivity.ChatConstants.AI_NAME && message.text.isEmpty()) {
            if (position == aiMessageIndex) {
                holder.startGeneratingMessageAnimation()
            } else {
                holder.startGeneratingMessageAnimation(funnyLoadingMessages.random())
            }
            aiMessageIndex++
        } else {
            holder.stopGeneratingMessageAnimation()
        }
    }

    override fun getItemCount() = messages.size

    class MessageViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val authorTextView: TextView = view.findViewById(R.id.textAuthor)
        private val messageTextView: TextView = view.findViewById(R.id.textMessage)
        private val previewMessageTextView: TextView = view.findViewById(R.id.textPreviewMessage)

        private var dotCount = 0
        private val updateHandler = Handler(Looper.getMainLooper())
        private var updateRunnable: Runnable? = null

        fun bind(message: Message) {
            if (message.author == ChatActivity.ChatConstants.AI_NAME) {
                if (message.text == "") {
                    messageTextView.visibility = View.GONE
                    previewMessageTextView.visibility = View.VISIBLE
                } else {
                    messageTextView.visibility = View.VISIBLE
                    previewMessageTextView.visibility = View.GONE
                }
                authorTextView.setTextColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.primaryColor
                    )
                )
                authorTextView.text = ChatActivity.ChatConstants.AI_NAME
            } else {
                authorTextView.setTextColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.white
                    )
                )
                authorTextView.text = ChatActivity.ChatConstants.PLAYER_NAME
            }
            messageTextView.text = message.text
        }

        fun startGeneratingMessageAnimation(baseText: String = "Generating") {
            stopGeneratingMessageAnimation()

            updateRunnable = Runnable {
                previewMessageTextView.text = baseText + ".".repeat(dotCount)

                dotCount = (dotCount + 1) % 4
                updateHandler.postDelayed(updateRunnable!!, 800)
            }
            updateHandler.post(updateRunnable!!)
        }

        fun stopGeneratingMessageAnimation() {
            if (updateRunnable != null) {
                updateHandler.removeCallbacks(updateRunnable!!)
                updateRunnable = null
            }
        }
    }

    fun addMessage(newMessage: Message) {
        messages.add(newMessage)

        notifyItemInserted(messages.size - 1)
    }

    fun updateLastAIMessage(newMessage: Message) {
        if (messages.get(messages.size - 1).author != ChatActivity.ChatConstants.AI_NAME) {
            throw Exception("Last message is not AI message")
        }
        messages[messages.size - 1] = newMessage

        notifyItemChanged(messages.size - 1)
    }
}
