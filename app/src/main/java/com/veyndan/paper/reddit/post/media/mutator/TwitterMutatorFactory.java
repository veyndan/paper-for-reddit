package com.veyndan.paper.reddit.post.media.mutator;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.veyndan.paper.reddit.BuildConfig;
import com.veyndan.paper.reddit.post.model.Post;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Maybe;
import io.reactivex.Single;

final class TwitterMutatorFactory implements MutatorFactory {

    private static final Pattern PATTERN = Pattern.compile("^https?://(?:www\\.)?twitter\\.com/\\w*/status/(\\d+)\\??.*$");

    static TwitterMutatorFactory create() {
        return new TwitterMutatorFactory();
    }

    private TwitterMutatorFactory() {
    }

    @Override
    public Maybe<Post> mutate(final Post post) {
        final Matcher matcher = PATTERN.matcher(post.linkUrl().value);

        return Single.just(post)
                .filter(post1 -> BuildConfig.HAS_TWITTER_API_CREDENTIALS && matcher.matches())
                .map(post1 -> {
                    final Long tweetId = Long.parseLong(matcher.group(1));
                    // TODO Replace Observable.create with an Observable returned by Retrofit.
                    final Single<Tweet> tweet = Single.create(subscriber -> {
                        TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
                            @Override
                            public void success(final Result<Tweet> result) {
                                subscriber.onSuccess(result.data);
                            }

                            @Override
                            public void failure(final TwitterException exception) {
                                subscriber.onError(exception);
                            }
                        });
                    });

                    return post1.withMedias(post1.medias().value.concatWith(tweet.toObservable()));
                });
    }
}
