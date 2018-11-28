package com.pixelart.mediaplayerapp.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class TabsAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private val fragments : List<Fragment> = ArrayList()
    private val titles : List<String> = ArrayList()

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return fragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }

    override fun getCount(): Int {
        // Show total pages.
        return fragments.size
    }

    fun addFragments(fragment: Fragment, title : String)
    {
        (fragments as ArrayList<Fragment>).add(fragment)
        (titles as ArrayList<String>).add(title)
    }
}