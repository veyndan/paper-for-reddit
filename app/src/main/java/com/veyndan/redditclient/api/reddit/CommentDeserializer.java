package com.veyndan.redditclient.api.reddit;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.veyndan.redditclient.api.reddit.model.Comment;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.Thing;

import java.lang.reflect.Type;

public class CommentDeserializer implements JsonDeserializer<Comment> {

    @Override
    public Comment deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        final Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        final JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.get("replies").isJsonPrimitive()) {
            final Thing<Listing> thing = new Thing<>();
            thing.data = new Listing();
            jsonObject.add("replies", gson.toJsonTree(thing));
        }

        return gson.fromJson(jsonObject, typeOfT);
    }
}
