package zeljko.dejan.rpginventorymanager

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zeljko.dejan.rpginventorymanager.database.Item

class ItemDetailActivity : AppCompatActivity() {
    private lateinit var itemName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        itemName = intent.getStringExtra("ITEM_NAME") ?: ""
        loadItemDetails()

        val deleteButton = findViewById<FrameLayout>(R.id.deleteButton)
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        val editButton = findViewById<FrameLayout>(R.id.editButton)
        editButton.setOnClickListener {
            val intent = Intent(this, ItemEditActivity::class.java)
            intent.putExtra("ITEM_NAME", itemName)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadItemDetails()
    }

    private fun updateUI(item: Item) {
        val itemNameTextView = findViewById<TextView>(R.id.itemNameTextView)
        val itemIconImageView = findViewById<ImageView>(R.id.itemIconImageView)
        val itemQuantityTextView = findViewById<TextView>(R.id.itemQuantityTextView)
        val itemCategoryTextView = findViewById<TextView>(R.id.itemCategoryTextView)
        val itemPropertiesLayout = findViewById<LinearLayout>(R.id.itemPropertiesLayout)

        itemNameTextView.text = item.name
        itemIconImageView.setImageResource(item.icon)
        itemQuantityTextView.text = item.quantity.toString()
        itemCategoryTextView.text = item.category

        itemPropertiesLayout.removeAllViews()

        item.properties.forEach { (key, value) ->
            val propertyTextView = TextView(this).apply {
                text = "$key: $value"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                }
                gravity = Gravity.CENTER
                textSize = 18f
                setTextColor(ContextCompat.getColor(context, R.color.neutralColor))
            }
            itemPropertiesLayout.addView(propertyTextView)
        }

    }

    private fun showDeleteConfirmationDialog() {
        val message = SpannableString("Do you really want to delete this item?")
        message.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.secondaryTextColor)),
            0, message.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )

        val dialog = AlertDialog.Builder(this)
            .setTitle("Delete Item")
            .setMessage(message)
            .setPositiveButton("Yes") { dialog, which ->
                deleteItem()
            }
            .setNegativeButton("Cancel", null)
            .show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.white))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.white))

        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.secondaryLightColor
                )
            )
        )
    }

    private fun deleteItem() {
        val itemName = intent.getStringExtra("ITEM_NAME")
        itemName?.let {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    AITextDungeon.database.itemDao().deleteItemByName(it)
                }
                finish()
            }
        }
    }

    private fun loadItemDetails() {
        if (itemName.isNotEmpty()) {
            lifecycleScope.launch {
                val item = withContext(Dispatchers.IO) {
                    AITextDungeon.database.itemDao().getItemByName(itemName)
                }
                updateUI(item)
            }
        }
    }
}
