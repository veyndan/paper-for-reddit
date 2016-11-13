package com.veyndan.paper.reddit.api.reddit;

import com.google.gson.TypeAdapterFactory;
import com.ryanharter.auto.value.gson.GsonTypeAdapterFactory;
import com.veyndan.paper.reddit.api.imgur.AutoValueGson_ImgurAdapterFactory;
import com.veyndan.paper.reddit.api.imgur.ImgurAdapterFactory;

@GsonTypeAdapterFactory
public abstract class RedditAdapterFactory implements TypeAdapterFactory {

    public static ImgurAdapterFactory create() {
        return new AutoValueGson_ImgurAdapterFactory();
    }
}
