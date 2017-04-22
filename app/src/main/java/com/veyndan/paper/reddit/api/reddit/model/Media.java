package com.veyndan.paper.reddit.api.reddit.model;

import com.squareup.moshi.Json;

public class Media {

    @Json(name = "o_embed") public OEmbed oEmbed;
    public String type;
}
