package com.veyndan.paper.reddit.tree.media.mutator;

import com.veyndan.paper.reddit.api.reddit.model.PostHint;
import com.veyndan.paper.reddit.tree.model.Post;
import com.veyndan.paper.reddit.tree.media.model.Link;

import rx.Observable;

final class LinkMutatorFactory implements MutatorFactory {

    static LinkMutatorFactory create() {
        return new LinkMutatorFactory();
    }

    private LinkMutatorFactory() {
    }

    @Override
    public Observable<Post> mutate(final Post post) {
        return Observable.just(post)
                .filter(Post::isLink)
                .filter(post1 -> post1.getPostHint() != PostHint.SELF)
                .map(post1 -> {
                    post1.setMediaObservable(Observable.just(new Link(post1.getDomain())));
                    return post1;
                });
    }
}
