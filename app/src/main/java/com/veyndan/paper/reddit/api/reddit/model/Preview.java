package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.ArrayList;
import java.util.List;

@AutoValue
public abstract class Preview {

    public static Preview create() {
        return new AutoValue_Preview(new ArrayList<>());
    }

    public abstract List<Image> images();

    public static JsonAdapter<Preview> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Preview.MoshiJsonAdapter(moshi);
    }
}
