package com.veyndan.redditclient.api.reddit;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.veyndan.redditclient.api.reddit.model.RedditObject;
import com.veyndan.redditclient.api.reddit.model.RedditObjectWrapper;

import java.lang.reflect.Type;

public class RedditObjectDeserializer implements JsonDeserializer<RedditObject> {

    public RedditObject deserialize(final JsonElement json, final Type type, final JsonDeserializationContext context)
            throws JsonParseException {
        if (!json.isJsonObject()) {
            // if there are no replies, we're given a String rather than an object
            return null;
        }
        try {
            final RedditObjectWrapper wrapper = new Gson().fromJson(json, RedditObjectWrapper.class);
            return context.deserialize(wrapper.data, wrapper.kind.getDerivedClass());
        } catch (final JsonParseException e) {
            System.err.println("Failed to deserialize: " + e.getMessage());
            return null;
        }
    }
}
