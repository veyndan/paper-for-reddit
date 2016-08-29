package com.veyndan.redditclient.post.mutator;

import com.google.common.collect.ImmutableList;
import com.veyndan.redditclient.post.model.Post;

import java.util.List;

import rx.functions.Action1;

public final class Mutators {

    /**
     * All available mutator factories.
     * <p>
     * Note that the order of the mutator factories is the order in which the post will be mutated.
     */
    private static final List<MutatorFactory> MUTATOR_FACTORIES = ImmutableList.of(
            LinkImageMutatorFactory.create(),
            LinkMutatorFactory.create(),
            ImgurMutatorFactory.create(),
            TwitterMutatorFactory.create(),
            XkcdMutatorFactory.create()
    );

    /**
     * Mutate a list of posts by all the available mutator factories.
     */
    public Action1<Post> mutate() {
        return post -> {
            for (final MutatorFactory mutatorFactory : MUTATOR_FACTORIES) {
                if (mutatorFactory.applicable(post)) mutatorFactory.mutate(post);
            }
        };
    }
}
