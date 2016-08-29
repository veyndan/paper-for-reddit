package com.veyndan.redditclient.post.mutator;

import com.veyndan.redditclient.api.reddit.model.Link;
import com.veyndan.redditclient.api.reddit.model.Source;
import com.veyndan.redditclient.post.model.Post;
import com.veyndan.redditclient.post.model.media.LinkImage;

import rx.Observable;

final class LinkImageMutatorFactory implements MutatorFactory {

    static LinkImageMutatorFactory create() {
        return new LinkImageMutatorFactory();
    }

    private LinkImageMutatorFactory() {
    }

    @Override
    public boolean applicable(final Post post) {
        return post.submission instanceof Link && !((Link) post.submission).preview.images.isEmpty();
    }

    @Override
    public void mutate(final Post post) {
        final Link link = (Link) post.submission;
        final Source source = link.preview.images.get(0).source;

        final LinkImage linkImage = new LinkImage(source.url, link.domain);

        post.setMediaObservable(Observable.just(linkImage));
    }
}
