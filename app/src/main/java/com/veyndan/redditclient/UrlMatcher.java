package com.veyndan.redditclient;

import com.google.common.base.Optional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UrlMatcher {

    public static final class Twitter {

        /**
         * Returns the tweetId of a url in the form of
         * https://twitter.com/{username}/status/{tweetId}.
         */
        public static Optional<Long> tweetId(final String url) {
            final Pattern pattern = Pattern.compile("https://twitter.com/\\w*/status/(\\d+)$");
            final Matcher matcher = pattern.matcher(url);

            return matcher.find()
                    ? Optional.of(Long.parseLong(matcher.group(1)))
                    : Optional.absent();
        }
    }
}
