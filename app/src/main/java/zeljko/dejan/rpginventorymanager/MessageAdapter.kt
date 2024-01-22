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

    private val funnyLoadingMessages = listOf(
        "Pondering",
        "Cooking",
        "Brewing",
        "Conjuring",
        "Summoning",
        "Decoding",
        "Unraveling",
        "Mystifying",
        "Riddling",
        "Enchanting",
        "Twisting",
        "Spinning",
        "Juggling",
        "Whisking",
        "Sculpting",
        "Crafting",
        "Formulating",
        "Plotting",
        "Animating",
        "Transforming",
        "Baffling",
        "Jinxing",
        "Blinking",
        "Fiddling",
        "Scribbling",
        "Doodling",
        "Sketching",
        "Painting",
        "Composing",
        "Humming",
        "Whistling",
        "Chanting",
        "Murmuring",
        "Muttering",
        "Babbling",
        "Giggling",
        "Snickering",
        "Chuckling",
        "Puzzling",
        "Tweaking",
        "Zapping",
        "Fizzing",
        "Warping",
        "Bending",
        "Shaping",
        "Flipping",
        "Twirling",
        "Swirling",
        "Spiraling",
        "Waving",
        "Tapping"
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
