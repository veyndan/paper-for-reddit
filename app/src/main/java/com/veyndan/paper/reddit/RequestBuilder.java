package com.veyndan.paper.reddit;

import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.api.reddit.network.QueryBuilder;
import com.veyndan.paper.reddit.api.reddit.network.Sort;
import com.veyndan.paper.reddit.api.reddit.network.User;

import retrofit2.Response;
import rx.Observable;

public class RequestBuilder {

    private static final int TYPE_UNDEFINED = -1;
    private static final int TYPE_SUBREDDIT = 0;
    private static final int TYPE_USER = 1;
    private final Reddit reddit;

    private int type = TYPE_UNDEFINED;

    private QueryBuilder query = new QueryBuilder();

    private String subreddit;
    private Sort sort;

    private String username;
    private User user;

    public RequestBuilder(final Reddit reddit) {
        this.reddit = reddit;
    }

    public RequestBuilder subreddit(final String subreddit, final Sort sort) {
        type = TYPE_SUBREDDIT;
        this.subreddit = subreddit;
        this.sort = sort;
        return this;
    }

    public RequestBuilder user(final String username, final User user) {
        type = TYPE_USER;
        this.username = username;
        this.user = user;
        return this;
    }

    public RequestBuilder query(final QueryBuilder query) {
        this.query = query;
        return this;
    }

    public Observable<Response<Thing<Listing>>> build() {
        switch (type) {
            case TYPE_SUBREDDIT:
                return reddit.subreddit(subreddit, sort, query);
            case TYPE_USER:
                return reddit.user(username, user, query);
            case TYPE_UNDEFINED:
                throw new IllegalStateException();
            default:
                throw new IllegalStateException();
        }
    }
}
