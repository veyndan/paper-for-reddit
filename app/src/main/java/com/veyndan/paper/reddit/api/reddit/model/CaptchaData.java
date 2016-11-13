package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class CaptchaData {

    public abstract String iden();

    public static TypeAdapter<CaptchaData> typeAdapter(final Gson gson) {
        return new AutoValue_CaptchaData.GsonTypeAdapter(gson);
    }
}
