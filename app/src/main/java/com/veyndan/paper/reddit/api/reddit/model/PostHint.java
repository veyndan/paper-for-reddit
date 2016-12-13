package com.veyndan.paper.reddit.api.reddit.model;

import com.google.gson.annotations.SerializedName;

public enum PostHint {

    /**
     * A self post.
     * <p>
     * #inferred
     */
    @SerializedName("self")SELF,

    /**
     * A video file, like an mp4.
     */
    @SerializedName("video")VIDEO,

    /**
     * An image file, like a gif or png.
     */
    @SerializedName("image")IMAGE,

    /**
     * A video embedded in HTML- like youtube or vimeo.
     */
    @SerializedName("rich:video")RICH_VIDEO,

    /**
     * Catch-all.
     */
    @SerializedName("link")LINK
}
