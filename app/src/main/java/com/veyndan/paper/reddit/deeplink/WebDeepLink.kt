package com.veyndan.paper.reddit.deeplink

import com.airbnb.deeplinkdispatch.DeepLinkSpec

@DeepLinkSpec(prefix = arrayOf(
        "http://www.reddit.com",
        "http://reddit.com",
        "https://www.reddit.com",
        "https://reddit.com")
)
annotation class WebDeepLink(vararg val value: String)
