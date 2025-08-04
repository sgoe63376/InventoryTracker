package com.example.inventorytracker

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.SeekBar

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
        adapter = InventoryAdapter(
            inventoryItems,
            onInventoryChanged = { updateEmptyState() },
            onDelete = { item ->
                adapter.removeItem(item)
                updateEmptyState()
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupFab() {
        fabAddItem.setOnClickListener {
            showAddItemDialog()
        }
    }

    private fun showAddItemDialog() {
        val context = this
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 10)
        }
        val editText = EditText(context).apply {
            hint = "Enter item name"
        }
        val colorSeekBar = SeekBar(context).apply {
            max = 0xFFFFFF
        }
        val colorPreview = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(100, 50)
            setBackgroundColor(Color.WHITE)
        }
        colorSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                colorPreview.setBackgroundColor(Color.rgb(progress shr 16, (progress shr 8) and 0xFF, progress and 0xFF))
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        layout.addView(editText)
        layout.addView(colorSeekBar)
        layout.addView(colorPreview)

        AlertDialog.Builder(this)
            .setTitle("Add New Item")
            .setView(layout)
            .setPositiveButton("Add") { _, _ ->
                val itemName = editText.text.toString().trim()
                val color = Color.rgb(
                    colorSeekBar.progress shr 16,
                    (colorSeekBar.progress shr 8) and 0xFF,
                    colorSeekBar.progress and 0xFF
                )
                if (itemName.isNotEmpty()) {
                    val newItem = InventoryItem(name = itemName, color = color)
                    val added = adapter.addItem(newItem)
                    if (!added) {
                        AlertDialog.Builder(this)
                            .setMessage("Duplicate item: \"$itemName\" already exists.")
                            .setPositiveButton("OK", null)
                            .show()
                    }
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