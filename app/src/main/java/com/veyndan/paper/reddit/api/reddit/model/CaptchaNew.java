package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class CaptchaNew {

    public abstract CaptchaJson json();

    public static TypeAdapter<CaptchaNew> typeAdapter(final Gson gson) {
        return new AutoValue_CaptchaNew.GsonTypeAdapter(gson);
    }
}
