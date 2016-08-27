package com.veyndan.redditclient.post.model;

import com.veyndan.redditclient.api.reddit.model.RedditObject;
import com.veyndan.redditclient.api.reddit.model.Submission;

public class Post {

    public final Submission submission;

    public Post(final RedditObject redditObject) {
        this.submission = (Submission) redditObject;
    }
}
