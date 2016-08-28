package com.veyndan.redditclient.post.model;

import com.twitter.sdk.android.core.models.Tweet;
import com.veyndan.redditclient.api.reddit.model.RedditObject;
import com.veyndan.redditclient.api.reddit.model.Submission;
import com.veyndan.redditclient.post.model.media.Image;

import rx.Observable;

public class Post {

    public final Submission submission;

    private Observable<Image> imageObservable;
    private Observable<Tweet> tweetObservable;

    public Post(final RedditObject redditObject) {
        this.submission = (Submission) redditObject;
    }

    public Observable<Image> getImageObservable() {
        return imageObservable;
    }

    public void setImageObservable(final Observable<Image> imageObservable) {
        this.imageObservable = imageObservable;
    }

    public Observable<Tweet> getTweetObservable() {
        return tweetObservable;
    }

    public void setTweetObservable(final Observable<Tweet> tweetObservable) {
        this.tweetObservable = tweetObservable;
    }
}
