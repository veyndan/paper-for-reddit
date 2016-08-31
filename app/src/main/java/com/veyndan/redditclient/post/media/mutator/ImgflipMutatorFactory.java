package com.veyndan.redditclient.post.media.mutator;

import com.veyndan.redditclient.post.model.Post;
import com.veyndan.redditclient.post.media.model.Image;

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
    public Observable<Post> mutate(final Post post) {
        final Matcher matcher = PATTERN.matcher(post.getLinkUrl());

        return Observable.just(post)
                .filter(post1 -> matcher.matches())
                .map(post1 -> {
                    final String directImageUrl = "https://i.imgflip.com/" + matcher.group(1) + ".jpg";
                    post1.setMediaObservable(Observable.just(new Image(directImageUrl)));
                    return post1;
                });
    }
}
