package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class CaptchaNew {

    public abstract CaptchaJson json();

    public static JsonAdapter<CaptchaNew> jsonAdapter(final Moshi moshi) {
        return new AutoValue_CaptchaNew.MoshiJsonAdapter(moshi);
    }
}
