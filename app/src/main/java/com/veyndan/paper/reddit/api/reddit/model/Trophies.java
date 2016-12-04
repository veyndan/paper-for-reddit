package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Trophies {
    @NonNull public final List<Thing<Trophy>> trophies = new ArrayList<>();
}
