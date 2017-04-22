package com.veyndan.paper.reddit.post.media.mutator;

import android.text.TextUtils;

import com.veyndan.paper.reddit.api.reddit.model.PostHint;
import com.veyndan.paper.reddit.post.media.model.Text;
import com.veyndan.paper.reddit.post.model.Post;

import io.reactivex.Maybe;
import io.reactivex.Observable;
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
                .filter(post1 -> post1.postHint().value == PostHint.SELF &&
                        !TextUtils.isEmpty(post1.bodyHtml()))
                .map(post1 -> {
                    final Text text = Text.create(post1::getDisplayBody);
                    return post1.withMedias(post1.medias().value.concatWith(Observable.just(text)));
                });
    }
}
