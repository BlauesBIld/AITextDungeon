package zeljko.dejan.rpginventorymanager

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemsAdapter(private val items: MutableList<Item>): RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.nameTextView.text = item.name
        if (item.icon != 0) {
            holder.iconImageView.setImageResource(item.icon)
        } else {
            holder.iconImageView.visibility = View.GONE
        }
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.item_name)
        val iconImageView: ImageView = view.findViewById(R.id.item_icon)
    }

    override fun getItemCount(): Int {
        return items.size;
    }

    fun addItem(item: Item) {
        items.add(item)
        notifyItemInserted(items.size - 1)
        Log.i("ItemsAdapter", "Item added: ${item.name}")
    }
}