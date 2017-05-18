package com.veyndan.paper.reddit.post.media.mutator;

import com.veyndan.paper.reddit.post.media.model.Image;
import com.veyndan.paper.reddit.post.model.Post;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

final class ImgflipMutatorFactory implements MutatorFactory {

    private static final Pattern PATTERN = Pattern.compile("^https?://(?:www\\.)?imgflip\\.com/i/(.*)$");

    @Override
    public Maybe<Post> mutate(final Post post) {
        final Matcher matcher = PATTERN.matcher(post.linkUrl());

        return Single.just(post)
                .filter(post1 -> matcher.matches())
                .map(post1 -> {
                    final String directImageUrl = "https://i.imgflip.com/" + matcher.group(1) + ".jpg";
                    final Image image = Image.create(directImageUrl);
                    return post1.withMedias(post1.medias().value.concatWith(Observable.just(image)));
                });
    }
}
