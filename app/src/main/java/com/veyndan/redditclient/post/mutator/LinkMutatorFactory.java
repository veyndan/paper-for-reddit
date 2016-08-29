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
    public boolean applicable(final Post post) {
        return post.submission instanceof com.veyndan.redditclient.api.reddit.model.Link
                && !((com.veyndan.redditclient.api.reddit.model.Link) post.submission).getPostHint().equals(PostHint.SELF);
    }

    @Override
    public void mutate(final Post post) {
        final com.veyndan.redditclient.api.reddit.model.Link link = (com.veyndan.redditclient.api.reddit.model.Link) post.submission;
        post.setLinkObservable(Observable.just(new Link(link.domain)));
    }
}
