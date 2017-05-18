package com.veyndan.paper.reddit.post.media.mutator

import com.veyndan.paper.reddit.post.model.Post
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function

class Mutators private constructor() {

    companion object {

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
        private val MUTATOR_FACTORIES: Observable<MutatorFactory> = Observable.fromArray(
                TwitterMutatorFactory(),
                XkcdMutatorFactory(),
                ImgurMutatorFactory(),
                ImgflipMutatorFactory(),
                ImageMutatorFactory(),
                TextMutatorFactory(),
                LinkImageMutatorFactory(),
                LinkMutatorFactory()
        )

        /**
         * Mutate a list of posts by the first mutator which is applicable to mutate the post.
         */
        @JvmStatic
        fun mutate(): Function<Post, Single<Post>> {
            return Function { post ->
                MUTATOR_FACTORIES
                        .flatMapMaybe { mutatorFactory -> mutatorFactory.mutate(post) }
                        .first(post)
            }
        }
    }
}
