package com.veyndan.redditclient.post.mutator;

import com.veyndan.redditclient.post.model.Post;
import com.veyndan.redditclient.post.model.media.Image;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;

final class ImgflipMutatorFactory implements MutatorFactory {

    private static final Pattern PATTERN = Pattern.compile("^https?://(?:www\\.)?imgflip\\.com/i/(.*)$");

    static ImgflipMutatorFactory create() {
        return new ImgflipMutatorFactory();
    }

    private ImgflipMutatorFactory() {
    }

    @Override
    public boolean mutate(final Post post) {
        final Matcher matcher = PATTERN.matcher(post.submission.linkUrl);

        if (matcher.matches()) {
            final String directImageUrl = "https://i.imgflip.com/" + matcher.group(1) + ".jpg";
            post.setMediaObservable(Observable.just(new Image(directImageUrl)));
            return true;
        }

        return false;
    }
}
