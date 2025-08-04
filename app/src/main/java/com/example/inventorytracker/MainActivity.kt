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

class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var fabAddItem: FloatingActionButton
    private lateinit var fabAddSheet: FloatingActionButton
    private lateinit var pagerAdapter: SheetPagerAdapter

    // Start with "Inventory" sheet
    val sheets = mutableListOf(InventorySheet("Inventory"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)
        fabAddItem = findViewById(R.id.fab_add_item)
        fabAddSheet = findViewById(R.id.fab_add_sheet)

        pagerAdapter = SheetPagerAdapter(this, sheets)
        viewPager.adapter = pagerAdapter

        // Automatically link tab text to sheet name
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = sheets[position].name
        }.attach()

        // FAB: Add new item to selected sheet
        fabAddItem.setOnClickListener {
            val currentSheetIndex = viewPager.currentItem
            showAddItemDialog(currentSheetIndex)
        }

        // FAB: Add new sheet
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

                    // Add to correct sheet
                    sheets[sheetIndex].items.add(newItem)
                    val currentFragment = supportFragmentManager.findFragmentByTag("f${sheetIndex}") as? InventoryFragment
                    currentFragment?.refreshItems()
                    Log.d("MainActivity", "Added item '$itemName' to sheet index $sheetIndex")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}