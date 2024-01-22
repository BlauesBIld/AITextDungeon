package zeljko.dejan.rpginventorymanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import zeljko.dejan.rpginventorymanager.database.Chat
import java.text.SimpleDateFormat
import java.util.Locale

class ChatCardAdapter(
    private val chatCards: MutableList<Chat>,
    private val onItemClick: (Chat) -> Unit
) : RecyclerView.Adapter<ChatCardAdapter.ChatCardViewHolder>() {

    inner class ChatCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.cardImage)
        val titleView: TextView = view.findViewById(R.id.cardTitle)
        val createdOnView: TextView = view.findViewById(R.id.cardCreatedOn)
        val lastPlayedView: TextView = view.findViewById(R.id.cardLastPlayed)

        init {
            itemView.setOnClickListener {
                onItemClick(chatCards[bindingAdapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatCardViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.chat_card_item, parent, false)
        return ChatCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatCardViewHolder, position: Int) {
        val item = chatCards[position]
        holder.titleView.text = item.title
        holder.createdOnView.text = "Created on: " + formatDate(item.creationTimeStamp)
        holder.lastPlayedView.text = "Last played: " + formatDate(item.lastPlayedTimeStamp)
    }

    private fun formatDate(timestamp: Long): String {
        val formatter = SimpleDateFormat("d.M.yyyy", Locale.getDefault())
        return formatter.format(timestamp)
    }

    override fun getItemCount(): Int = chatCards.size

    fun addChatCard(item: Chat) {
        chatCards.add(item)
        notifyItemInserted(chatCards.size - 1)
    }
}
