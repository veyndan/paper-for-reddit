package com.veyndan.paper.reddit.api.xkcd;

import com.google.gson.TypeAdapterFactory;
import com.ryanharter.auto.value.gson.GsonTypeAdapterFactory;

@GsonTypeAdapterFactory
public abstract class XkcdAdapterFactory implements TypeAdapterFactory {

    public static XkcdAdapterFactory create() {
        return new AutoValueGson_XkcdAdapterFactory();
    }
}
