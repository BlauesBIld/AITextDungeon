package zeljko.dejan.rpginventorymanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import zeljko.dejan.rpginventorymanager.database.Message

class MessageAdapter(private val messages: MutableList<Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.chat_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount() = messages.size

    class MessageViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val authorTextView: TextView = view.findViewById(R.id.textAuthor)
        private val messageTextView: TextView = view.findViewById(R.id.textMessage)

        fun bind(message: Message) {
            if (message.author == ChatActivity.ChatConstants.AI_NAME) {
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
    }

    fun addMessage(newMessage: Message) {
        messages.add(newMessage)

        notifyItemInserted(messages.size - 1)
    }
}
