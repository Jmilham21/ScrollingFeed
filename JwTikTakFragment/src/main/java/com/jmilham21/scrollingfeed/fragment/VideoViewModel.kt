package com.jmilham21.scrollingfeed.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jmilham21.scrollingfeed.data.JwAdvertisement
import com.jmilham21.scrollingfeed.data.JwVideo
import com.jmilham21.scrollingfeed.middleware.JwMiddleware
import com.jmilham21.scrollingfeed.view.configs.TikTakUiConfig
import com.jwplayer.pub.api.configuration.PlayerConfig
import com.jwplayer.pub.api.configuration.UiConfig
import com.jwplayer.pub.api.configuration.ads.ima.ImaAdvertisingConfig
import com.jwplayer.pub.api.media.ads.AdBreak

class VideoViewModel(
    private val middleware: JwMiddleware
) : ViewModel(), ViewModelProvider.Factory {
    fun getPlayerConfig(media: JwVideo, config: TikTakUiConfig): PlayerConfig {
        val allDisabledUiConfig = UiConfig.Builder().hideAllControls().build()
        return PlayerConfig.Builder()
            .file("https://cdn.jwplayer.com/manifests/${media.mediaid}.m3u8")
            .image("https://cdn.jwplayer.com/v2/media/${media.mediaid}/poster.jpg")
            .stretching(config.getStretching()) // allow the media to fill allotted space as best as possible
            .preload(config.getPreload()) // data hog and may be overkill
            .autostart(true)
            .uiConfig(allDisabledUiConfig)
            .repeat(true)
            .build()
    }

    fun getPlayerAdvertConfig(advertisement: JwAdvertisement, config: TikTakUiConfig): PlayerConfig {
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
        return PlayerConfig.Builder()
            .file("http://content.jwplatform.com/videos/nhYDGoyh-el5vTWpr.mp4") // TODO: how do i setup a config without a file / media and just do an ad?
            .stretching(config.getStretching()) // allow the media to fill allotted space as best as possible
            .advertisingConfig(imaAdvertising)
            .uiConfig(allDisabledUiConfig)
            .build()
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VideoViewModel(middleware) as T
    }
}