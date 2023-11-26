package zeljko.dejan.rpginventorymanager

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.marginEnd
import zeljko.dejan.rpginventorymanager.databinding.ActivityAddingProcessBinding

class AddingProcessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddingProcessBinding
    private var currentStepIndex = 0
    private var currentItem: Item? = null

    private val icons = arrayOf(
        R.drawable.item_ic_bow,
        R.drawable.item_ic_bread,
        R.drawable.item_ic_cape,
        R.drawable.item_ic_key,
        R.drawable.item_ic_potion,
        R.drawable.item_ic_necklace,
        R.drawable.item_ic_pouch,
        R.drawable.item_ic_robe,
        R.drawable.item_ic_staff,
        R.drawable.item_ic_sword,
        R.drawable.item_ic_scroll,
        R.drawable.item_ic_armor
    )

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
        switchToScreen(R.layout.new_item_assign_category)
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

        val nextButton = view.findViewById<Button>(R.id.nextButton)
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

        gridView.adapter = IconAdapter(this, icons)

        if (currentItem?.icon != 0) {
            gridView.setSelection(icons.indexOf(currentItem?.icon))
            selectedIconImageView.setImageResource(currentItem?.icon!!)
            nextButton.isEnabled = true
        } else {
            gridView.setSelection(0)
        }

        gridView.setOnItemClickListener { parent, view, position, id ->
            val selectedIcon = icons[position]
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

        if(currentItem?.properties?.size == 0) {
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

    private fun updateNavigationBarSelection(itemId: Int) {
        binding.navigationBar.setOnItemSelectedListener(null)

        binding.navigationBar.selectedItemId = itemId

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

    private fun isValidItemName(name: String): Boolean {
        val pattern = "^[A-Za-z0-9 ]{3,30}$"
        return name.matches(pattern.toRegex())
    }

    private fun addPropertyRow(name: String = "", value: String = "") {
        val propertiesLayout: LinearLayout = findViewById(R.id.propertiesLayout)

        val rowView: View = LayoutInflater.from(this).inflate(R.layout.property_row_layout, propertiesLayout, false)
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
}
