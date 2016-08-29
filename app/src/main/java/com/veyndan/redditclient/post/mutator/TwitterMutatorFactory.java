package com.veyndan.redditclient.post.mutator;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.veyndan.redditclient.api.reddit.model.Link;
import com.veyndan.redditclient.post.model.Post;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;

final class TwitterMutatorFactory implements MutatorFactory {

    private final Pattern pattern = Pattern.compile("^https?://(www\\.)?twitter\\.com/\\w*/status/(\\d+)\\?.*$");

    static TwitterMutatorFactory create() {
        return new TwitterMutatorFactory();
    }

    private TwitterMutatorFactory() {
    }

    @Override
    public boolean applicable(final Post post) {
        final Matcher matcher = pattern.matcher(post.submission.linkUrl);
        return post.submission instanceof Link && matcher.matches();
    }

    @Override
    public void mutate(final Post post) {
        final Matcher matcher = pattern.matcher(post.submission.linkUrl);
        if (matcher.matches()) {
            final Long tweetId = Long.parseLong(matcher.group(2));
            // TODO Replace Observable.create with an Observable returned by Retrofit.
            post.setMediaObservable(Observable.create(subscriber -> {
                TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
                    @Override
                    public void success(final Result<Tweet> result) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(result.data);
                            subscriber.onCompleted();
                        }
                    }

                    @Override
                    public void failure(final TwitterException exception) {
                        subscriber.onError(exception);
                    }
                });
            }));
        }
    }
}
