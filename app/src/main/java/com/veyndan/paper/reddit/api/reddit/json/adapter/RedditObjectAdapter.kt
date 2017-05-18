package com.veyndan.paper.reddit.api.reddit.json.adapter

import com.squareup.moshi.*
import com.veyndan.paper.reddit.api.reddit.model.RedditObject
import com.veyndan.paper.reddit.api.reddit.model.Thing

// TODO Reading large comment sections don't work as Json is too deeply nested.
// Potential problems:
// - use of Moshi instance (i.e. variable 'moshi') in RedditObjectAdapter
// - does More.java effect it? Unlikely to have it in small comment sections hence it works?
class RedditObjectAdapter private constructor(private val moshi: Moshi) : JsonAdapter<RedditObject>() {

    companion object {

        val FACTORY: JsonAdapter.Factory = JsonAdapter.Factory { type, _, moshi ->
            if (type == RedditObject::class.java) RedditObjectAdapter(moshi) else null
        }
    }

    override fun fromJson(reader: JsonReader): RedditObject {
        val type = Types.newParameterizedType(Thing::class.java, Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java))
        val thingMapAdapter = moshi.adapter<Thing<Map<String, Any>>>(type)
        val thing = thingMapAdapter.fromJson(reader)
        return moshi.adapter<RedditObject>(thing.kind!!.derivedClass).fromJsonValue(thing.data)
    }

    override fun toJson(writer: JsonWriter, value: RedditObject) {
        throw UnsupportedOperationException("toJson() not implemented")
    }
}
