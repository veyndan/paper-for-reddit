package com.veyndan.redditclient.post.mutator;

import com.veyndan.redditclient.post.model.Post;

interface MutatorFactory {

    /**
     * Mutate the post according to the mutators function.
     *
     * @return {@code true} if the MutatorFactory mutated the post.
     */
    boolean mutate(Post post);
}
