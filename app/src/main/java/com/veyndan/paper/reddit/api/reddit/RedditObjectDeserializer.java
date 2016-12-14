package com.veyndan.paper.reddit.api.reddit;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.veyndan.paper.reddit.api.reddit.model.Comment;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.RedditObject;
import com.veyndan.paper.reddit.api.reddit.model.Thing;

import java.lang.reflect.Type;

public class RedditObjectDeserializer implements JsonDeserializer<RedditObject> {

    @NonNull
    @Override
    public RedditObject deserialize(@NonNull final JsonElement json, @NonNull final Type type,
                                    @NonNull final JsonDeserializationContext context)
            throws JsonParseException {
        if (!json.isJsonObject()) {
            // if there are no replies, we're given a String rather than an object
            return null;
        }
        try {
            final Thing<JsonObject> thing = new Gson().fromJson(json, new TypeToken<Thing<JsonObject>>() {}.getType());

            if (thing.getKind().getDerivedClass().equals(Comment.class)) {
                // If there are no replies, instead of returning an empty object or null, an empty
                // string is returned. This sets an empty object if empty string.
                if (thing.getData().get("replies").isJsonPrimitive()) {
                    final Thing<Listing> thing1 = new Thing<>(new Listing());
                    thing.getData().add("replies", new Gson().toJsonTree(thing1));
                }
            }

            return context.deserialize(thing.getData(), thing.getKind().getDerivedClass());
        } catch (final JsonParseException e) {
            System.err.println("Failed to deserialize: " + e.getMessage());
            return null;
        }
    }
}
