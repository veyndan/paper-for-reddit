package com.veyndan.paper.reddit.post.media.mutator;

import com.veyndan.paper.reddit.api.reddit.model.Source;
import com.veyndan.paper.reddit.post.media.model.LinkImage;
import com.veyndan.paper.reddit.post.model.Post;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

final class LinkImageMutatorFactory implements MutatorFactory {

    @Override
    public Maybe<Post> mutate(final Post post) {
        return Single.just(post)
                .filter(post1 -> post1.preview().images.size() > 0)
                .map(post1 -> {
                    final Source source = post1.preview().images.get(0).source;
                    final LinkImage linkImage = LinkImage.create(source.url, post1.domain());
                    return post1.withMedias(post1.medias().value.concatWith(Observable.just(linkImage)));
                });
    }
}
