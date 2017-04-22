package com.veyndan.paper.reddit.api.reddit.model;

import java.util.ArrayList;
import java.util.List;

public class Listing implements RedditObject {

    public String before;
    public String after;
    public String modhash;
    public final List<RedditObject> children = new ArrayList<>();
}
