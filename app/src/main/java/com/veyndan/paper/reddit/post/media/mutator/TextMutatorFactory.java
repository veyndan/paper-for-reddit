package com.veyndan.paper.reddit.post.media.mutator;

import android.support.annotation.NonNull;

import com.veyndan.paper.reddit.api.reddit.model.PostHint;
import com.veyndan.paper.reddit.post.media.model.Text;
import com.veyndan.paper.reddit.post.model.Post;

import io.reactivex.Maybe;
import io.reactivex.Single;

final class TextMutatorFactory implements MutatorFactory {

    @NonNull
    static TextMutatorFactory create() {
        return new TextMutatorFactory();
    }

    private TextMutatorFactory() {
    }

    @NonNull
    @Override
    public Maybe<Post> mutate(@NonNull final Post post) {
        return Single.just(post)
                .filter(post1 -> post1.getPostHint() == PostHint.SELF && post1.getBody().length() > 0)
                .map(post1 -> {
                    final Text text = new Text(context -> post1.getDisplayBody(context).blockingGet());
                    post1.getMedias().add(text);
                    return post1;
                });
    }
}
