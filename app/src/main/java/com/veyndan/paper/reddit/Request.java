package com.veyndan.paper.reddit;

import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.api.reddit.network.QueryBuilder;
import com.veyndan.paper.reddit.api.reddit.network.Sort;
import com.veyndan.paper.reddit.api.reddit.network.User;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;
import retrofit2.Response;

public final class Request {

    private static final Reddit REDDIT = new Reddit(Config.REDDIT_CREDENTIALS);

    public static Maybe<Response<Thing<Listing>>> subreddit(final String subreddit, final Sort sort) {
        return subreddit(subreddit, sort, new QueryBuilder());
    }

    public static Maybe<Response<Thing<Listing>>> subreddit(final String subreddit, final Sort sort, final QueryBuilder query) {
        return paginate(new RequestBuilder(REDDIT).subreddit(subreddit, sort), query);
    }

    public static Maybe<Response<Thing<Listing>>> user(final String username, final User user) {
        return user(username, user, new QueryBuilder());
    }

    public static Maybe<Response<Thing<Listing>>> user(final String username, final User user, final QueryBuilder query) {
        return paginate(new RequestBuilder(REDDIT).user(username, user), query);
    }

    public static Single<Response<Thing<Listing>>> comments(final String subreddit, final String article) {
        return REDDIT.subredditComments(subreddit, article)
                .map(response -> {
                    final List<Thing<Listing>> things = response.body();
                    things.get(0).data.children.addAll(things.get(1).data.children);
                    return Response.success(things.get(0));
                });
    }

    private static Maybe<Response<Thing<Listing>>> paginate(final RequestBuilder requestBuilder, final QueryBuilder query) {
        return Single.just(query)
                // TODO If the query has never been initialized, then we want it to pass.
                .filter(query1 -> !query1.build().containsKey("after") || query1.build().get("after") != null)
                .flatMap(query1 -> requestBuilder.query(query1).build().toMaybe())
                .doOnSuccess(response -> query.after(response.body().data.after));
    }
}
