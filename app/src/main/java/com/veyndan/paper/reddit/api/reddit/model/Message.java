package com.veyndan.paper.reddit.api.reddit.model;

import com.squareup.moshi.Json;

public class Message extends RedditObject implements Created {

    private long created;
    @Json(name = "created_utc") private long createdUtc;
    public String body;
    @Json(name = "was_comment") public Boolean wasComment;
    @Json(name = "first_message") public Object firstMessage;
    public String name;
    @Json(name = "first_message_name") public Object firstMessageName;
    public String dest;
    public String author;
    @Json(name = "body_html") public String bodyHtml;
    public Object subreddit;
    @Json(name = "parent_id") public Object parentId;
    public String context;
    public String replies;
    public String id;
    @Json(name = "new") public Boolean _new;
    public String distinguished;
    public String subject;

    @Override
    public long getCreated() {
        return created;
    }

    @Override
    public long getCreatedUtc() {
        return createdUtc;
    }
}
