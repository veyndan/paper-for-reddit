package com.veyndan.paper.reddit.post.media.mutator;

import com.veyndan.paper.reddit.api.reddit.model.PostHint;
import com.veyndan.paper.reddit.api.reddit.model.Source;
import com.veyndan.paper.reddit.post.model.Post;
import com.veyndan.paper.reddit.post.media.model.Image;

import rx.Observable;

public final class ImageMutatorFactory implements MutatorFactory {

    public static ImageMutatorFactory create() {
        return new ImageMutatorFactory();
    }

    private ImageMutatorFactory() {
    }

    @Override
    public Observable<Post> mutate(final Post post) {
        return Observable.just(post)
                .filter(Post::isLink)
                .filter(post1 -> post1.getPostHint().equals(PostHint.IMAGE))
                .map(post1 -> {
                    final boolean imageDimensAvailable = !post.getPreview().images.isEmpty();

                    int width = 0;
                    int height = 0;
                    if (imageDimensAvailable) {
                        final Source source = post.getPreview().images.get(0).source;
                        width = source.width;
                        height = source.height;
                    }

                    final Image image = new Image(post1.getLinkUrl(), width, height);
                    post1.setMediaObservable(Observable.just(image));
                    return post1;
                });
    }
}
