package com.veyndan.redditclient;

import com.veyndan.redditclient.api.network.QueryBuilder;
import com.veyndan.redditclient.api.network.Sort;

public class SubredditFilter {

    private String subreddit;
    private Sort sort;
    private QueryBuilder query;

    public SubredditFilter(final String subreddit, final Sort sort) {
        this(subreddit, sort, new QueryBuilder());
    }

    public SubredditFilter(final String subreddit, final Sort sort, final QueryBuilder query) {
        this.subreddit = subreddit;
        this.sort = sort;
        this.query = query;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public Sort getSort() {
        return sort;
    }

    public QueryBuilder getQuery() {
        return query;
    }
}
