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
    public boolean mutate(final Post post) {
        if (post.submission instanceof com.veyndan.redditclient.api.reddit.model.Link) {
            final com.veyndan.redditclient.api.reddit.model.Link link = (com.veyndan.redditclient.api.reddit.model.Link) post.submission;

            if (!link.getPostHint().equals(PostHint.SELF)) {
                post.setMediaObservable(Observable.just(new Link(link.domain)));
                return true;
            }
        }

        return false;
    }
}
