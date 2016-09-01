package com.veyndan.redditclient.post.media.mutator;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.veyndan.redditclient.post.model.Post;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;

final class TwitterMutatorFactory implements MutatorFactory {

    private static final Pattern PATTERN = Pattern.compile("^https?://(?:www\\.)?twitter\\.com/\\w*/status/(\\d+)\\??.*$");

    static TwitterMutatorFactory create() {
        return new TwitterMutatorFactory();
    }

    private TwitterMutatorFactory() {
    }

    @Override
    public Observable<Post> mutate(final Post post) {
        final Matcher matcher = PATTERN.matcher(post.getLinkUrl());

        return Observable.just(post)
                .filter(post1 -> post1.isLink() && matcher.matches())
                .map(post1 -> {
                    final Long tweetId = Long.parseLong(matcher.group(1));
                    // TODO Replace Observable.create with an Observable returned by Retrofit.
                    post1.setMediaObservable(Observable.create(subscriber -> {
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
                    return post1;
                });
    }
}
