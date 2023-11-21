package zeljko.dejan.rpginventorymanager

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.core.content.ContextCompat

class IconAdapter(private val context: Context, private val icons: Array<Int>) : BaseAdapter() {

    override fun getCount(): Int = icons.size

    override fun getItem(position: Int): Any = icons[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView = convertView as ImageView? ?: ImageView(context)
        imageView.setImageResource(icons[position])
        imageView.background = ContextCompat.getDrawable(context, R.drawable.icon_background)
        imageView.setPadding(8, 8, 8, 8)
        return imageView
    }
}
