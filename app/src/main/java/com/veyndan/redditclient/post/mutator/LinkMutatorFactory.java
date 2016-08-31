package com.veyndan.redditclient.post.mutator;

import com.veyndan.redditclient.api.reddit.model.PostHint;
import com.veyndan.redditclient.post.model.Post;
import com.veyndan.redditclient.post.model.media.Link;

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
                .filter(post1 -> !post1.getPostHint().equals(PostHint.SELF))
                .map(post1 -> {
                    post1.setMediaObservable(Observable.just(new Link(post1.getDomain())));
                    return post1;
                });
    }
}
