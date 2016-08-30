package com.veyndan.redditclient.post.mutator;

import com.veyndan.redditclient.api.reddit.model.Comment;
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
                .filter(post1 -> post1.submission instanceof Comment)
                .map(post1 -> {
                    final Comment comment = (Comment) post1.submission;
                    post1.setMediaObservable(Observable.just(new Text(comment.bodyHtml)));
                    return post1;
                });
    }
}
