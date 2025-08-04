package com.example.inventorytracker

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SheetPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val sheets: List<InventorySheet>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = sheets.size

    override fun createFragment(position: Int) = InventoryFragment.newInstance(position)
}