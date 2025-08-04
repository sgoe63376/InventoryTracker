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
    private val onInventoryChanged: () -> Unit = {}
) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    inner class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorIndicator: View = itemView.findViewById(R.id.view_color_indicator)
        val itemName: TextView = itemView.findViewById(R.id.tv_item_name)
        val itemCount: TextView = itemView.findViewById(R.id.tv_item_count)
        val btnAdd: Button = itemView.findViewById(R.id.btn_add)
        val btnSubtract: Button = itemView.findViewById(R.id.btn_subtract)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory, parent, false)
        return InventoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val item = items[position]

        holder.colorIndicator.setBackgroundColor(item.color)
        holder.itemName.text = item.name
        holder.itemCount.text = item.count.toString()

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
    }

    override fun getItemCount(): Int = items.size
}