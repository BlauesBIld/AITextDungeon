package zeljko.dejan.rpginventorymanager

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class ItemsAdapter(
    private val items: MutableList<Item>,
    private val onItemClick: (Item) -> Unit
) : RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>() {
    class ItemDiffCallback(private val oldList: List<Item>, private val newList: List<Item>) :
        DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].name == newList[newItemPosition].name
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }


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

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.item_name)
        val iconImageView: ImageView = view.findViewById(R.id.item_icon)

        init {
            itemView.setOnClickListener {
                onItemClick(items[bindingAdapterPosition])
            }
        }
    }


    override fun getItemCount(): Int {
        return items.size;
    }

    fun addItem(item: Item) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun updateItems(newItems: MutableList<Item>) {
        val diffCallback = ItemDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items.clear()
        items.addAll(newItems)
        for (item in items) {
            Log.d("Item", item.name)
        }
        diffResult.dispatchUpdatesTo(this)
    }
}