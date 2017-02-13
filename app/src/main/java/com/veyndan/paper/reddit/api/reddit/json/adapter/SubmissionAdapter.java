package com.veyndan.paper.reddit.api.reddit.json.adapter;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

// The idea is to cast the Submission to Post, so even though it is still a Submission as the result of the API,
// it can be upcasted to being a Post.
public final class SubmissionAdapter extends JsonAdapter<List<?>> {

    public static final JsonAdapter.Factory FACTORY = new Factory() {
        @Override
        public JsonAdapter<?> create(final Type type, final Set<? extends Annotation> annotations,
                                     final Moshi moshi) {

            Timber.d("%s\n%s\n%s", type, Types.getRawType(type), Arrays.toString(Types.getRawType(type).getInterfaces()));
            if (Types.getRawType(type).equals(List.class)) {
                Timber.d("create()");
                Timber.d(Types.getRawType(type).toString());
                return new SubmissionAdapter(moshi.adapter(type, annotations));
            }
            return null;
        }
    };

    private final JsonAdapter<List<?>> delegate;

    private SubmissionAdapter(final JsonAdapter<List<?>> delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<?> fromJson(final JsonReader reader) throws IOException {
        Timber.d("fromJson()");
        return delegate.fromJson(reader);
    }

    @Override
    public void toJson(final JsonWriter writer, final List<?> value) throws IOException {
        throw new UnsupportedOperationException("toJson() not implemented");
    }
}
