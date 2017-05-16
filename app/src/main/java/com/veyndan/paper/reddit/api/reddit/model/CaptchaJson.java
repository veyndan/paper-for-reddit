package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

@AutoValue
public abstract class CaptchaJson {

    public abstract List<Object> errors();

    public abstract CaptchaData data();

    public static JsonAdapter<CaptchaJson> jsonAdapter(final Moshi moshi) {
        return new AutoValue_CaptchaJson.MoshiJsonAdapter(moshi);
    }
}
