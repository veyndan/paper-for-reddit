package com.veyndan.paper.reddit.api.reddit.model;

import com.squareup.moshi.Json;

public class Karma {
    public String sr;
    @Json(name = "comment_karma") public int commentKarma;
    @Json(name = "link_karma") public int linkKarma;
}
