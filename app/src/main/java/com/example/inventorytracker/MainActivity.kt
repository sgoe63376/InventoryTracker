package com.example.inventorytracker

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var fabAddItem: FloatingActionButton
    private lateinit var fabAddSheet: FloatingActionButton
    private lateinit var pagerAdapter: SheetPagerAdapter

    val sheets = mutableListOf<InventorySheet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)
        fabAddItem = findViewById(R.id.fab_add_item)
        fabAddSheet = findViewById(R.id.fab_add_sheet)

        loadSheets()
        if (sheets.isEmpty()) {
            sheets.add(InventorySheet("Inventory")) // Default sheet if none saved
        }

        pagerAdapter = SheetPagerAdapter(this, sheets)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = sheets[position].name
        }.attach()

        fabAddItem.setOnClickListener {
            val currentSheetIndex = viewPager.currentItem
            showAddItemDialog(currentSheetIndex)
        }

        fabAddSheet.setOnClickListener {
            showAddSheetDialog()
        }
    }

    private fun showAddSheetDialog() {
        val editText = EditText(this).apply {
            hint = "Enter sheet name"
        }

        AlertDialog.Builder(this)
            .setTitle("Add New Sheet")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val name = editText.text.toString().trim()
                if (name.isNotEmpty()) {
                    sheets.add(InventorySheet(name))
                    pagerAdapter.notifyItemInserted(sheets.lastIndex)
                    viewPager.setCurrentItem(sheets.lastIndex, true)
                    saveSheets()
                    Log.d("MainActivity", "Added sheet: $name")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddItemDialog(sheetIndex: Int) {
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

        colorSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val red = (progress shr 16) and 0xFF
                val green = (progress shr 8) and 0xFF
                val blue = progress and 0xFF
                val color = Color.rgb(red, green, blue)
                colorPreview.setBackgroundColor(color)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        layout.addView(editText)
        layout.addView(colorSeekBar)
        layout.addView(colorPreview)

        AlertDialog.Builder(context)
            .setTitle("Add New Item")
            .setView(layout)
            .setPositiveButton("Add") { _, _ ->
                val itemName = editText.text.toString().trim()
                if (itemName.isNotEmpty()) {
                    val color = (colorPreview.background as? android.graphics.drawable.ColorDrawable)?.color
                        ?: Color.WHITE
                    val newItem = InventoryItem(name = itemName, count = 0, color = color)

                    // Prevent duplicates on this sheet
                    val sheet = sheets[sheetIndex]
                    if (sheet.items.any { it.name.equals(itemName, ignoreCase = true) }) {
                        AlertDialog.Builder(this)
                            .setMessage("Duplicate item name on this sheet.")
                            .setPositiveButton("OK", null)
                            .show()
                        return@setPositiveButton
                    }

                    sheet.items.add(newItem)
                    sortSheetItems(sheet)
                    pagerAdapter.notifyItemChanged(sheetIndex)

                    saveSheets()
                    Log.d("MainActivity", "Added item '$itemName' to sheet index $sheetIndex")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun sortSheetItems(sheet: InventorySheet) {
        sheet.items.sortWith(compareBy({ it.color }, { it.name.lowercase() }))
    }

    fun deleteItemFromSheet(sheetIndex: Int, item: InventoryItem) {
        val sheet = sheets.getOrNull(sheetIndex) ?: return
        val removed = sheet.items.remove(item)
        if (removed) {
            saveSheets()
            pagerAdapter.notifyItemChanged(sheetIndex)
            Log.d("MainActivity", "Deleted item '${item.name}' from sheet $sheetIndex")
        }
    }

    fun deleteSheet(index: Int) {
        if (index >= 0 && index < sheets.size) {
            sheets.removeAt(index)
            pagerAdapter.notifyItemRemoved(index)
            val newIndex = if (index == sheets.size) index - 1 else index
            if (newIndex >= 0) viewPager.setCurrentItem(newIndex, true)
            saveSheets()
        }
    }

    // --- Persistence with SharedPreferences + Gson ---

    private fun saveSheets() {
        val prefs = getSharedPreferences("inventory_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(sheets)
        editor.putString("sheets_data", json)
        editor.apply()
        Log.d("MainActivity", "Saved sheets to SharedPreferences")
    }

    private fun loadSheets() {
        val prefs = getSharedPreferences("inventory_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("sheets_data", null)
        if (json != null) {
            val gson = Gson()
            val type = object : TypeToken<MutableList<InventorySheet>>() {}.type
            val loadedSheets: MutableList<InventorySheet> = gson.fromJson(json, type)
            sheets.clear()
            sheets.addAll(loadedSheets)
            Log.d("MainActivity", "Loaded sheets from SharedPreferences")
        }
    }
}