package com.veyndan.paper.reddit.post.media.mutator;

import android.support.annotation.StringRes;
import android.util.Size;

import com.veyndan.paper.reddit.api.reddit.model.PostHint;
import com.veyndan.paper.reddit.api.reddit.model.Source;
import com.veyndan.paper.reddit.post.media.model.Image;
import com.veyndan.paper.reddit.post.model.Post;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public final class ImageMutatorFactory implements MutatorFactory {

    public static ImageMutatorFactory create() {
        return new ImageMutatorFactory();
    }

    private ImageMutatorFactory() {
    }

    @Override
    public Maybe<Post> mutate(final Post post) {
        return Single.just(post)
                .filter(post1 -> post1.postHint() == PostHint.IMAGE)
                .map(post1 -> {
                    final boolean imageDimensAvailable = !post1.preview().images.isEmpty();

                    final Size size;
                    if (imageDimensAvailable) {
                        final Source source = post1.preview().images.get(0).source;
                        size = new Size(source.width, source.height);
                    } else {
                        size = new Size(0, 0);
                    }

                    @StringRes final int type = post1.linkUrl().endsWith(".gif")
                            ? Image.IMAGE_TYPE_GIF
                            : Image.IMAGE_TYPE_STANDARD;
                    final Image image = Image.create(post1.linkUrl(), size, type);
                    return post1.withMedias(post1.medias().value.concatWith(Observable.just(image)));
                });
    }
}
