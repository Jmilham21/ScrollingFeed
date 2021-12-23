package com.jmilham.scrollingfeed.ui.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jmilham.scrollingfeed.R
import com.jmilham.scrollingfeed.models.JwVideo
import com.jwplayer.pub.api.JWPlayer
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
        var jwPlayer: JWPlayer? = null

        init {
            // Define click listener for the ViewHolder's View.
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
    // todo: could set something up to show the image when player is setting up after initial load. would help for recycled items
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (viewHolder.jwPlayer != null) {
            viewHolder.jwPlayer!!.play()
        } else {
            viewHolder.jwPlayer = viewHolder.jwPlayerView.player
            val allDisabledUiConfig = UiConfig.Builder().hideAllControls().build()
            val config: PlayerConfig = PlayerConfig.Builder()
                .file("https://cdn.jwplayer.com/manifests/${dataSet[position].mediaid}.m3u8")
                .image("https://cdn.jwplayer.com/v2/media/${dataSet[position].mediaid}/poster.jpg")
                .autostart(true) // don't do this after testing
                .uiConfig(allDisabledUiConfig)
                .repeat(true)
                .build()
            viewHolder.jwPlayer?.setup(config)
            viewHolder.text.text = dataSet[position].title

        }
        // TODO: Setup the player config and load it with no controls and paused with a listener until fully visible?
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder.jwPlayer != null) {
            holder.jwPlayer!!.play()
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.jwPlayer?.pause()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    // poke from the pager itself, could be used to do something but limited access to views safely
    fun setCurrentVideoPosition(position: Int) {
        dataSet
    }

}
