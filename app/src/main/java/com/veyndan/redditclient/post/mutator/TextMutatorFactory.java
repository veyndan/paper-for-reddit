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
    public boolean applicable(final Post post) {
        return post.submission instanceof Comment;
    }

    @Override
    public void mutate(final Post post) {
        final Comment comment = (Comment) post.submission;
        post.setTextObservable(Observable.just(new Text(comment.bodyHtml)));
    }
}
