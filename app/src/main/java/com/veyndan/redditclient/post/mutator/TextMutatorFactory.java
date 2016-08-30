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
    public boolean mutate(final Post post) {
        if (post.submission instanceof Comment) {
            final Comment comment = (Comment) post.submission;
            post.setMediaObservable(Observable.just(new Text(comment.bodyHtml)));
            return true;
        }

        return false;
    }
}
