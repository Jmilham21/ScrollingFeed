package com.jmilham21.scrollingfeed.data

import com.jwplayer.pub.api.configuration.ads.ima.ImaAdvertisingConfig

/**
 * Data class intended to be used drive an Advertising based view in the ViewPager
 *
 * Inherits from [JwVideo] to be used in [com.jmilham21.scrollingfeed.view.adapters.VideoFragmentAdapter]
 *
 *
 * @property adUrl is the ad URL (currently only support [ImaAdvertisingConfig] tags
 */
data class JwAdvertisement(
    var adUrl: String = ""
) : JwMedia() {

}