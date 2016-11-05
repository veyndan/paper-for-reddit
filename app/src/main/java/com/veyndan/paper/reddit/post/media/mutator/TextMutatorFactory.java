package com.veyndan.paper.reddit.post.media.mutator;

import android.text.TextUtils;

import com.veyndan.paper.reddit.api.reddit.model.PostHint;
import com.veyndan.paper.reddit.post.media.model.Text;
import com.veyndan.paper.reddit.post.model.Post;

import io.reactivex.Maybe;
import io.reactivex.Single;

final class TextMutatorFactory implements MutatorFactory {

    static TextMutatorFactory create() {
        return new TextMutatorFactory();
    }

    private TextMutatorFactory() {
    }

    @Override
    public Maybe<Post> mutate(final Post post) {
        return Single.just(post)
                .filter(post1 -> post1.getPostHint() == PostHint.SELF &&
                        !TextUtils.isEmpty(post1.getBody()))
                .map(post1 -> {
                    final Text text = new Text(post1::getDisplayBody);
                    post1.getMedias().add(text);
                    return post1;
                });
    }
}
