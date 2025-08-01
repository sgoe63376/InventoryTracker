package com.example.inventorytracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class InventoryAdapter(
    private val items: MutableList<InventoryItem>,
    private val onQuantityChanged: (InventoryItem) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.tv_item_name)
        val itemQuantity: TextView = itemView.findViewById(R.id.tv_item_quantity)
        val btnDecrease: Button = itemView.findViewById(R.id.btn_decrease)
        val btnIncrease: Button = itemView.findViewById(R.id.btn_increase)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory, parent, false)
        return InventoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val item = items[position]

        holder.itemName.text = item.name
        holder.itemQuantity.text = item.quantity.toString()

        holder.btnDecrease.setOnClickListener {
            if (item.quantity > 0) {
                item.quantity--
                holder.itemQuantity.text = item.quantity.toString()
                onQuantityChanged(item)
            }
        }

        holder.btnIncrease.setOnClickListener {
            item.quantity++
            holder.itemQuantity.text = item.quantity.toString()
            onQuantityChanged(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun addItem(item: InventoryItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }
}