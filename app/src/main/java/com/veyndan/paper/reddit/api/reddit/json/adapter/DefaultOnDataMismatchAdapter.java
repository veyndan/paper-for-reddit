/*
 * Copyright (C) 2017 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.veyndan.paper.reddit.api.reddit.json.adapter;

import android.support.annotation.Nullable;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * Copy of https://github.com/square/moshi/blob/master/examples/src/main/java/com/squareup/moshi/recipes/DefaultOnDataMismatchAdapter.java
 * <p>
 * All changes are cosmetic.
 */
public final class DefaultOnDataMismatchAdapter<T> extends JsonAdapter<T> {

    private final JsonAdapter<T> delegate;
    private final T defaultValue;

    private DefaultOnDataMismatchAdapter(final JsonAdapter<T> delegate, final T defaultValue) {
        this.delegate = delegate;
        this.defaultValue = defaultValue;
    }

    @Override
    public T fromJson(final JsonReader reader) throws IOException {
        // Read the value first so that the reader will be in a known state even if there's an
        // exception. Otherwise it may be awkward to recover: it might be between calls to
        // beginObject() and endObject() for example.
        final Object jsonValue = reader.readJsonValue();

        // Use the delegate to convert the JSON value to the target type.
        try {
            return delegate.fromJsonValue(jsonValue);
        } catch (final JsonDataException e) {
            return defaultValue;
        }
    }

    @Override
    public void toJson(final JsonWriter writer, final T value) throws IOException {
        delegate.toJson(writer, value);
    }

    public static <T> Factory newFactory(final Type type, final T defaultValue) {
        return new JsonAdapter.Factory() {
            @Nullable
            @Override
            public JsonAdapter<?> create(final Type requestedType,
                                         final Set<? extends Annotation> annotations,
                                         final Moshi moshi) {
                if (type.equals(requestedType)) {
                    final JsonAdapter<T> delegate = moshi.nextAdapter(this, type, annotations);
                    return new DefaultOnDataMismatchAdapter<>(delegate, defaultValue);
                }
                return null;
            }
        };
    }
}
