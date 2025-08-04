package com.example.inventorytracker

data class InventorySheet(
    val name: String,
    val items: MutableList<InventoryItem> = mutableListOf()
)