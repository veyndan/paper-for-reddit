package com.veyndan.paper.reddit.node.something.post.media.mutator;

import com.veyndan.paper.reddit.node.something.post.media.model.Image;
import com.veyndan.paper.reddit.node.something.post.Post;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Maybe;
import io.reactivex.Single;

final class ImgflipMutatorFactory implements MutatorFactory {

    private static final Pattern PATTERN = Pattern.compile("^https?://(?:www\\.)?imgflip\\.com/i/(.*)$");

    static ImgflipMutatorFactory create() {
        return new ImgflipMutatorFactory();
    }

    private ImgflipMutatorFactory() {
    }

    @Override
    public Maybe<Post> mutate(final Post post) {
        final Matcher matcher = PATTERN.matcher(post.getLinkUrl());

        return Single.just(post)
                .filter(post1 -> matcher.matches())
                .map(post1 -> {
                    final String directImageUrl = "https://i.imgflip.com/" + matcher.group(1) + ".jpg";
                    post1.getMedias().add(Image.create(directImageUrl));
                    return post1;
                });
    }
}
