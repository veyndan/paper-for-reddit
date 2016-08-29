package com.veyndan.redditclient.post.model;

import com.twitter.sdk.android.core.models.Tweet;
import com.veyndan.redditclient.api.reddit.model.RedditObject;
import com.veyndan.redditclient.api.reddit.model.Submission;
import com.veyndan.redditclient.post.model.media.Image;
import com.veyndan.redditclient.post.model.media.Link;
import com.veyndan.redditclient.post.model.media.LinkImage;

import rx.Observable;

public class Post {

    public final Submission submission;

    private Observable<Link> linkObservable;
    private Observable<LinkImage> linkImageObservable;
    private Observable<Image> imageObservable;
    private Observable<Tweet> tweetObservable;

    public Post(final RedditObject redditObject) {
        this.submission = (Submission) redditObject;
    }

    public Observable<Link> getLinkObservable() {
        return linkObservable;
    }

    public void setLinkObservable(final Observable<Link> linkObservable) {
        this.linkObservable = linkObservable;
    }

    public Observable<LinkImage> getLinkImageObservable() {
        return linkImageObservable;
    }

    public void setLinkImageObservable(final Observable<LinkImage> linkImageObservable) {
        this.linkImageObservable = linkImageObservable;
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
