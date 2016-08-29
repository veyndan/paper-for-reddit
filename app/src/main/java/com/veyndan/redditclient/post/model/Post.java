package com.veyndan.redditclient.post.model;

import com.twitter.sdk.android.core.models.Tweet;
import com.veyndan.redditclient.api.reddit.model.RedditObject;
import com.veyndan.redditclient.api.reddit.model.Submission;
import com.veyndan.redditclient.post.model.media.Image;
import com.veyndan.redditclient.post.model.media.Link;
import com.veyndan.redditclient.post.model.media.LinkImage;
import com.veyndan.redditclient.post.model.media.Text;

import rx.Observable;

public class Post {

    public final Submission submission;

    private Observable<Object> textObservable = Observable.empty();
    private Observable<Object> linkObservable = Observable.empty();
    private Observable<Object> linkImageObservable = Observable.empty();
    private Observable<Object> imageObservable = Observable.empty();
    private Observable<Object> tweetObservable = Observable.empty();

    public Post(final RedditObject redditObject) {
        this.submission = (Submission) redditObject;
    }

    public Observable<Object> getTextObservable() {
        return textObservable;
    }

    public void setTextObservable(final Observable<Text> textObservable) {
        this.textObservable = textObservable.cast(Object.class);
    }

    public Observable<Object> getLinkObservable() {
        return linkObservable;
    }

    public void setLinkObservable(final Observable<Link> linkObservable) {
        this.linkObservable = linkObservable.cast(Object.class);
    }

    public Observable<Object> getLinkImageObservable() {
        return linkImageObservable;
    }

    public void setLinkImageObservable(final Observable<LinkImage> linkImageObservable) {
        this.linkImageObservable = linkImageObservable.cast(Object.class);
    }

    public Observable<Object> getImageObservable() {
        return imageObservable;
    }

    public void setImageObservable(final Observable<Image> imageObservable) {
        this.imageObservable = imageObservable.cast(Object.class);
    }

    public Observable<Object> getTweetObservable() {
        return tweetObservable;
    }

    public void setTweetObservable(final Observable<Tweet> tweetObservable) {
        this.tweetObservable = tweetObservable.cast(Object.class);
    }
}
