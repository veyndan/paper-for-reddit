package com.veyndan.redditclient.post.mutator;

import com.veyndan.redditclient.api.reddit.model.RedditObject;

interface MutatorFactory {

    /**
     * @return {@code true} if MutatorFactory should mutate the post.
     */
    boolean applicable(RedditObject post);

    /**
     * Mutate the post according to the mutators function.
     */
    void mutate(RedditObject post);
}
