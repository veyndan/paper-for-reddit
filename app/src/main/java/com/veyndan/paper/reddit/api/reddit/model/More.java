package com.veyndan.paper.reddit.api.reddit.model;

import java.util.ArrayList;
import java.util.List;

public class More implements RedditObject {

    public int count;
    public String id;
    public String name;
    public List<String> children = new ArrayList<>();
}
