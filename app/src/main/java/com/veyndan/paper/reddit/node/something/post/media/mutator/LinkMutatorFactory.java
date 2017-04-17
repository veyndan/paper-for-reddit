package com.veyndan.paper.reddit.node.something.post.media.mutator;

import com.veyndan.paper.reddit.api.reddit.model.PostHint;
import com.veyndan.paper.reddit.node.something.post.media.model.Link;
import com.veyndan.paper.reddit.node.something.post.Post;

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
                .filter(post1 -> post1.getPostHint() != PostHint.SELF)
                .map(post1 -> {
                    post1.getMedias().add(Link.create(post1.getDomain()));
                    return post1;
                });
    }
}
