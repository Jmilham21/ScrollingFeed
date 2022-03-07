package com.jmilham.scrollingfeed.ui.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jmilham.scrollingfeed.R
import com.jmilham.scrollingfeed.models.JwVideo
import com.jwplayer.pub.api.JWPlayer
import com.jwplayer.pub.api.PlayerState
import com.jwplayer.pub.api.configuration.PlayerConfig
import com.jwplayer.pub.api.configuration.UiConfig
import com.jwplayer.pub.view.JWPlayerView
import java.util.*

class VideoAdapter(private var dataSet: ArrayList<JwVideo>) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

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

        init {
            parent.setOnClickListener {
                if (jwPlayer != null) {
                    if (jwPlayer!!.state == PlayerState.PLAYING) {
                        jwPlayer?.pause()
                    } else if (jwPlayer!!.state == PlayerState.PAUSED) {
                        jwPlayer?.play()
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
                        R.drawable.icon_play
                    )
                )
            } else if (jwPlayer?.state == PlayerState.PAUSED) {
                play_pause.setImageDrawable(
                    ContextCompat.getDrawable(
                        jwPlayerView.context,
                        R.drawable.icon_pause
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
            viewHolder.jwPlayer = viewHolder.jwPlayerView.player
            val allDisabledUiConfig = UiConfig.Builder().hideAllControls().build()
            val config: PlayerConfig = PlayerConfig.Builder()
                .file("https://cdn.jwplayer.com/manifests/${dataSet[position].mediaid}.m3u8")
                .image("https://cdn.jwplayer.com/v2/media/${dataSet[position].mediaid}/poster.jpg")
                .stretching(PlayerConfig.STRETCHING_UNIFORM) // allow the media to fill allotted space as best as possible
                .uiConfig(allDisabledUiConfig)
                .repeat(true)
                .build()
            viewHolder.jwPlayer?.setup(config)
            viewHolder.jwPlayer?.controls = false // fully disable the controls
            viewHolder.text.text = dataSet[position].title
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
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
