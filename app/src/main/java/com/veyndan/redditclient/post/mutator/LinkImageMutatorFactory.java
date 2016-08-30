package com.veyndan.redditclient.post.mutator;

import com.veyndan.redditclient.api.reddit.model.Link;
import com.veyndan.redditclient.api.reddit.model.Source;
import com.veyndan.redditclient.post.model.Post;
import com.veyndan.redditclient.post.model.media.LinkImage;

import rx.Observable;
import timber.log.Timber;

final class LinkImageMutatorFactory implements MutatorFactory {

    static LinkImageMutatorFactory create() {
        return new LinkImageMutatorFactory();
    }

    private LinkImageMutatorFactory() {
    }

    @Override
    public Observable<Post> mutate(final Post post) {
        Timber.d(post.submission.linkTitle);
        Timber.d(String.valueOf(((Link) post.submission).preview.images.size()));
        return Observable.just(post)
                .filter(post1 -> post1.submission instanceof Link)
                .filter(post1 -> {
                    final Link link = (Link) post1.submission;
                    return !link.preview.images.isEmpty();
                })
                .map(post1 -> {
                    final Link link = (Link) post1.submission;
                    final Source source = link.preview.images.get(0).source;
                    final LinkImage linkImage = new LinkImage(source.url, link.domain);
                    post1.setMediaObservable(Observable.just(linkImage));
                    return post1;
                });
    }
}
