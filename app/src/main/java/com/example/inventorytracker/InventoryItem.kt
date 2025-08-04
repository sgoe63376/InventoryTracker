package com.example.inventorytracker

data class InventoryItem(
    val name: String,
    var color: Int = 0xFFFFFFFF.toInt() // Default: white
)