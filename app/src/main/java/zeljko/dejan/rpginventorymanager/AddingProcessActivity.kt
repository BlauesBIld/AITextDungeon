package zeljko.dejan.rpginventorymanager

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import zeljko.dejan.rpginventorymanager.databinding.ActivityAddingProcessBinding

class AddingProcessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddingProcessBinding
    private var currentStepIndex = 0
    private var currentItem: Item? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddingProcessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigationBar()
        showSetNameScreen()

        disableNavigationItems(1)

        currentItem = Item("", 0)
    }

    private fun disableNavigationItems(startingFrom: Int) {
        val menu = binding.navigationBar.menu
        for (i in startingFrom until menu.size()) {
            menu.getItem(i).isEnabled = false
        }
    }

    private fun setupNavigationBar() {
        binding.navigationBar.setOnItemSelectedListener { menuItem ->
            saveCurrentStep()
            when (menuItem.itemId) {
                R.id.navigation_set_name -> showSetNameScreen()
                R.id.navigation_select_icon -> showSelectIconScreen()
                R.id.navigation_set_properties -> showSetPropertiesScreen()
                R.id.navigation_assign_category -> showAssignCategoryScreen()
            }
            true
        }
    }

    private fun saveCurrentStep() {
        when (currentStepIndex) {
            0 -> saveName()
            1 -> saveIcon()
            2 -> saveProperties()
        }
    }

    private fun goToNextStep() {
        val menu = binding.navigationBar.menu
        if (currentStepIndex < menu.size()) {
            val nextItem = menu.getItem(currentStepIndex + 1)
            nextItem.isEnabled = true
            nextItem.isVisible = true

            when (nextItem.itemId) {
                R.id.navigation_select_icon -> showSelectIconScreen()
                R.id.navigation_set_properties -> showSetPropertiesScreen()
                R.id.navigation_assign_category -> showAssignCategoryScreen()
            }
        }
    }

    private fun showSetNameScreen() {
        if (R.id.navigation_set_name != binding.navigationBar.selectedItemId) {
            updateNavigationBarSelection(R.id.navigation_set_name)
        }
        val itemNameView = switchToScreen(R.layout.new_item_set_name)
        setUpSetNameScreen(itemNameView)
        currentStepIndex = 0
    }

    private fun showSelectIconScreen() {
        if (R.id.navigation_select_icon != binding.navigationBar.selectedItemId) {
            updateNavigationBarSelection(R.id.navigation_select_icon)
        }
        val iconView = switchToScreen(R.layout.new_item_select_icon)
        setUpSelectIconScreen(iconView)
        currentStepIndex = 1
    }

    private fun showSetPropertiesScreen() {
        if (R.id.navigation_set_properties != binding.navigationBar.selectedItemId) {
            updateNavigationBarSelection(R.id.navigation_set_properties)
        }
        val propertiesView = switchToScreen(R.layout.new_item_set_properties)
        setUpPropertiesScreen(propertiesView)
        currentStepIndex = 2
    }

    private fun showAssignCategoryScreen() {
        if (R.id.navigation_assign_category != binding.navigationBar.selectedItemId) {
            updateNavigationBarSelection(R.id.navigation_assign_category)
        }
        val categoriesView = switchToScreen(R.layout.new_item_assign_category)
        setUpAssignCategoryScreen(categoriesView)
        currentStepIndex = 3
    }

    private fun switchToScreen(newScreen: Int): android.view.View {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(newScreen, binding.contentFrame, false)
        binding.contentFrame.removeAllViews()
        binding.contentFrame.addView(view)
        Log.i("APA", "switchToScreen: $currentStepIndex")
        return view
    }

    private fun setUpSetNameScreen(view: View) {
        val itemNameEditText = view.findViewById<EditText>(R.id.itemNameEditText)
        val nextButton = view.findViewById<Button>(R.id.nextButton)

        nextButton.isEnabled = false

        if (currentItem?.name != "") {
            itemNameEditText.setText(currentItem?.name)
        }

        itemNameEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                v.clearFocus()
                true
            } else {
                false
            }
        }

        itemNameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val nextButton = view.findViewById<Button>(R.id.nextButton)
                nextButton.isEnabled = isValidItemName(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        nextButton.setOnClickListener {
            if (isValidItemName(itemNameEditText.text.toString())) {
                currentItem?.name = itemNameEditText.text.toString()
                goToNextStep()
            } else {
                itemNameEditText.error =
                    "Item name needs to be 3-30 characters long and contain only letters, numbers, and spaces."
            }
        }
    }

    private fun setUpSelectIconScreen(view: View) {
        val gridView = view.findViewById<GridView>(R.id.iconGridView)
        val selectedIconImageView = view.findViewById<ImageView>(R.id.selectedIconImageView)

        val nextButton = view.findViewById<Button>(R.id.nextButton)
        nextButton.isEnabled = false

        gridView.adapter = IconAdapter(this, Item.icons)

        if (currentItem?.icon != 0) {
            gridView.setSelection(Item.icons.indexOf(currentItem?.icon))
            selectedIconImageView.setImageResource(currentItem?.icon!!)
            nextButton.isEnabled = true
        } else {
            gridView.setSelection(0)
        }

        gridView.setOnItemClickListener { parent, view, position, id ->
            val selectedIcon = Item.icons[position]
            currentItem?.icon = selectedIcon
            selectedIconImageView.setImageResource(selectedIcon)
            nextButton.isEnabled = true
        }

        nextButton.setOnClickListener {
            goToNextStep()
        }
    }

    private fun setUpPropertiesScreen(view: View) {
        val propertiesLayout: LinearLayout = view.findViewById(R.id.propertiesLayout)

        if (currentItem?.properties?.size == 0) {
            addPropertyRow()
        }

        currentItem?.properties?.forEach { (name, value) ->
            addPropertyRow(name, value)
        }

        val addPropertyButton: ImageButton = view.findViewById(R.id.addPropertyButton)
        addPropertyButton.setOnClickListener {
            addPropertyRow()
        }

        val nextButton = view.findViewById<Button>(R.id.nextButton)
        nextButton.setOnClickListener {
            saveProperties()
            goToNextStep()
        }
    }

    private fun setUpAssignCategoryScreen(view: View) {
        val categorySpinner: Spinner = view.findViewById(R.id.categorySpinner)
        val newCategoryEditText: EditText = view.findViewById(R.id.newCategoryEditText)
        val nextButton: Button = view.findViewById(R.id.nextButton)

        val categoriesList = mutableListOf<String>()

        categoriesList.add("New Category")
        categoriesList.addAll(Item.defaultCategories)
        Inventory.database.itemDao().getAllCategories().forEach { category ->
            if (category !in categoriesList && category != "")
                categoriesList.add(category)
        }

        val categoriesAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesList)
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoriesAdapter

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedCategory = parent?.getItemAtPosition(position).toString()
                if (selectedCategory == "New Category") {
                    newCategoryEditText.visibility = View.VISIBLE
                    nextButton.isEnabled = false
                } else {
                    newCategoryEditText.visibility = View.GONE
                    currentItem?.category = selectedCategory
                    nextButton.isEnabled = true
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                newCategoryEditText.visibility = View.GONE
            }
        }

        newCategoryEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                nextButton.isEnabled = isValidCategoryName(s.toString().trim())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        nextButton.setOnClickListener {
            if (categorySpinner.selectedItem.toString() == "New Category") {
                val newCategoryName = newCategoryEditText.text.toString().trim()
                if (isValidCategoryName(newCategoryName)) {
                    Item.allCategories.add(newCategoryName)
                    categoriesAdapter.notifyDataSetChanged()
                    currentItem?.category = newCategoryName
                    saveItemAndFinishActivity()
                } else {
                    newCategoryEditText.error =
                        "Category name must be 3-30 characters long and should be one word."
                }
            } else {
                currentItem?.category = categorySpinner.selectedItem.toString()
                saveItemAndFinishActivity()
            }
        }
    }

    private fun isValidCategoryName(name: String): Boolean {
        val pattern = "^[A-Za-z0-9]{3,30}$"
        return name.matches(pattern.toRegex())
    }

    private fun isValidItemName(name: String): Boolean {
        val pattern = "^[A-Za-z0-9 ]{3,30}$"
        return name.matches(pattern.toRegex())
    }

    private fun updateNavigationBarSelection(itemId: Int) {
        binding.navigationBar.setOnItemSelectedListener(null)

        binding.navigationBar.selectedItemId = itemId

        setupNavigationBar()
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

    private fun saveProperties() {
        val propertiesLayout: LinearLayout = findViewById(R.id.propertiesLayout)
        currentItem?.properties?.clear()

        for (i in 0 until propertiesLayout.childCount) {
            val rowView = propertiesLayout.getChildAt(i) as? LinearLayout
            val propertyNameEditText = rowView?.findViewById<EditText>(R.id.propertyName)
            val propertyValueEditText = rowView?.findViewById<EditText>(R.id.propertyValue)

            val propertyName = propertyNameEditText?.text?.toString()?.trim()
            val propertyValue = propertyValueEditText?.text?.toString()?.trim()

            if (!propertyName.isNullOrEmpty() && !propertyValue.isNullOrEmpty()) {
                currentItem?.properties?.put(propertyName, propertyValue)
            }
        }
    }

    private fun saveName() {
        val itemNameEditText: EditText? = findViewById(R.id.itemNameEditText)
        val itemName = itemNameEditText?.text.toString().trim()
        if (isValidItemName(itemName)) {
            currentItem?.name = itemName
        }
        Log.i("Debug", "saveName: ${itemName}")
    }

    private fun saveIcon() {
        val currentSelectedIcon = currentItem?.icon ?: 0
        if (currentSelectedIcon != 0) {
            currentItem?.icon = currentSelectedIcon
        }
    }

    private fun saveItemAndFinishActivity() {
        currentItem?.let { item ->
            item.quantity = 1
            lifecycleScope.launch {
                Inventory.database.itemDao().insertItem(item)
                finish()
            }
        }
    }
}
