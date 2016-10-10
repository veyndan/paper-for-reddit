package com.veyndan.redditclient;

import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.api.reddit.network.QueryBuilder;
import com.veyndan.redditclient.api.reddit.network.Sort;
import com.veyndan.redditclient.api.reddit.network.User;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

public final class Request {

    private static final Reddit REDDIT = new Reddit.Builder(Config.REDDIT_CREDENTIALS).build();

    public static Observable<Response<Thing<Listing>>> subreddit(final String subreddit, final Sort sort) {
        return subreddit(subreddit, sort, new QueryBuilder());
    }

    public static Observable<Response<Thing<Listing>>> subreddit(final String subreddit, final Sort sort, final QueryBuilder query) {
        return paginate(new RequestBuilder(REDDIT).subreddit(subreddit, sort), query);
    }

    public static Observable<Response<Thing<Listing>>> user(final String username, final User user) {
        return user(username, user, new QueryBuilder());
    }

    public static Observable<Response<Thing<Listing>>> user(final String username, final User user, final QueryBuilder query) {
        return paginate(new RequestBuilder(REDDIT).user(username, user), query);
    }

    public static Observable<Response<Thing<Listing>>> comments(final String subreddit, final String article) {
        return REDDIT.subredditComments(subreddit, article)
                .map(response -> {
                    final List<Thing<Listing>> things = response.body();
                    things.get(0).data.children.addAll(things.get(1).data.children);
                    return Response.success(things.get(0));
                });
    }

    private static Observable<Response<Thing<Listing>>> paginate(final RequestBuilder requestBuilder, final QueryBuilder query) {
        return Observable.just(query)
                // TODO If the query has never been initialized, then we want it to pass.
                .filter(query1 -> !query1.build().containsKey("after") || query1.build().get("after") != null)
                .flatMap(query1 -> requestBuilder.query(query1).build())
                .doOnNext(response -> query.after(response.body().data.after));
    }
}
