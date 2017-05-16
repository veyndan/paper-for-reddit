package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class CaptchaData {

    public abstract String iden();

    public static JsonAdapter<CaptchaData> jsonAdapter(final Moshi moshi) {
        return new AutoValue_CaptchaData.MoshiJsonAdapter(moshi);
    }
}
