package com.veyndan.redditclient.api.reddit.model;

import com.google.gson.annotations.SerializedName;

public class Message {
    public String body;
    public Boolean wasComment;
    public Object firstMessage;
    public String name;
    public Object firstMessageName;
    public Integer created;
    public String dest;
    public String author;
    public Integer createdUtc;
    public String bodyHtml;
    public Object subreddit;
    public Object parentId;
    public String context;
    public String replies;
    public String id;
    @SerializedName("new") public Boolean _new;
    public String distinguished;
    public String subject;
}
