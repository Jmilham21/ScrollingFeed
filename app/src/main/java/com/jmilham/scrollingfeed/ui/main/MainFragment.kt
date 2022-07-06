package com.jmilham.scrollingfeed.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.jmilham.scrollingfeed.R
import com.jmilham.scrollingfeed.ui.helpers.VideoFragmentAdapter

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: VideoFragmentAdapter
    private var pager: ViewPager2? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pager = view.findViewById(R.id.pager)
        adapter = VideoFragmentAdapter(this, ArrayList())
        pager?.adapter = adapter
        pager?.offscreenPageLimit = 1 // this is a danger number if higher (and sometimes even at 1)
        // BE CAUTIOUS OF ABOVE AND THE AMOUNT OF DECODERS THE DEVICE FOR THE MIME TYPE.
        // there are two limitations for how large this should be: decoders and memory allocation
        viewModel.liveVideos.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                // bad playlistID or bad config
                Toast.makeText(context, "PlaylistID invalid", Toast.LENGTH_LONG).show()
            } else {
                // TODO: Refactor how to insert an ad type into the list
//                it.add(it.size / 2, JwAdvertisement("https://playertest.longtailvideo.com/vast/preroll-jw.xml")) // add an ad to the middle for test
                adapter = VideoFragmentAdapter(this, it)
                pager?.adapter = adapter
                adapter.notifyDataSetChanged() // safe since only on fresh load
            }
        }
        viewModel.loadSomeVideoList()
    }

    // needs rules for non empty? or some kind of formatting?
    fun updatePlaylistId(playlistId: String) {
        viewModel.loadSomeVideoList(playlistId)
    }

    // taking this as a chance to dump all the views inside ViewPager2
    override fun onDestroyView() {
        super.onDestroyView()
        val size =  adapter.data.size
        adapter.data = ArrayList()
        adapter.notifyItemRangeRemoved(0,size)
    }
}