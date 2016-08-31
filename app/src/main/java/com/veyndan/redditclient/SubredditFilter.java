package com.veyndan.redditclient;

import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.api.reddit.network.QueryBuilder;
import com.veyndan.redditclient.api.reddit.network.Sort;
import com.veyndan.redditclient.post.PostsFilter;

import retrofit2.Response;
import rx.Observable;

public class SubredditFilter implements PostsFilter {

    private final String subreddit;
    private final Sort sort;
    private final QueryBuilder query;

    public SubredditFilter(final String subreddit, final Sort sort) {
        this(subreddit, sort, new QueryBuilder());
    }

    public SubredditFilter(final String subreddit, final Sort sort, final QueryBuilder query) {
        this.subreddit = subreddit;
        this.sort = sort;
        this.query = query;
    }

    @Override
    public Observable<Response<Thing<Listing>>> getRequestObservable(final Reddit reddit) {
        return reddit.subreddit(subreddit, sort, query);
    }

    @Override
    public void setAfter(final String after) {
        query.after(after);
    }
}
