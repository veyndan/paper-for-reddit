package com.veyndan.paper.reddit.api.reddit.model;

import com.squareup.moshi.Json;

public class OEmbed {

    @Json(name = "provider_url") public String providerUrl;
    public String description;
    public String title;
    public String type;
    @Json(name = "thumbnail_width") public int thumbnailWidth;
    public int height;
    public int width;
    public String html;
    public String version;
    @Json(name = "provider_name") public String providerName;
    @Json(name = "thumbnail_url") public String thumbnailUrl;
    @Json(name = "thumbnail_height") public int thumbnailHeight;
}
