package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Json {
    @NonNull public List<Object> errors = new ArrayList<>();
    @Nullable public Things data;
}
