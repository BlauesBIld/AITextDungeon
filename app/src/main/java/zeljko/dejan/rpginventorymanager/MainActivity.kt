package zeljko.dejan.rpginventorymanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var itemsRecyclerView: RecyclerView
    private lateinit var adapter: ItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        itemsRecyclerView = findViewById(R.id.itemsRecyclerView)
        val layoutManager = GridLayoutManager(this, 2)
        itemsRecyclerView.layoutManager = layoutManager

        val fabAddItem = findViewById<FloatingActionButton>(R.id.fabAddItem)
        fabAddItem.setOnClickListener {
            startAddNewItemProcess()
        }

        initializeItems()
    }


    fun onSearchClicked(view: View) {}

    fun onFilterClicked(view: View) {}

    fun startAddNewItemProcess(){
        val intent = Intent(this, AddingProcessActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        initializeItems()
    }

    private fun initializeItems() {
        lifecycleScope.launch {
            val itemDao = Inventory.database.itemDao()

            // Check and Insert items only if needed
            if (itemDao.getAllItems().isEmpty()) {
                itemDao.insertItem(Item("Example Item1", R.drawable.item_ic_pouch))
                itemDao.insertItem(Item("Example Item2", R.drawable.item_ic_pouch))
            }

            val items = itemDao.getAllItems()
            adapter = ItemsAdapter(items.toMutableList()) { item ->
                val intent = Intent(this@MainActivity, ItemDetailActivity::class.java)
                intent.putExtra("ITEM_NAME", item.name)
                startActivity(intent)
            }

            itemsRecyclerView.adapter = adapter
        }
    }
}
