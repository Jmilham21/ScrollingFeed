package com.jmilham.scrollingfeed.models

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
) {
    data class JwVideoImage(
        var src: String = "",
        var width: Int? = -1,
        var type: String = "",
    )
}

