package com.jmilham21.scrollingfeed.data

/**
 * Data class that drives the main view type in [com.jmilham21.scrollingfeed.view.adapters.VideoFragmentAdapter]
 * Only [JwVideo.title], [JwVideo.link], [JwVideo.title], [JwVideo.image] are currently driving the view
 *
 * @property title as defined in the JW Delivery API. Shown on view
 * @property mediaid as defined in the JW Delivery API
 * @property link as defined in the JW Delivery API
 * @property image as defined in the JW Delivery API
 * @property images as defined in the JW Delivery API
 * @property feedid as defined in the JW Delivery API
 * @property duration as defined in the JW Delivery API
 * @property pubdate as defined in the JW Delivery API
 * @property description as defined in the JW Delivery API
 * @property tags as defined in the JW Delivery API
 * @property skip_to as defined in the JW Delivery API
 *
 */
data class JwVideo(
    var title: String = "",
    var mediaid: String = "",
    var link: String = "",
    var image: String = "",
    var images: ArrayList<JwVideoImage> = ArrayList(),
    var feedid: String = "",
    var duration: Int? = -1,
    var pubdate: Int? = -1,
    var description: String = "",
    var tags: String = "",
    var skip_to: String = "",
) : JwMedia() {
    /**
     * Internal Image class for [JwMedia].
     * Used to parse the Thumbnail from Jw Delivery API and display
     * as a place holder in [com.jmilham21.scrollingfeed.view.adapters.VideoFragmentAdapter]
     *
     * @property src Image URL from Jw delivery API.
     * @property width Width (in px) from the JW Delivery API
     * @property type The mime type for the image
     */
    data class JwVideoImage(
        var src: String = "",
        var width: Int? = -1,
        var type: String = "",
    )
}

