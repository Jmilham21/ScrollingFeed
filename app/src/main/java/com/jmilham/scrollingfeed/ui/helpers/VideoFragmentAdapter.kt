package com.jmilham.scrollingfeed.ui.helpers

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jmilham.scrollingfeed.models.JwMedia

class VideoFragmentAdapter(fragment: Fragment, var data: ArrayList<JwMedia>) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return data.size
    }

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        val fragment = VideoFragment(data[position], this, position)
        return fragment
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