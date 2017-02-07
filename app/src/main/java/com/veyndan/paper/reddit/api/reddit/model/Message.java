package com.veyndan.paper.reddit.api.reddit.model;

import com.squareup.moshi.Json;

public class Message extends RedditObject {
    public String body;
    @Json(name = "was_comment") public Boolean wasComment;
    @Json(name = "first_message") public Object firstMessage;
    public String name;
    @Json(name = "first_message_name") public Object firstMessageName;
    public Integer created;
    public String dest;
    public String author;
    @Json(name = "created_utc") public Integer createdUtc;
    @Json(name = "body_html") public String bodyHtml;
    public Object subreddit;
    @Json(name = "parent_id") public Object parentId;
    public String context;
    public String replies;
    public String id;
    @Json(name = "new") public Boolean _new;
    public String distinguished;
    public String subject;
}
