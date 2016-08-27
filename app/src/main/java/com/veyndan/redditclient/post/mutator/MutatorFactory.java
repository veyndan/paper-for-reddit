package com.veyndan.redditclient.post.mutator;

import com.veyndan.redditclient.post.model.Post;

interface MutatorFactory {

    /**
     * @return {@code true} if MutatorFactory should mutate the post.
     */
    boolean applicable(Post post);

    /**
     * Mutate the post according to the mutators function.
     */
    void mutate(Post post);
}
