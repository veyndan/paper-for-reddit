package com.veyndan.paper.reddit.post.media.mutator;

import com.veyndan.paper.reddit.post.model.Post;

import rx.Observable;

interface MutatorFactory {

    /**
     * Mutate the post according to the mutators function.
     */
    Observable<Post> mutate(Post post);
}
