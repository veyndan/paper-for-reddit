package com.veyndan.paper.reddit.deeplink;

import com.airbnb.deeplinkdispatch.DeepLinkSpec;

@DeepLinkSpec(prefix = {
        "http://www.reddit.com",
        "http://reddit.com",
        "https://www.reddit.com",
        "https://reddit.com"
})
public @interface WebDeepLink {
    String[] value();
}
