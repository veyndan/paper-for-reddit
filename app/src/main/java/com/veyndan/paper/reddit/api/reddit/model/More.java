package com.veyndan.paper.reddit.api.reddit.model;

import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;

public class More extends RedditObject {
    public int count;
    @Json(name = "parent_id") public String parentId;
    public String id;
    public String name;
    public List<String> children = new ArrayList<>();
}
