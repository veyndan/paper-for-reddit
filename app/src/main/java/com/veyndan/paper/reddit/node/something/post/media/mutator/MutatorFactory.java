package com.veyndan.paper.reddit.node.something.post.media.mutator;

import com.veyndan.paper.reddit.node.something.post.Post;

import io.reactivex.Maybe;

interface MutatorFactory {

    /**
     * Mutate the post according to the mutators function.
     */
    Maybe<Post> mutate(Post post);
}
