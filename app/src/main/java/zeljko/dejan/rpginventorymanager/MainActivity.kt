package zeljko.dejan.rpginventorymanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var itemsRecyclerView: RecyclerView
    private lateinit var adapter: ItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        itemsRecyclerView = findViewById(R.id.itemsRecyclerView)

        ItemRepository.addItem(Item("ExampleItem1", R.drawable.item_ic_pouch))
        ItemRepository.addItem(Item("ExampleItem2", R.drawable.item_ic_pouch))
        Log.d("Item", ItemRepository.getItems().size.toString())
        adapter = ItemsAdapter(ItemRepository.getItems())
        itemsRecyclerView.adapter = adapter

        val layoutManager = GridLayoutManager(this, 2)
        itemsRecyclerView.layoutManager = layoutManager

        val fabAddItem = findViewById<FloatingActionButton>(R.id.fabAddItem)
        fabAddItem.setOnClickListener{
            startAddNewItemProcess()
        }
    }

    fun onSearchClicked(view: View) {}

    fun onFilterClicked(view: View) {}

    fun startAddNewItemProcess(){
        val intent = Intent(this, AddingProcessActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        adapter.updateItems(ItemRepository.getItems())
    }
}
