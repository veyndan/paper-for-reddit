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
                .filter(post1 -> post1.submission instanceof com.veyndan.redditclient.api.reddit.model.Link)
                .filter(post1 -> {
                    final com.veyndan.redditclient.api.reddit.model.Link link = (com.veyndan.redditclient.api.reddit.model.Link) post1.submission;
                    return !link.getPostHint().equals(PostHint.SELF);
                })
                .map(post1 -> {
                    final com.veyndan.redditclient.api.reddit.model.Link link = (com.veyndan.redditclient.api.reddit.model.Link) post1.submission;
                    post1.setMediaObservable(Observable.just(new Link(link.domain)));
                    return post1;
                });
    }
}
