package zeljko.dejan.rpginventorymanager

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
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
            when (menuItem.itemId) {
                R.id.navigation_set_name -> showSetNameScreen()
                R.id.navigation_select_icon -> showSelectIconScreen()
                R.id.navigation_set_properties -> showSetPropertiesScreen()
                R.id.navigation_assign_category -> showAssignCategoryScreen()
            }
            true
        }
    }

    fun goToNextStep() {
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
        Log.i("APA", "goToNextStep: $currentStepIndex")
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
        switchToScreen(R.layout.new_item_set_properties)
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

        if(currentItem?.name != "") {
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
                itemNameEditText.error = "Item name needs to be 3-30 characters long and contain only letters, numbers, and spaces."
            }
        }
    }

    private fun setUpSelectIconScreen(view: View) {
        val nextButton = view.findViewById<Button>(R.id.nextButton)
        nextButton.setOnClickListener {
            goToNextStep()
        }
    }

    private fun updateNavigationBarSelection(itemId: Int) {
        binding.navigationBar.setOnItemSelectedListener(null)

        binding.navigationBar.selectedItemId = itemId

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


    private fun isValidItemName(name: String): Boolean {
        val pattern = "^[A-Za-z0-9 ]{3,30}$"
        return name.matches(pattern.toRegex())
    }
}
