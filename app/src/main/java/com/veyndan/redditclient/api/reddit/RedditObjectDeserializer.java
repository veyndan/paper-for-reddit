package com.veyndan.redditclient.api.reddit;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.veyndan.redditclient.api.reddit.model.Comment;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.RedditObject;
import com.veyndan.redditclient.api.reddit.model.Thing;

import java.lang.reflect.Type;

public class RedditObjectDeserializer implements JsonDeserializer<RedditObject> {

    @Override
    public RedditObject deserialize(final JsonElement json, final Type type, final JsonDeserializationContext context)
            throws JsonParseException {
        if (!json.isJsonObject()) {
            // if there are no replies, we're given a String rather than an object
            return null;
        }
        try {
            final Thing<JsonObject> thing = new Gson().fromJson(json, new TypeToken<Thing<JsonObject>>() {}.getType());

            if (thing.kind.getDerivedClass().equals(Comment.class)) {
                // If there are no replies, instead of returning an empty object or null, an empty
                // string is returned. This sets an empty object if empty string.
                if (thing.data.get("replies").isJsonPrimitive()) {
                    final Thing<Listing> thing1 = new Thing<>();
                    thing1.data = new Listing();
                    thing.data.add("replies", new Gson().toJsonTree(thing1));
                }
            }

            return context.deserialize(thing.data, thing.kind.getDerivedClass());
        } catch (final JsonParseException e) {
            System.err.println("Failed to deserialize: " + e.getMessage());
            return null;
        }
    }
}
