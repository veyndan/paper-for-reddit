package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class Message implements Created, RedditObject {

    public abstract String body();

    @Json(name = "was_comment")
    public abstract Boolean wasComment();

    @Json(name = "first_message")
    public abstract Object firstMessage();

    public abstract String name();

    @Json(name = "first_message_name")
    public abstract Object firstMessageName();

    public abstract String dest();

    public abstract String author();

    @Json(name = "body_html")
    public abstract String bodyHtml();

    public abstract Object subreddit();

    @Json(name = "parent_id")
    public abstract Object parentId();

    public abstract String context();

    public abstract String replies();

    public abstract String id();

    @Json(name = "new")
    public abstract Boolean _new();

    public abstract String distinguished();

    public abstract String subject();

    public static JsonAdapter<Message> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Message.MoshiJsonAdapter(moshi);
    }
}
