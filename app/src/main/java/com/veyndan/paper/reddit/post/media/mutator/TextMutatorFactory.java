package com.veyndan.paper.reddit.post.media.mutator;

import android.text.TextUtils;

import com.veyndan.paper.reddit.api.reddit.model.PostHint;
import com.veyndan.paper.reddit.post.media.model.Text;
import com.veyndan.paper.reddit.post.model.Post;

import rx.Observable;

final class TextMutatorFactory implements MutatorFactory {

    static TextMutatorFactory create() {
        return new TextMutatorFactory();
    }

    private TextMutatorFactory() {
    }

    @Override
    public Observable<Post> mutate(final Post post) {
        return Observable.just(post)
                .filter(post1 -> post1.getPostHint() == PostHint.SELF &&
                        !TextUtils.isEmpty(post1.getBody()))
                .map(post1 -> {
                    final Text text = new Text(post1::getDisplayBody);
                    post1.setMediaObservable(Observable.just(text));
                    return post1;
                });
    }
}
