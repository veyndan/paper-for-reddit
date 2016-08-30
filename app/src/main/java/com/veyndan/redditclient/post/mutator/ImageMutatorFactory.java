package com.veyndan.redditclient.post.mutator;

import com.veyndan.redditclient.api.reddit.model.Link;
import com.veyndan.redditclient.api.reddit.model.PostHint;
import com.veyndan.redditclient.api.reddit.model.Source;
import com.veyndan.redditclient.post.model.Post;
import com.veyndan.redditclient.post.model.media.Image;

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
                .filter(post1 -> post1.submission instanceof Link)
                .filter(post1 -> {
                    final Link link = (Link) post1.submission;
                    return link.getPostHint().equals(PostHint.IMAGE);
                })
                .map(post1 -> {
                    final Link link = (Link) post1.submission;

                    final boolean imageDimensAvailable = !link.preview.images.isEmpty();

                    int width = 0;
                    int height = 0;
                    if (imageDimensAvailable) {
                        final Source source = link.preview.images.get(0).source;
                        width = source.width;
                        height = source.height;
                    }

                    final Image image = new Image(post1.submission.linkUrl, width, height);
                    post1.setMediaObservable(Observable.just(image));
                    return post1;
                });
    }
}
