package com.veyndan.redditclient.post.mutator;

import com.veyndan.redditclient.post.model.Post;

import rx.Observable;

interface MutatorFactory {

    /**
     * Mutate the post according to the mutators function.
     */
    Observable<Post> mutate(Post post);
}
