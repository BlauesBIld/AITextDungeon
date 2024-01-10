package zeljko.dejan.rpginventorymanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemEditActivity : AppCompatActivity() {

    private lateinit var itemNameTextView: TextView
    private lateinit var quantityTextView: TextView
    private lateinit var categorySpinner: Spinner
    private lateinit var propertiesLayout: LinearLayout
    private var currentItem: Item? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)

        itemNameTextView = findViewById(R.id.itemNameTextView)
        quantityTextView = findViewById(R.id.quantityTextView)
        categorySpinner = findViewById(R.id.categorySpinner)
        propertiesLayout = findViewById(R.id.propertiesLayout)

        val itemName = intent.getStringExtra("ITEM_NAME")
        if (itemName != null) {
            fetchItemDetails(itemName)
        } else {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<ImageView>(R.id.minusButton).setOnClickListener { updateQuantity(false) }
        findViewById<ImageView>(R.id.plusButton).setOnClickListener { updateQuantity(true) }
        findViewById<FrameLayout>(R.id.confirmButton).setOnClickListener { confirmEdit() }
        findViewById<FrameLayout>(R.id.cancelButton).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.addPropertyButton).setOnClickListener { addPropertyRow() }
    }

    private fun fetchItemDetails(itemName: String) {
        lifecycleScope.launch {
            currentItem = withContext(Dispatchers.IO) {
                Inventory.database.itemDao().getItemByName(itemName)
            }

            currentItem?.let { item ->
                itemNameTextView.text = item.name
                quantityTextView.text = item.quantity.toString()
                setUpCategorySpinner(item.category)
                setUpPropertiesLayout(item.properties)
            }
        }
    }

    private fun setUpCategorySpinner(selectedCategory: String) {
        val categories = mutableListOf<String>()
        lifecycleScope.launch {
            categories.addAll(Item.defaultCategories)
            Inventory.database.itemDao().getAllCategories().forEach { category ->
                if (category !in categories && category != "")
                    categories.add(category)
            }
            val adapter = ArrayAdapter(
                this@ItemEditActivity,
                android.R.layout.simple_spinner_item,
                categories
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
            categorySpinner.setSelection(categories.indexOf(selectedCategory))
        }
    }

    private fun setUpPropertiesLayout(properties: Map<String, String>) {
        properties.forEach { (name, value) ->
            addPropertyRow(name, value)
        }
    }

    private fun addPropertyRow(name: String = "", value: String = "") {
        val propertiesLayout: LinearLayout = findViewById(R.id.propertiesLayout)

        val rowView: View =
            LayoutInflater.from(this).inflate(R.layout.property_row_layout, propertiesLayout, false)
        val propertyNameEditText: EditText = rowView.findViewById(R.id.propertyName)
        val propertyValueEditText: EditText = rowView.findViewById(R.id.propertyValue)

        propertyNameEditText.setText(name)
        propertyValueEditText.setText(value)

        val removeButton: ImageButton = rowView.findViewById(R.id.removePropertyButton)
        removeButton.setOnClickListener {
            propertiesLayout.removeView(rowView)
        }

        propertiesLayout.addView(rowView)
    }

    private fun updateQuantity(isIncrement: Boolean) {
        var quantity = quantityTextView.text.toString().toIntOrNull() ?: 0
        quantity = if (isIncrement) quantity + 1 else maxOf(quantity - 1, 1)
        quantityTextView.text = quantity.toString()
    }

    private fun confirmEdit() {
        if (validateInputs()) {
            saveItem()
        } else {
            AlertDialog.Builder(this)
                .setMessage("Invalid input. Please check your entries.")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun validateInputs(): Boolean {
        return true
    }

    private fun saveItem() {
        val updatedItem = currentItem?.copy()
        updatedItem?.quantity = quantityTextView.text.toString().toInt()
        updatedItem?.category = categorySpinner.selectedItem.toString()
        updatedItem?.properties = mutableMapOf<String, String>().apply {
            for (i in 0 until propertiesLayout.childCount) {
                val rowView = propertiesLayout.getChildAt(i) as? LinearLayout
                val propertyNameEditText = rowView?.findViewById<EditText>(R.id.propertyName)
                val propertyValueEditText = rowView?.findViewById<EditText>(R.id.propertyValue)

                val propertyName = propertyNameEditText?.text.toString().trim()
                val propertyValue = propertyValueEditText?.text.toString().trim()
                if (propertyName.isNotBlank() && propertyValue.isNotBlank()) {
                    put(propertyName, propertyValue)
                }
            }
        }

        updatedItem?.let { item ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    Inventory.database.itemDao().updateItem(item)
                }
                finish()
            }
        } ?: run {
            Toast.makeText(this, "Error updating item", Toast.LENGTH_SHORT).show()
        }
    }
}
