package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class Message {
    @Nullable public String body;
    @Nullable public Boolean wasComment;
    @Nullable public Object firstMessage;
    @Nullable public String name;
    @Nullable public Object firstMessageName;
    @Nullable public Integer created;
    @Nullable public String dest;
    @Nullable public String author;
    @Nullable public Integer createdUtc;
    @Nullable public String bodyHtml;
    @Nullable public Object subreddit;
    @Nullable public Object parentId;
    @Nullable public String context;
    @Nullable public String replies;
    @Nullable public String id;
    @Nullable @SerializedName("new") public Boolean _new;
    @Nullable public String distinguished;
    @Nullable public String subject;
}
