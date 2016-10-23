package com.veyndan.paper.reddit.tree.media.mutator;

import com.veyndan.paper.reddit.api.reddit.model.PostHint;
import com.veyndan.paper.reddit.tree.media.model.Text;
import com.veyndan.paper.reddit.tree.model.Post;

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
                        post1.getDisplayBody() != null)
                .map(post1 -> {
                    post1.setMediaObservable(Observable.just(new Text(post1.getDisplayBody())));
                    return post1;
                });
    }
}
