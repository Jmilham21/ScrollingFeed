package com.jmilham21.scrollingfeed.middleware

import okhttp3.Headers

class MiddlewareConfig(
    middlewareUrl: String? = null,
    likesEndpoint: Boolean = true,
    val headers: Headers = Headers.Builder().build() // This may need to vary on the call rather than blanket headers
) {
}