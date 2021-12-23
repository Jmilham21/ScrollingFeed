package com.jmilham.scrollingfeed.ui.helpers

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jmilham.scrollingfeed.R
import com.jmilham.scrollingfeed.models.JwVideo
import com.jwplayer.pub.view.JWPlayerView
import java.util.*

class VideoAdapter(private var dataSet: ArrayList<JwVideo>) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val player: JWPlayerView = view.findViewById(R.id.jwplayer)

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
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Log.e("","")

        // TODO: Setup the player config and load it with no controls and paused with a listener until fully visible?
        viewHolder.player.player.confi

    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
