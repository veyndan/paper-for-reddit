package com.veyndan.redditclient.post.mutator;

import com.google.common.collect.ImmutableList;
import com.veyndan.redditclient.post.model.Post;

import java.util.List;

import rx.functions.Action1;

public final class Mutators {

    /**
     * All available mutator factories.
     * <p>
     * Note that the order of the mutator factories is the order in which the post will be
     * checked for applicability of mutation.
     * <p>
     * Example if the {@link TwitterMutatorFactory} was set
     * after {@link LinkMutatorFactory} then the post would see if the {@link LinkMutatorFactory} is
     * applicable to mutate the post first, and if {@code false}, then {@link TwitterMutatorFactory}
     * would check if it is applicable. In this case the {@link TwitterMutatorFactory} would never
     * be checked for applicability or be applicable, as if the post contains a link, then
     * {@link LinkMutatorFactory} would be applicable, then mutation would stop. If
     * {@link LinkMutatorFactory} isn't applicable, then the post doesn't have a link, so
     * {@link TwitterMutatorFactory} would never be applicable. Obviously this means that
     * {@link LinkMutatorFactory} should occur <b><em>after</em></b> {@link TwitterMutatorFactory}.
     */
    private static final List<MutatorFactory> MUTATOR_FACTORIES = ImmutableList.of(
            TwitterMutatorFactory.create(),
            XkcdMutatorFactory.create(),
            ImgurMutatorFactory.create(),
            ImgflipMutatorFactory.create(),
            ImageMutatorFactory.create(),
            TextMutatorFactory.create(),
            LinkImageMutatorFactory.create(),
            LinkMutatorFactory.create()
    );

    /**
     * Mutate a list of posts by the first mutator which is applicable to mutate the post.
     */
    public static Action1<Post> mutate() {
        return post -> {
            for (final MutatorFactory mutatorFactory : MUTATOR_FACTORIES) {
                if (mutatorFactory.mutate(post)) return;
            }
        };
    }
}
