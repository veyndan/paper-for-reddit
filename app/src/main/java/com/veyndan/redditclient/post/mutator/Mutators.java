package com.veyndan.redditclient.post.mutator;

import com.google.common.collect.ImmutableList;
import com.veyndan.redditclient.api.reddit.model.RedditObject;

import java.util.List;

import rx.functions.Action1;

public final class Mutators {

    private static final List<MutatorFactory> MUTATOR_FACTORIES = ImmutableList.of(
            ImgurMutatorFactory.create()
    );

    public Action1<RedditObject> mutate() {
        return post -> {
            for (final MutatorFactory mutatorFactory : MUTATOR_FACTORIES) {
                if (mutatorFactory.applicable(post)) mutatorFactory.mutate(post);
            }
        };
    }
}
