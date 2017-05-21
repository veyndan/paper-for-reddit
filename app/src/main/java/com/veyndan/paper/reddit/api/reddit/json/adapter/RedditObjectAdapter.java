package com.veyndan.paper.reddit.api.reddit.json.adapter;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.veyndan.paper.reddit.api.reddit.model.RedditObject;
import com.veyndan.paper.reddit.api.reddit.model.Thing;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

// TODO Reading large comment sections don't work as Json is too deeply nested.
// Potential problems:
// - use of Moshi instance (i.e. variable 'moshi') in RedditObjectAdapter
// - does More.java effect it? Unlikely to have it in small comment sections hence it works?

// TODO Once solved above, squash commits as many redundant ones there.
public final class RedditObjectAdapter extends JsonAdapter<RedditObject> {

    @SuppressWarnings("ReturnOfNull") public static final JsonAdapter.Factory FACTORY = (type, annotations, moshi) -> {
        if (type.equals(RedditObject.class)) {
            return new RedditObjectAdapter(moshi);
        }
        return null;
    };

    // This is similar to context in Gson.
    private final Moshi moshi;

    private RedditObjectAdapter(final Moshi moshi) {
        this.moshi = moshi;
    }

    @Override
    public RedditObject fromJson(final JsonReader reader) throws IOException {
        final Type type = Types.newParameterizedType(Thing.class, Types.newParameterizedType(Map.class, String.class, Object.class));
        final JsonAdapter<Thing<Map<String, Object>>> thingMapAdapter = moshi.adapter(type);
        final Thing<Map<String, Object>> thing = thingMapAdapter.fromJson(reader);
        return moshi.adapter(thing.kind.getDerivedClass()).fromJsonValue(thing.data);
    }

    @Override
    public void toJson(final JsonWriter writer, final RedditObject value) throws IOException {
        throw new UnsupportedOperationException("toJson() not implemented");
    }
}
