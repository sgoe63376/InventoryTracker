package com.example.inventorytracker

import android.graphics.Color

data class InventoryItem(
    val name: String,
    var count: Int = 0,
    val color: Int = Color.WHITE
)