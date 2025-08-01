
package com.example.inventorytracker

data class InventoryItem(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    var quantity: Int = 0
)