package com.veyndan.redditclient.api.reddit.model;

import java.util.ArrayList;
import java.util.List;

public class Image {
    public final Source source = new Source();
    public List<Source> resolutions = new ArrayList<>();
    public Object variants;
    public String id;
}
