package com.example.inventorytracker

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InventoryAdapter
    private lateinit var fabAddItem: FloatingActionButton
    private lateinit var emptyStateText: TextView
    private val inventoryItems = mutableListOf<InventoryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViews()
        setupRecyclerView()
        setupFab()
        updateEmptyState()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.rv_inventory)
        fabAddItem = findViewById(R.id.fab_add_item)
        emptyStateText = findViewById(R.id.tv_empty_state)
    }

    private fun setupRecyclerView() {
        adapter = InventoryAdapter(inventoryItems) { item ->
            // Callback when quantity changes - you could save to database here
            updateEmptyState()
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupFab() {
        fabAddItem.setOnClickListener {
            showAddItemDialog()
        }
    }

    private fun showAddItemDialog() {
        val editText = EditText(this).apply {
            hint = "Enter item name"
            setPadding(50, 30, 50, 30)
        }

        AlertDialog.Builder(this)
            .setTitle("Add New Item")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val itemName = editText.text.toString().trim()
                if (itemName.isNotEmpty()) {
                    val newItem = InventoryItem(name = itemName)
                    adapter.addItem(newItem)
                    updateEmptyState()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateEmptyState() {
        if (inventoryItems.isEmpty()) {
            emptyStateText.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyStateText.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}