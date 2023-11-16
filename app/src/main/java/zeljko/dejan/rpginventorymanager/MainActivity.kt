package zeljko.dejan.rpginventorymanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var itemsRecyclerView: RecyclerView
    private lateinit var adapter: ItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        itemsRecyclerView = findViewById(R.id.itemsRecyclerView)
        adapter = ItemsAdapter(mutableListOf(Item("ExampleItem", R.drawable.ic_pouch), Item("ExampleItem2", R.drawable.ic_pouch)))
        itemsRecyclerView.adapter = adapter

        val layoutManager = GridLayoutManager(this, 2)
        itemsRecyclerView.layoutManager = layoutManager

        val fabAddItem = findViewById<FloatingActionButton>(R.id.fabAddItem)
        fabAddItem.setOnClickListener{
            val intent = Intent(this, AddingProcessActivity::class.java)
            startActivity(intent)
        }
    }

    fun onSearchClicked(view: View) {}

    fun onFilterClicked(view: View) {}

    fun startAddNewItemProcess(){
        Log.i("MainActivity", "Add new item process started")
        val exampleItem = Item("ExampleItem", R.drawable.ic_pouch)
        adapter.addItem(exampleItem)
    }
}
