package com.veyndan.paper.reddit.api.reddit.model;

import com.squareup.moshi.Json;

public enum PostHint {

    /**
     * A self post.
     * <p>
     * #inferred
     */
    @Json(name = "self")SELF,

    /**
     * A video file, like an mp4.
     */
    @Json(name = "video")VIDEO,

    /**
     * An image file, like a gif or png.
     */
    @Json(name = "image")IMAGE,

    /**
     * A video embedded in HTML- like youtube or vimeo.
     */
    @Json(name = "rich:video")RICH_VIDEO,

    /**
     * Catch-all.
     */
    @Json(name = "link")LINK
}
