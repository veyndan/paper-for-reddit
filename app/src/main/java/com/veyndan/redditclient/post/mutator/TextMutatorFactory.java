package com.veyndan.redditclient.post.mutator;

import com.veyndan.redditclient.post.model.Post;
import com.veyndan.redditclient.post.model.media.Text;

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
                .filter(Post::isComment)
                .map(post1 -> {
                    post1.setMediaObservable(Observable.just(new Text(post1.getBodyHtml())));
                    return post1;
                });
    }
}
