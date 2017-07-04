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
package com.veyndan.paper.reddit.api.reddit.json.adapter

import com.squareup.moshi.*
import java.lang.reflect.Type

class DefaultOnDataMismatchAdapter<T> private constructor(private val delegate: JsonAdapter<T>, private val defaultValue: T) : JsonAdapter<T>() {

    override fun fromJson(reader: JsonReader): T {
        // Read the value first so that the reader will be in a known state even if there's an
        // exception. Otherwise it may be awkward to recover: it might be between calls to
        // beginObject() and endObject() for example.
        val jsonValue = reader.readJsonValue()

        // Use the delegate to convert the JSON value to the target type.
        return try {
            delegate.fromJsonValue(jsonValue)!!
        } catch (e: JsonDataException) {
            defaultValue
        }
    }

    override fun toJson(writer: JsonWriter?, value: T?) {
        delegate.toJson(writer, value)
    }

    companion object {

        fun <T> newFactory(type: Type, defaultValue: T): JsonAdapter.Factory {
            return object : JsonAdapter.Factory {
                override fun create(requestedType: Type,
                                    annotations: Set<Annotation>,
                                    moshi: Moshi): JsonAdapter<*>? {
                    if (type == requestedType) {
                        val delegate: JsonAdapter<T> = moshi.nextAdapter<T>(this, type, annotations)
                        return DefaultOnDataMismatchAdapter(delegate, defaultValue)
                    }
                    return null
                }
            }
        }
    }
}
