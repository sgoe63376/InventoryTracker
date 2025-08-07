package com.example.inventorytracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InventoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InventoryAdapter
    private var sheetIndex: Int = 0

    companion object {
        fun newInstance(sheetIndex: Int): InventoryFragment {
            val fragment = InventoryFragment()
            val args = Bundle()
            args.putInt("sheetIndex", sheetIndex)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sheetIndex = arguments?.getInt("sheetIndex") ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inventory, container, false)
        recyclerView = view.findViewById(R.id.rv_inventory)

        val mainActivity = activity as? MainActivity
        val items = mainActivity?.sheets?.get(sheetIndex)?.items
            ?: mutableListOf()

        adapter = InventoryAdapter(
            items,
            onInventoryChanged = {
                // Could update UI or save state here if needed
            },
            onDelete = { item ->
                mainActivity?.deleteItemFromSheet(sheetIndex, item)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        return view
    }

    fun refreshItems() {
        adapter.notifyDataSetChanged()
    }
}