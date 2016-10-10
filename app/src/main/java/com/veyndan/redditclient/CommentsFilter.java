package com.veyndan.redditclient;

import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.post.PostsFilter;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

public class CommentsFilter implements PostsFilter {

    private final String subreddit;
    private final String article;

    public CommentsFilter(final String subreddit, final String article) {
        this.subreddit = subreddit;
        this.article = article;
    }

    @Override
    public Observable<Response<Thing<Listing>>> getRequestObservable(final Reddit reddit) {
        return reddit.subredditComments(subreddit, article)
                .map(response -> {
                    final List<Thing<Listing>> things = response.body();
                    things.get(0).data.children.addAll(things.get(1).data.children);
                    return Response.success(things.get(0));
                });
    }
}
