package com.veyndan.redditclient.post.model;

import com.veyndan.redditclient.api.reddit.model.RedditObject;
import com.veyndan.redditclient.api.reddit.model.Submission;

import rx.Observable;

public class Post {

    public final Submission submission;

    private Observable<Object> mediaObservable = Observable.empty();

    public Post(final RedditObject redditObject) {
        this.submission = (Submission) redditObject;
    }

    public Observable<Object> getMediaObservable() {
        return mediaObservable;
    }

    public <T> void setMediaObservable(final Observable<T> mediaObservable) {
        this.mediaObservable = mediaObservable.cast(Object.class);
    }
}
