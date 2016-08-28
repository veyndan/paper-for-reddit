package com.veyndan.redditclient.post.model;

import com.veyndan.redditclient.api.reddit.model.RedditObject;
import com.veyndan.redditclient.api.reddit.model.Submission;
import com.veyndan.redditclient.post.model.media.Image;

import rx.Observable;

public class Post {

    public final Submission submission;

    private Observable<Image> imageObservable;

    public Post(final RedditObject redditObject) {
        this.submission = (Submission) redditObject;
    }

    public Observable<Image> getImageObservable() {
        return imageObservable;
    }

    public void setImageObservable(final Observable<Image> imageObservable) {
        this.imageObservable = imageObservable;
    }
}
