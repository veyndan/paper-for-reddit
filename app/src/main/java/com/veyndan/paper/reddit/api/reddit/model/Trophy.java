package com.veyndan.paper.reddit.api.reddit.model;

import com.squareup.moshi.Json;

public class Trophy {
    @Json(name = "icon_70") public String icon70;
    public String description;
    public String url;
    @Json(name = "icon_40") public String icon40;
    @Json(name = "award_id") public String awardId;
    public String id;
    public String name;
}
