package com.jmilham21.scrollingfeed.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.jmilham21.scrollingfeed.R
import com.jmilham21.scrollingfeed.view.adapters.VideoFragmentAdapter
import com.jmilham21.scrollingfeed.view.configs.TikTakUiConfig
import com.jwplayer.pub.api.configuration.PlayerConfig
import com.jwplayer.pub.api.events.EventType
import com.jwplayer.pub.api.events.SetupErrorEvent
import com.jwplayer.pub.api.events.listeners.VideoPlayerEvents
import com.jwplayer.pub.view.JWPlayerView

class JwTikTakFragment(
    private val playlistId: String = "",
    private val config: TikTakUiConfig = TikTakUiConfig()
) : Fragment() {

    private var errorMessage: String = ""
    private var errorCode: Int = 0
    var validLicense = true

    companion object {
        fun newInstance(
            playlistId: String = "",
            config: TikTakUiConfig = TikTakUiConfig()
        ) = JwTikTakFragment(playlistId, config)
    }

    private lateinit var viewModel: JwTikTakViewModel
    private var adapter: VideoFragmentAdapter? = null
    private var pager: ViewPager2? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[JwTikTakViewModel::class.java]
        return inflater.inflate(R.layout.tik_tak, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pager = view.findViewById(R.id.pager)
        pager?.orientation = config.getOrientation()
        pager?.offscreenPageLimit = config.getOffscreenLimit() // Be very aware of this number
        viewModel.liveVideos.observe(viewLifecycleOwner) {
            if (!validLicense) {
                adapter?.data = ArrayList()
                adapter?.notifyDataSetChanged()
                view.findViewById<TextView>(R.id.error_text).text = getString(R.string.error_message, errorMessage, errorCode)
                return@observe
            }
            if (it.isEmpty()) {
                // bad playlistID or bad config
                Toast.makeText(context, "PlaylistID invalid", Toast.LENGTH_LONG).show()
            } else {
                adapter = VideoFragmentAdapter(this, it, config)
                pager?.adapter = adapter
            }
        }
        testJwKey()
        viewModel.loadSomeVideoList(playlistId)
        pager?.setPageTransformer(MarginPageTransformer(1))
    }

    /***
     * Hacky way to test the key is valid before moving on: must be done async alongside the
     * real data call to avoid EXTRA latency
     */
    private fun testJwKey() {
        val dummyPlayer = JWPlayerView(requireContext()).getPlayer(this)
        dummyPlayer.addListener(EventType.SETUP_ERROR, object : VideoPlayerEvents.OnSetupErrorListener {
            override fun onSetupError(p0: SetupErrorEvent) {
                if (p0.code == 100012) {
                    // We have a bad license
                    viewModel.loadSomeVideoList("")
                    validLicense = false
                    errorCode = p0.code
                    errorMessage = p0.message
                }
            }
        })
        dummyPlayer.setup(PlayerConfig.Builder().autostart(true).playlistUrl("https://cdn.jwplayer.com/v2/playlists/zAdW5unD").build())
    }

    // needs rules for non empty? or some kind of formatting?
    fun updatePlaylistId(playlistId: String) {
        viewModel.loadSomeVideoList(playlistId)
    }

    // taking this as a chance to dump all the views inside ViewPager2
    override fun onDestroyView() {
        super.onDestroyView()
        val size = adapter?.data?.size
        adapter?.data = ArrayList()
        adapter?.notifyItemRangeRemoved(0, size ?: 0)
    }
}