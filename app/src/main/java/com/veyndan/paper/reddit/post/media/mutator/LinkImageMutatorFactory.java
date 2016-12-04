package com.veyndan.paper.reddit.post.media.mutator;

import android.support.annotation.NonNull;

import com.veyndan.paper.reddit.api.reddit.model.Source;
import com.veyndan.paper.reddit.post.media.model.LinkImage;
import com.veyndan.paper.reddit.post.model.Post;

import io.reactivex.Maybe;
import io.reactivex.Single;

final class LinkImageMutatorFactory implements MutatorFactory {

    @NonNull
    static LinkImageMutatorFactory create() {
        return new LinkImageMutatorFactory();
    }

    private LinkImageMutatorFactory() {
    }

    @NonNull
    @Override
    public Maybe<Post> mutate(@NonNull final Post post) {
        return Single.just(post)
                .filter(post1 -> !post1.getPreview().images.isEmpty())
                .map(post1 -> {
                    final Source source = post1.getPreview().images.get(0).source;
                    final LinkImage linkImage = new LinkImage(source.url, post1.getDomain().blockingGet());
                    post1.getMedias().add(linkImage);
                    return post1;
                });
    }
}
