package com.challenge.fidoreader

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(var fragmentActivity: FragmentActivity, var fragments: MutableList<Fragment>) : FragmentStateAdapter(fragmentActivity){
    val TAG = "ViewPagerAdapter"

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }


}