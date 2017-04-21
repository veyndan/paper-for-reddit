package com.veyndan.paper.reddit.post.media.mutator;

import com.veyndan.paper.reddit.api.reddit.model.PostHint;
import com.veyndan.paper.reddit.post.media.model.Link;
import com.veyndan.paper.reddit.post.model.Post;

import io.reactivex.Maybe;
import io.reactivex.Single;

final class LinkMutatorFactory implements MutatorFactory {

    static LinkMutatorFactory create() {
        return new LinkMutatorFactory();
    }

    private LinkMutatorFactory() {
    }

    @Override
    public Maybe<Post> mutate(final Post post) {
        return Single.just(post)
                .filter(post1 -> post1.postHint().value != PostHint.SELF)
                .map(post1 -> {
                    post1.medias().add(Link.create(post1.domain()));
                    return post1;
                });
    }
}
