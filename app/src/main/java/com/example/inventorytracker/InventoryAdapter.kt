package com.example.inventorytracker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class InventoryAdapter(
    private val items: MutableList<InventoryItem>,
    private val onInventoryChanged: () -> Unit,
    private val onDelete: ((InventoryItem) -> Unit)? = null
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val colorIndicator: View = view.findViewById(R.id.view_color_indicator)
        val nameText: TextView = view.findViewById(R.id.tv_item_name)
        val countText: TextView = view.findViewById(R.id.tv_item_count)
        val btnAdd: Button = view.findViewById(R.id.btn_add)
        val btnSubtract: Button = view.findViewById(R.id.btn_subtract)
        val btnDelete: Button = view.findViewById(R.id.btn_delete)  // New delete button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.colorIndicator.setBackgroundColor(item.color)
        holder.nameText.text = item.name
        holder.countText.text = item.count.toString()

        holder.btnAdd.setOnClickListener {
            item.count++
            notifyItemChanged(position)
            onInventoryChanged()
        }

        holder.btnSubtract.setOnClickListener {
            if (item.count > 0) {
                item.count--
                notifyItemChanged(position)
                onInventoryChanged()
            }
        }

        holder.btnDelete.setOnClickListener {
            onDelete?.invoke(item)
        }
    }

    fun addItem(newItem: InventoryItem): Boolean {
        // Check for duplicates by name (case insensitive)
        if (items.any { it.name.equals(newItem.name, ignoreCase = true) }) {
            return false
        }
        items.add(newItem)
        sortItems()
        notifyDataSetChanged()
        onInventoryChanged()
        return true
    }

    fun removeItem(item: InventoryItem) {
        val removed = items.remove(item)
        if (removed) {
            notifyDataSetChanged()
            onInventoryChanged()
        }
    }

    private fun sortItems() {
        items.sortWith(compareBy({ it.color }, { it.name.toLowerCase() }))
    }
}


