package com.veyndan.redditclient.post.media.mutator;

import android.text.TextUtils;

import com.veyndan.redditclient.api.reddit.model.PostHint;
import com.veyndan.redditclient.post.media.model.Text;
import com.veyndan.redditclient.post.model.Post;

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
                .filter(post1 -> post1.getPostHint().equals(PostHint.SELF) &&
                        !TextUtils.isEmpty(post1.getBodyHtml()))
                .map(post1 -> {
                    post1.setMediaObservable(Observable.just(new Text(post1.getBodyHtml())));
                    return post1;
                });
    }
}
