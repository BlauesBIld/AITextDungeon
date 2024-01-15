package zeljko.dejan.rpginventorymanager

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InventoryActivity : AppCompatActivity() {
    private lateinit var itemsRecyclerView: RecyclerView
    private lateinit var adapter: ItemsAdapter

    private lateinit var searchBarLayout: LinearLayout
    private lateinit var bottomBar: LinearLayout
    private lateinit var searchEditText: EditText

    private lateinit var sortSpinner: Spinner
    private lateinit var categoryFilterSpinner: Spinner
    private var currentSortOption: String = "Default"
    private var currentCategory: String = "All Categories"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        itemsRecyclerView = findViewById(R.id.itemsRecyclerView)
        val layoutManager = GridLayoutManager(this, 2)
        itemsRecyclerView.layoutManager = layoutManager

        val fabAddItem = findViewById<FloatingActionButton>(R.id.fabAddItem)
        fabAddItem.setOnClickListener {
            startAddNewItemProcess()
        }

        initializeItems()

        searchBarLayout = findViewById(R.id.searchBarLayout)
        bottomBar = findViewById(R.id.bottomBar)
        searchEditText = findViewById(R.id.searchEditText)

        findViewById<FrameLayout>(R.id.searchButton).setOnClickListener {
            toggleSearchBarVisibility(true)
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (it.isNotEmpty()) {
                        performSearch(it.toString())
                    } else {
                        initializeItems()
                    }
                }
            }
        })

        findViewById<FrameLayout>(R.id.filterButton).setOnClickListener {
            showFilterBottomSheet()
        }
    }

    private fun showFilterBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_filter, null)
        bottomSheetDialog.setContentView(sheetView)

        sortSpinner = sheetView.findViewById(R.id.sortSpinner)
        categoryFilterSpinner = sheetView.findViewById(R.id.categoryFilterSpinner)

        val sortOptions = arrayOf("A -> Z", "Z -> A", "Default")
        sortSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions)

        populateCategorySpinner()

        val sortAdapter = sortSpinner.adapter as ArrayAdapter<String>
        sortSpinner.setSelection(sortAdapter.getPosition(currentSortOption))

        val categoryAdapter = categoryFilterSpinner.adapter as ArrayAdapter<String>
        categoryFilterSpinner.setSelection(categoryAdapter.getPosition(currentCategory))


        bottomSheetDialog.show()

        val resetFilterButton = sheetView.findViewById<Button>(R.id.resetFilterButton)
        val confirmFilterButton = sheetView.findViewById<Button>(R.id.confirmFilterButton)

        resetFilterButton.setOnClickListener {
            resetFilters()
            bottomSheetDialog.dismiss()
        }

        confirmFilterButton.setOnClickListener {
            applyFilters()
            bottomSheetDialog.dismiss()
        }
    }

    private fun populateCategorySpinner() {
        lifecycleScope.launch {
            val categories = mutableListOf("All Categories")
            categories.addAll(AITextDungeon.database.itemDao().getAllCategories())
            categoryFilterSpinner.adapter =
                ArrayAdapter(
                    this@InventoryActivity,
                    android.R.layout.simple_spinner_item,
                    categories
                )
        }
    }

    private fun toggleSearchBarVisibility(showSearch: Boolean) {
        if (showSearch) {
            searchBarLayout.visibility = View.VISIBLE
            bottomBar.visibility = View.GONE
        } else {
            searchEditText.text.clear()
            searchBarLayout.visibility = View.GONE
            bottomBar.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        if (searchBarLayout.visibility == View.VISIBLE) {
            toggleSearchBarVisibility(false)
        } else {
            super.onBackPressed()
        }
    }

    private fun performSearch(query: String) {
        lifecycleScope.launch {
            val filteredItems = withContext(Dispatchers.IO) {
                AITextDungeon.database.itemDao().searchItemsThatContainQuery("%$query%")
            }
            adapter.updateItems(filteredItems.toMutableList())
        }
    }

    fun startAddNewItemProcess() {
        val intent = Intent(this, AddingProcessActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        initializeItems()
        toggleSearchBarVisibility(false)
        resetFilters()
    }

    private fun initializeItems() {
        lifecycleScope.launch {
            val itemDao = AITextDungeon.database.itemDao()

            val items = itemDao.getAllItems()
            adapter = ItemsAdapter(items.toMutableList()) { item ->
                val intent = Intent(this@InventoryActivity, ItemDetailActivity::class.java)
                intent.putExtra("ITEM_NAME", item.name)
                startActivity(intent)
            }

            itemsRecyclerView.adapter = adapter
        }
    }

    private fun applyFilters() {
        currentSortOption = sortSpinner.selectedItem as String
        currentCategory = categoryFilterSpinner.selectedItem as String
        applySortAndFilter(currentSortOption, currentCategory)
    }

    private fun resetFilters() {
        currentSortOption = "Default"
        currentCategory = "All Categories"
        loadDefaultItems()
    }

    private fun loadDefaultItems() {
        lifecycleScope.launch {
            val items = withContext(Dispatchers.IO) {
                AITextDungeon.database.itemDao().getAllItems()
            }
            adapter.updateItems(items.toMutableList())
        }
    }

    private fun applySortAndFilter(sortOption: String, category: String) {
        lifecycleScope.launch {
            val sortedAndFilteredItems = withContext(Dispatchers.IO) {
                val sortedItems = when (sortOption) {
                    "A -> Z" -> AITextDungeon.database.itemDao().getItemsSortedByNameAsc()
                    "Z -> A" -> AITextDungeon.database.itemDao().getItemsSortedByNameAsc()
                        .reversed()

                    else -> AITextDungeon.database.itemDao().getAllItems()
                }

                if (category != "All Categories") {
                    sortedItems.filter { it.category == category }
                } else {
                    sortedItems
                }
            }

            adapter.updateItems(sortedAndFilteredItems.toMutableList())
        }
    }

}
