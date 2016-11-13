package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class Message {

    public abstract String body();

    public abstract Boolean wasComment();

    public abstract Object firstMessage();

    public abstract String name();

    public abstract Object firstMessageName();

    public abstract Integer created();

    public abstract String dest();

    public abstract String author();

    public abstract Integer createdUtc();

    public abstract String bodyHtml();

    public abstract Object subreddit();

    public abstract Object parentId();

    public abstract String context();

    public abstract String replies();

    public abstract String id();

    @SerializedName("new")
    public abstract Boolean _new();

    public abstract String distinguished();

    public abstract String subject();

    public static TypeAdapter<Message> typeAdapter(final Gson gson) {
        return new AutoValue_Message.GsonTypeAdapter(gson);
    }
}
