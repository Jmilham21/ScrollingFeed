package com.jmilham.scrollingfeed.ui.helpers

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jmilham.scrollingfeed.R
import com.jmilham.scrollingfeed.models.JwAdvertisement
import com.jmilham.scrollingfeed.models.JwMedia
import com.jmilham.scrollingfeed.models.JwVideo
import com.jwplayer.pub.api.JWPlayer
import com.jwplayer.pub.api.PlayerState
import com.jwplayer.pub.api.configuration.PlayerConfig
import com.jwplayer.pub.api.configuration.UiConfig
import com.jwplayer.pub.api.configuration.ads.ima.ImaAdvertisingConfig
import com.jwplayer.pub.api.events.*
import com.jwplayer.pub.api.events.listeners.AdvertisingEvents
import com.jwplayer.pub.api.events.listeners.VideoPlayerEvents
import com.jwplayer.pub.api.media.ads.AdBreak
import com.jwplayer.pub.view.JWPlayerView

class VideoAdapter(private var dataSet: ArrayList<JwMedia>) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    val tag = "VideoAdapter.ViewHolder"

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val jwPlayerView: JWPlayerView = view.findViewById(R.id.jwplayer)
        val text: TextView = view.findViewById(R.id.textView)
        val parent: ConstraintLayout = view.findViewById(R.id.parent)
        val play_pause: ImageView = view.findViewById(R.id.play_pause)
        var jwPlayer: JWPlayer? = null

        var isAd: Boolean = false

        init {
            parent.setOnClickListener {
                if (jwPlayer != null) {
                    if (jwPlayer!!.state == PlayerState.PLAYING) {
                        jwPlayer?.pause()
                        jwPlayer?.pauseAd(true)
                    } else if (jwPlayer!!.state == PlayerState.PAUSED) {
                        jwPlayer?.play()
                        jwPlayer?.pauseAd(false)
                    }

                    setIsPlayingIcon()
                    play_pause.visibility =
                        View.VISIBLE // force show it and in .5 seconds it'll hide or stay

                    startIconTimeout()
                }
            }
        }

        fun setIsPlayingIcon() {
            if (jwPlayer?.state == PlayerState.PLAYING) {
                play_pause.setImageDrawable(
                    ContextCompat.getDrawable(
                        jwPlayerView.context,
                        R.drawable.ic_jw_play
                    )
                )
            } else if (jwPlayer?.state == PlayerState.PAUSED) {
                play_pause.setImageDrawable(
                    ContextCompat.getDrawable(
                        jwPlayerView.context,
                        R.drawable.ic_jw_play
                    )
                )
            }
        }

        fun startIconTimeout() {
            // after .5 seconds hide the icon if playing
            this.parent.postDelayed({
                if (jwPlayer != null) {
                    if (jwPlayer!!.state == PlayerState.PLAYING) {
                        play_pause.visibility = View.INVISIBLE
                    } else if (jwPlayer!!.state == PlayerState.PAUSED) {
                        play_pause.visibility = View.VISIBLE
                    }
                }
            }, 500)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.video_page, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (viewHolder.jwPlayer != null) {
//            viewHolder.jwPlayer!!.play()
        } else {

            if (dataSet[viewHolder.adapterPosition] is JwVideo) {
                viewHolder.isAd = false
                val media = dataSet[viewHolder.adapterPosition] as JwVideo
                viewHolder.jwPlayer = viewHolder.jwPlayerView.player
                val allDisabledUiConfig = UiConfig.Builder().hideAllControls().build()
                val config: PlayerConfig = PlayerConfig.Builder()
                    .file("https://cdn.jwplayer.com/manifests/${media.mediaid}.m3u8")
                    .image("https://cdn.jwplayer.com/v2/media/${media}/poster.jpg")
                    .stretching(PlayerConfig.STRETCHING_UNIFORM) // allow the media to fill allotted space as best as possible
                    .uiConfig(allDisabledUiConfig)
                    .repeat(true)
                    .build()
                viewHolder.jwPlayer?.setup(config)
                viewHolder.jwPlayer?.controls = false // fully disable the controls
                viewHolder.text.text = media.title

                viewHolder.jwPlayer!!.addListener(
                    EventType.ERROR,
                    object : VideoPlayerEvents.OnErrorListener {
                        override fun onError(p0: ErrorEvent?) {
                            if (p0?.message?.isNotEmpty() == true) {
                                // there is an error message and should probably handle this
                                Log.e(tag, p0.message)
                            }
                        }
                    })
            } else {
                viewHolder.isAd = true
                val advertisement = dataSet[viewHolder.adapterPosition] as JwAdvertisement
                viewHolder.jwPlayer = viewHolder.jwPlayerView.player

                val adSchedule: MutableList<AdBreak> = ArrayList()
                val adBreak: AdBreak = AdBreak.Builder()
                    .offset("pre")
                    .tag(advertisement.adUrl)
                    .build()
                adSchedule.add(adBreak)
                val imaAdvertising: ImaAdvertisingConfig = ImaAdvertisingConfig.Builder()
                    .schedule(adSchedule)
                    .build()

                viewHolder.jwPlayer!!.addListener(
                    EventType.AD_ERROR,
                    object : AdvertisingEvents.OnAdErrorListener {
                        override fun onAdError(p0: AdErrorEvent?) {
                            if (p0?.message?.isNotEmpty() == true) {
                                // how to handle ad error?
                                Log.e(tag, p0.message)

                                // For now just skip it
                                dataSet.remove(advertisement)
                                this@VideoAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                            }
                        }

                    })

                viewHolder.jwPlayer!!.addListener(
                    EventType.AD_COMPLETE,
                    object : AdvertisingEvents.OnAdCompleteListener {
                        override fun onAdComplete(p0: AdCompleteEvent?) {
                            dataSet.remove(advertisement)
                            this@VideoAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                        }
                    })

                viewHolder.jwPlayer!!.addListener(
                    EventType.AD_SKIPPED,
                    object : AdvertisingEvents.OnAdSkippedListener {
                        override fun onAdSkipped(p0: AdSkippedEvent?) {
                            dataSet.remove(advertisement)
                            this@VideoAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                        }
                    })


                val allDisabledUiConfig = UiConfig.Builder().hideAllControls().build()
                val config: PlayerConfig = PlayerConfig.Builder()
                    .file("http://content.jwplatform.com/videos/nhYDGoyh-el5vTWpr.mp4") // TODO: how do i setup a config without a file / media and just do an ad?
                    .stretching(PlayerConfig.STRETCHING_UNIFORM) // allow the media to fill allotted space as best as possible
                    .advertisingConfig(imaAdvertising)
                    .uiConfig(allDisabledUiConfig)
                    .build()
                viewHolder.jwPlayer?.setup(config)
                viewHolder.jwPlayer?.controls = false // fully disable the controls
            }

        }
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder.jwPlayer != null) {
            holder.play_pause.visibility = View.INVISIBLE
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.jwPlayer?.pause()
        holder.jwPlayer?.pauseAd(true)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
