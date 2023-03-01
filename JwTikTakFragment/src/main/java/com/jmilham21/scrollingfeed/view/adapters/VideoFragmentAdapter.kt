package com.jmilham21.scrollingfeed.view.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.jmilham21.scrollingfeed.R
import com.jmilham21.scrollingfeed.data.JwMedia
import com.jmilham21.scrollingfeed.fragment.VideoFragment
import com.jmilham21.scrollingfeed.view.configs.TikTakUiConfig


class VideoFragmentAdapter(
    private val fragment: Fragment,
    var data: ArrayList<JwMedia>,
    private val config: TikTakUiConfig
) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return data.size
    }

    fun setPagingEnabled(enabled: Boolean) {
        fragment.requireActivity().findViewById<ViewPager2>(R.id.pager).isUserInputEnabled = enabled
    }

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        return VideoFragment(data[position], this, position, config)
    }

    /***
     * Pass the index of the current fragment to remove from the list
     * and get off the screen
     */
    // TODO
    fun removeFragment(position: Int) {
        // Stub

    }

}