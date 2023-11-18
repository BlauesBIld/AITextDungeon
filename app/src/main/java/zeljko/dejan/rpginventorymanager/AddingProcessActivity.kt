package zeljko.dejan.rpginventorymanager

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import zeljko.dejan.rpginventorymanager.databinding.ActivityAddingProcessBinding

class AddingProcessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddingProcessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddingProcessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigationBar()
        showSetNameScreen() // Initialize with the first screen
    }

    private fun setupNavigationBar() {
        binding.navigationBar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_name -> showSetNameScreen()
                R.id.navigation_icon -> showSelectIconScreen()
                R.id.navigation_properties -> showSetPropertiesScreen()
                R.id.navigation_category -> showAssignCategoryScreen()
            }
            true
        }
    }

    private fun showSetNameScreen() {
        switchToScreen(R.layout.new_item_set_name)
    }

    private fun showSelectIconScreen() {
        switchToScreen(R.layout.new_item_select_icon)
    }
    private fun showSetPropertiesScreen() {
        switchToScreen(R.layout.new_item_set_properties)
    }

    private fun showAssignCategoryScreen() {
        switchToScreen(R.layout.new_item_assign_category)
    }

    private fun switchToScreen(newScreen: Int) {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(newScreen, binding.contentFrame, false)
        binding.contentFrame.removeAllViews()
        binding.contentFrame.addView(view)
    }
}
