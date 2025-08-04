package com.example.inventorytracker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class InventoryAdapter(
    private val items: MutableList<InventoryItem>,
    private val onInventoryChanged: (InventoryItem) -> Unit,
    private val onDelete: (InventoryItem) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.tv_item_name)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.btn_delete)
        val colorBar: View = itemView.findViewById(R.id.color_bar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemName.text = item.name
        holder.colorBar.setBackgroundColor(item.color)
        holder.deleteBtn.setOnClickListener {
            onDelete(item)
        }
    }

    fun addItem(item: InventoryItem): Boolean {
        if (items.any { it.name.equals(item.name, ignoreCase = true) }) {
            return false // Duplicate
        }
        items.add(item)
        notifyItemInserted(items.size - 1)
        onInventoryChanged(item)
        return true
    }

    fun removeItem(item: InventoryItem) {
        val idx = items.indexOf(item)
        if (idx != -1) {
            items.removeAt(idx)
            notifyItemRemoved(idx)
            onInventoryChanged(item)
        }
    }
}