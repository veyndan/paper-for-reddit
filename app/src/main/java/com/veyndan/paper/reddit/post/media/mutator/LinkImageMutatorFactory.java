package com.veyndan.paper.reddit.post.media.mutator;

import com.veyndan.paper.reddit.api.reddit.model.Source;
import com.veyndan.paper.reddit.post.media.model.LinkImage;
import com.veyndan.paper.reddit.post.model.Post;

import io.reactivex.Maybe;
import io.reactivex.Single;

final class LinkImageMutatorFactory implements MutatorFactory {

    static LinkImageMutatorFactory create() {
        return new LinkImageMutatorFactory();
    }

    private LinkImageMutatorFactory() {
    }

    @Override
    public Maybe<Post> mutate(final Post post) {
        return Single.just(post)
                .filter(post1 -> !post1.getPreview().images.isEmpty())
                .map(post1 -> {
                    final Source source = post1.getPreview().images.get(0).source;
                    final LinkImage linkImage = LinkImage.create(source.url, post1.getDomain());
                    post1.getMedias().add(linkImage);
                    return post1;
                });
    }
}
