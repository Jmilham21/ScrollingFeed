package com.jwplayer.jwtiktak.fragment

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
import com.jwplayer.jwtiktak.R
import com.jwplayer.jwtiktak.data.JwAdvertisement
import com.jwplayer.jwtiktak.data.JwMedia
import com.jwplayer.jwtiktak.data.JwVideo
import com.jwplayer.jwtiktak.databinding.VideoPageBinding
import com.jwplayer.jwtiktak.view.adapters.VideoFragmentAdapter
import com.jwplayer.pub.api.JWPlayer
import com.jwplayer.pub.api.PlayerState
import com.jwplayer.pub.api.configuration.PlayerConfig
import com.jwplayer.pub.api.events.*
import com.jwplayer.pub.api.events.listeners.AdvertisingEvents
import com.jwplayer.pub.api.events.listeners.VideoPlayerEvents.*
import com.squareup.picasso.Picasso


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
            loadThumbnail(jwMedia)
            setupVideo()
        } else if (jwMedia is JwAdvertisement) {
            setupAdvertisement()
        }

        return binding.root
    }

    private fun loadThumbnail(media: JwVideo) {
        Picasso.get().load(media.image).fit().centerInside()
            .into(binding.videoThumbnail)
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (!menuVisible) {
            // being used as a double check since the lifecycle observer can't be trusted due to multi frags
            // loaded at once and the lifecycle events aren't a sure thing.
            val position = jwPlayer?.position
            if (position != null) {
                if (position > .5) {
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
                if (binding.loadingIcon.visibility == View.INVISIBLE) {
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
    }

    private fun setupVideo() {
        isAd = false
        val media = jwMedia as JwVideo
        jwPlayer = binding.jwplayer.getPlayer(this)
        setupListeners()
        val config: PlayerConfig = viewModel.getPlayerConfig(jwMedia)
        jwPlayer?.setup(config)
        jwPlayer?.controls = false // fully disable the controls
        binding.textView.text = media.title
    }

    private fun setupAdvertisement() {
        isAd = true
        val advertisement = jwMedia as JwAdvertisement
        jwPlayer = binding.jwplayer.getPlayer(this)
        setupListeners()
        val playerConfig = viewModel.getPlayerAdvertConfig(advertisement)
        jwPlayer?.setup(playerConfig)
        jwPlayer?.controls = false // fully disable the controls
    }

    /***
     * Attach all the necesssary listeners for the player at this point
     *
     * Requires the JwPlayer object to be non null
     */
    private fun setupListeners() {
        jwPlayer!!.addListener(EventType.BUFFER, object : OnBufferListener {
            override fun onBuffer(p0: BufferEvent?) {
                if (p0?.bufferReason != BufferReason.COMPLETE) {
                    binding.loadingIcon.visibility = View.VISIBLE
                } else {
                    binding.loadingIcon.visibility = View.INVISIBLE
                }
            }

        })
        jwPlayer!!.addListener(
            EventType.ERROR,
            object : OnErrorListener {
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

        jwPlayer!!.addListener(EventType.READY, object : OnReadyListener {
            override fun onReady(p0: ReadyEvent?) {
            }
        })

        jwPlayer!!.addListener(EventType.SEEK, object : OnSeekListener {
            override fun onSeek(p0: SeekEvent?) {
                binding.videoThumbnail.visibility = View.VISIBLE
                binding.loadingIcon.visibility = View.VISIBLE
            }
        })
        // I also happen when the player starts a repeat!
        jwPlayer!!.addListener(EventType.SEEKED, object : OnSeekedListener {
            override fun onSeeked(p0: SeekedEvent?) {
                binding.videoThumbnail.visibility = View.GONE
            }
        })

        jwPlayer!!.addListener(EventType.PLAY, object : OnPlayListener {
            override fun onPlay(p0: PlayEvent?) {
                binding.videoThumbnail.visibility = View.GONE
                binding.loadingIcon.visibility = View.INVISIBLE
            }
        })

        jwPlayer!!.addListener(EventType.ERROR, object : OnErrorListener {
            override fun onError(p0: ErrorEvent?) {
                // TODO: Fragment is in a dead state, show a message or recover?
            }

        })
        jwPlayer!!.addListener(EventType.SETUP_ERROR, object : OnSetupErrorListener {
            override fun onSetupError(p0: SetupErrorEvent?) {
                // TODO: Fragment is in a dead state, show a message or recover?
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
