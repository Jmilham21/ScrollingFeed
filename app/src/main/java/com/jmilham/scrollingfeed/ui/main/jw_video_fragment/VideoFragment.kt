package com.jmilham.scrollingfeed.ui.main.jw_video_fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jmilham.scrollingfeed.R
import com.jmilham.scrollingfeed.databinding.VideoPageBinding
import com.jmilham.scrollingfeed.models.JwAdvertisement
import com.jmilham.scrollingfeed.models.JwMedia
import com.jmilham.scrollingfeed.models.JwVideo
import com.jmilham.scrollingfeed.ui.helpers.VideoFragmentAdapter
import com.jwplayer.pub.api.JWPlayer
import com.jwplayer.pub.api.PlayerState
import com.jwplayer.pub.api.configuration.PlayerConfig
import com.jwplayer.pub.api.configuration.UiConfig
import com.jwplayer.pub.api.configuration.ads.ima.ImaAdvertisingConfig
import com.jwplayer.pub.api.events.*
import com.jwplayer.pub.api.events.listeners.AdvertisingEvents
import com.jwplayer.pub.api.events.listeners.VideoPlayerEvents
import com.jwplayer.pub.api.media.ads.AdBreak


class VideoFragment(
    val jwMedia: JwMedia,
    val videoFragmentAdapter: VideoFragmentAdapter,
    val position: Int
) : Fragment() {

    private lateinit var viewModel: VideoViewModel
    private lateinit var binding: VideoPageBinding


    private var jwPlayer: JWPlayer? = null
    var isAd = false

    val TAG = "VideoFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[VideoViewModel::class.java]
        binding = DataBindingUtil.inflate(
            inflater, R.layout.video_page, container, false
        )

        // setup
        if (jwMedia is JwVideo) {
            setupVideo()
        } else if (jwMedia is JwAdvertisement) {
            setupAdvertisement()
        }

        return binding.root
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (!menuVisible) {
            // being used as a double check since the lifecycle observer can't be trusted due to multi frags
            // loaded at once and the lifecycle events aren't a sure thing.
            val position = jwPlayer?.position
            if (position != null) {
                if (position > .5){
                    jwPlayer?.seek(0.0) // TODO this is causing hiccups but best way to force a start over?
                }
            }
            jwPlayer?.pause()
        }
    }

    override fun onDestroy() {
        jwPlayer?.stop()
        super.onDestroy()
    }

    // can do setup for player here
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.setOnClickListener {
            if (jwPlayer != null) {
                if (jwPlayer!!.state == PlayerState.PLAYING) {
                    jwPlayer?.pause()
                    jwPlayer?.pauseAd(true)
                } else if (jwPlayer!!.state == PlayerState.PAUSED) {
                    jwPlayer?.play()
                    jwPlayer?.pauseAd(false)
                }

                setIsPlayingIcon()
                binding.playPause.visibility =
                    View.VISIBLE // force show it and in .5 seconds it'll hide or stay

                startIconTimeout()
            }
        }
    }

    private fun setupVideo() {
        isAd = false
        val media = jwMedia as JwVideo
        jwPlayer = binding.jwplayer.getPlayer(this)
        setupListeners()
        val allDisabledUiConfig = UiConfig.Builder().hideAllControls().build()
        val config: PlayerConfig = PlayerConfig.Builder()
            .file("https://cdn.jwplayer.com/manifests/${media.mediaid}.m3u8")
            .image("https://cdn.jwplayer.com/v2/media/${media.mediaid}/poster.jpg")
            .stretching(PlayerConfig.STRETCHING_FILL) // allow the media to fill allotted space as best as possible
//            .preload(true) // data hog and may be overkill
            .autostart(true)
            .uiConfig(allDisabledUiConfig)
            .repeat(true)
            .build()
        jwPlayer?.setup(config)
        jwPlayer?.controls = false // fully disable the controls
        binding.textView.text = media.title
    }

    private fun setupAdvertisement() {
        isAd = true
        val advertisement = jwMedia as JwAdvertisement
        jwPlayer = binding.jwplayer.getPlayer(this)
        setupListeners()
        val adSchedule: MutableList<AdBreak> = ArrayList()
        val adBreak: AdBreak = AdBreak.Builder()
            .offset("pre")
            .tag(advertisement.adUrl)
            .build()
        adSchedule.add(adBreak)
        val imaAdvertising: ImaAdvertisingConfig = ImaAdvertisingConfig.Builder()
            .schedule(adSchedule)
            .build()

        val allDisabledUiConfig = UiConfig.Builder().hideAllControls().build()
        val config: PlayerConfig = PlayerConfig.Builder()
            .file("http://content.jwplatform.com/videos/nhYDGoyh-el5vTWpr.mp4") // TODO: how do i setup a config without a file / media and just do an ad?
            .stretching(PlayerConfig.STRETCHING_UNIFORM) // allow the media to fill allotted space as best as possible
            .advertisingConfig(imaAdvertising)
            .uiConfig(allDisabledUiConfig)
            .build()
        jwPlayer?.setup(config)
        jwPlayer?.controls = false // fully disable the controls
    }

    /***
     * Attach all the necesssary listeners for the player at this point
     *
     * Requires the JwPlayer object to be non null
     */
    private fun setupListeners() {
        jwPlayer!!.addListener(
            EventType.ERROR,
            object : VideoPlayerEvents.OnErrorListener {
                override fun onError(p0: ErrorEvent?) {
                    if (p0?.message?.isNotEmpty() == true) {
                        // there is an error message and should probably handle this
                        Log.e(TAG, p0.message)
                    }
                }
            })

        jwPlayer!!.addListener(
            EventType.AD_ERROR,
            object : AdvertisingEvents.OnAdErrorListener {
                override fun onAdError(p0: AdErrorEvent?) {
                    if (p0?.message?.isNotEmpty() == true) {
                        videoFragmentAdapter.removeFragment(position)
                        videoFragmentAdapter.setPagingEnabled(true)
                        isAd = false // The add has been removed
                    }
                }

            })

        jwPlayer!!.addListener(
            EventType.AD_COMPLETE,
            object : AdvertisingEvents.OnAdCompleteListener {
                override fun onAdComplete(p0: AdCompleteEvent?) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        videoFragmentAdapter.removeFragment(position)
                        videoFragmentAdapter.setPagingEnabled(true)
                        isAd = false // The add has been removed
                    }, 200)
                }
            })

        jwPlayer!!.addListener(
            EventType.AD_SKIPPED,
            object : AdvertisingEvents.OnAdSkippedListener {
                override fun onAdSkipped(p0: AdSkippedEvent?) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        videoFragmentAdapter.removeFragment(position)
                        videoFragmentAdapter.setPagingEnabled(true)
                        isAd = false // The add has been removed
                    }, 200)
                }
            })
    }

    // can safely tell the screen is back
    override fun onResume() {
        super.onResume()
        if (jwPlayer != null) {
            binding.playPause.visibility = View.INVISIBLE
        }
        jwPlayer?.play()
        jwPlayer?.pauseAd(false)

        videoFragmentAdapter.setPagingEnabled(!isAd)
    }

    // can safely tell the screen is gone
    override fun onPause() {
        super.onPause()
        jwPlayer?.pause()
        jwPlayer?.pauseAd(true)
    }

    /**
     *  after .5 seconds hide the icon if playing
     */
    fun startIconTimeout() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (jwPlayer != null) {
                if (jwPlayer!!.state == PlayerState.PLAYING) {
                    binding.playPause.visibility = View.INVISIBLE
                } else if (jwPlayer!!.state == PlayerState.PAUSED) {
                    binding.playPause.visibility = View.VISIBLE
                }
            }
        }, 500)
    }

    fun setIsPlayingIcon() {
        if (jwPlayer?.state == PlayerState.PLAYING) {
            binding.playPause.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_jw_play
                )
            )
        } else if (jwPlayer?.state == PlayerState.PAUSED) {
            binding.playPause.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_jw_pause
                )
            )
        }
    }
}