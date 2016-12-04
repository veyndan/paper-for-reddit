package com.veyndan.paper.reddit.post.media.mutator;

import android.support.annotation.NonNull;

import com.veyndan.paper.reddit.post.model.Post;

import io.reactivex.Maybe;

interface MutatorFactory {

    /**
     * Mutate the post according to the mutators function.
     */
    @NonNull
    Maybe<Post> mutate(@NonNull Post post);
}
