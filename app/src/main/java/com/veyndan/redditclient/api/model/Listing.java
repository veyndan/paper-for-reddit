package com.veyndan.redditclient.api.model;

import java.util.ArrayList;
import java.util.List;

public class Listing {
    public String before;
    public String after;
    public String modhash;
    public List<RedditObject> children = new ArrayList<>();
}
