package com.veyndan.paper.reddit.api.imgur;

import com.google.gson.TypeAdapterFactory;
import com.ryanharter.auto.value.gson.GsonTypeAdapterFactory;

@GsonTypeAdapterFactory
public abstract class ImgurAdapterFactory implements TypeAdapterFactory {

    public static ImgurAdapterFactory create() {
        return new AutoValueGson_ImgurAdapterFactory();
    }
}
