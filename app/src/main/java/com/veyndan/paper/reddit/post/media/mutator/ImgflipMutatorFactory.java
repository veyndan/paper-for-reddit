package com.veyndan.paper.reddit.post.media.mutator;

import android.support.annotation.NonNull;

import com.veyndan.paper.reddit.post.media.model.Image;
import com.veyndan.paper.reddit.post.model.Post;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Maybe;
import io.reactivex.Single;

final class ImgflipMutatorFactory implements MutatorFactory {

    @NonNull private static final Pattern PATTERN = Pattern.compile("^https?://(?:www\\.)?imgflip\\.com/i/(.*)$");

    @NonNull
    static ImgflipMutatorFactory create() {
        return new ImgflipMutatorFactory();
    }

    private ImgflipMutatorFactory() {
    }

    @NonNull
    @Override
    public Maybe<Post> mutate(@NonNull final Post post) {
        final Matcher matcher = PATTERN.matcher(post.getLinkUrl());

        return Single.just(post)
                .filter(post1 -> matcher.matches())
                .map(post1 -> {
                    final String directImageUrl = "https://i.imgflip.com/" + matcher.group(1) + ".jpg";
                    post1.getMedias().add(new Image(directImageUrl));
                    return post1;
                });
    }
}
