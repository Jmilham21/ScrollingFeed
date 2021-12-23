package com.jmilham.scrollingfeed.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.jmilham.scrollingfeed.R
import com.jmilham.scrollingfeed.ui.helpers.VideoAdapter
import kotlinx.coroutines.runBlocking

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private var adapter: VideoAdapter = VideoAdapter(ArrayList())
     private var  pager: ViewPager2? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]


        viewModel.liveVideos.observe(viewLifecycleOwner, {
            // do stuff?
            Log.e("","")
            adapter = VideoAdapter(it)
            pager?.adapter = adapter
            adapter.notifyDataSetChanged()
        })
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

                adapter.setCurrentVideoPosition(position)
                Log.e("","")
            }
        })

        viewModel.loadSomeVideoList()

        Log.e("", "")
    }


}