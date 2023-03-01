package com.jmilham21.scrollingfeed.view.configs

import androidx.viewpager2.widget.ViewPager2
import com.jwplayer.pub.api.configuration.PlayerConfig

/**
 * UI configuration used by JwTikTakFragment to setup child fragments
 * and the scrolling effect itself
 *
 * This class has no useful logic; it's just a documentation example.
 *
 * @param offScreenLimit The number of fragments loaded on either side of current. Default = 2
 * @param stretching How the video may be stretched to fit the parent view. Default = PlayerConfig.STRETCHING_UNIFORM
 * @param preload If true, the player will start loading the buffer. Default = true
 * @param orientation of the ViewPager2 in the Fragment. Default = ViewPager2.ORIENTATION_VERTICAL
 *
 */
class TikTakUiConfig(// used by adapter
    private var offScreenLimit: Int = 2,
    private var stretching: String = PlayerConfig.STRETCHING_UNIFORM,
    private var preload: Boolean = true,
    private var orientation: Int = ViewPager2.ORIENTATION_VERTICAL
) {
    fun getOffscreenLimit(): Int {
        return offScreenLimit
    }

    fun getStretching(): String {
        return stretching
    }

    fun getPreload(): Boolean {
        return preload
    }

    fun getOrientation(): Int {
        return orientation
    }
}