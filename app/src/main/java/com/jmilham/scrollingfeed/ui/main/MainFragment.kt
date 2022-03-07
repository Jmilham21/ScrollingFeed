package com.jmilham.scrollingfeed.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.jmilham.scrollingfeed.R
import com.jmilham.scrollingfeed.models.JwAdvertisement
import com.jmilham.scrollingfeed.ui.helpers.VideoAdapter

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private var adapter: VideoAdapter = VideoAdapter(ArrayList())
    private var pager: ViewPager2? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]


        viewModel.liveVideos.observe(viewLifecycleOwner) {
            it.add(it.size / 2, JwAdvertisement("https://playertest.longtailvideo.com/vast/preroll-jw.xml")) // add an ad to the middle for test
            adapter = VideoAdapter(it)
            pager?.adapter = adapter
            pager?.offscreenPageLimit = 4
            adapter.notifyDataSetChanged() // safe since only on fresh load
        }
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    //
    override fun onStart() {
        super.onStart()
        pager = view?.findViewById(R.id.pager)
        pager?.adapter = adapter

        pager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val count = (pager?.adapter as VideoAdapter).itemCount
                for (i in 0..count) {
                    // go through and pause all, just in case?
                    if((pager?.getChildAt(0) as RecyclerView).findViewHolderForAdapterPosition(i) is VideoAdapter.ViewHolder){
                        ((pager?.getChildAt(0) as RecyclerView).findViewHolderForAdapterPosition(i) as VideoAdapter.ViewHolder).jwPlayer?.pause()
                        ((pager?.getChildAt(0) as RecyclerView).findViewHolderForAdapterPosition(i) as VideoAdapter.ViewHolder).setIsPlayingIcon()
                        ((pager?.getChildAt(0) as RecyclerView).findViewHolderForAdapterPosition(i) as VideoAdapter.ViewHolder).startIconTimeout()
                        ((pager?.getChildAt(0) as RecyclerView).findViewHolderForAdapterPosition(i) as VideoAdapter.ViewHolder).jwPlayer?.pauseAd(true)

                    }
                }
                // Hacky way to touch the currently visible view in a ViewPager2.
                ((pager?.getChildAt(0) as RecyclerView).findViewHolderForAdapterPosition(position) as VideoAdapter.ViewHolder).jwPlayer?.play()
                ((pager?.getChildAt(0) as RecyclerView).findViewHolderForAdapterPosition(position) as VideoAdapter.ViewHolder).jwPlayer?.pauseAd(false)

            }
        })
        viewModel.loadSomeVideoList()
    }


}