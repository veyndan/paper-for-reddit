package com.veyndan.paper.reddit.api.reddit.model;

import java.util.ArrayList;
import java.util.List;

public class Trophies implements RedditObject {

    public final List<Thing<Trophy>> trophies = new ArrayList<>();
}
