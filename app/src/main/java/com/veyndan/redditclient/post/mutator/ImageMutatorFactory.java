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
    public boolean applicable(final Post post) {
        return post.submission instanceof Link
                && ((Link) post.submission).getPostHint().equals(PostHint.IMAGE);
    }

    @Override
    public void mutate(final Post post) {
        final boolean imageDimensAvailable = !((Link) post.submission).preview.images.isEmpty();

        int width = 0;
        int height = 0;
        if (imageDimensAvailable) {
            final Source source = ((Link) post.submission).preview.images.get(0).source;
            width = source.width;
            height = source.height;
        }

        final Image image = new Image(post.submission.linkUrl, width, height);
        post.setMediaObservable(Observable.just(image));
    }
}
