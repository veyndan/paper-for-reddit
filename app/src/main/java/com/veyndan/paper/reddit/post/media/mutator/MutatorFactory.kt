package com.veyndan.paper.reddit.post.media.mutator

import com.veyndan.paper.reddit.post.model.Post
import io.reactivex.Maybe

interface MutatorFactory {

    /**
     * Mutate the post according to the mutators function.
     */
    fun mutate(post: Post): Maybe<Post>
}
