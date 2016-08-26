package com.veyndan.redditclient.post;

import com.veyndan.redditclient.Config;
import com.veyndan.redditclient.Presenter;
import com.veyndan.redditclient.SubredditFilter;
import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.network.Credentials;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PostPresenter implements Presenter<PostMvpView> {

    private PostMvpView postMvpView;

    private final Reddit reddit;

    public PostPresenter() {
        final Credentials credentials = new Credentials(Config.REDDIT_CLIENT_ID_RAWJAVA, Config.REDDIT_CLIENT_SECRET, Config.REDDIT_USER_AGENT, Config.REDDIT_USERNAME, Config.REDDIT_PASSWORD);
        reddit = new Reddit.Builder(credentials).build();
    }

    @Override
    public void attachView(final PostMvpView view) {
        postMvpView = view;
    }

    @Override
    public void detachView() {
        postMvpView = null;
    }

    public void loadPosts(final SubredditFilter subredditFilter) {
        reddit.subreddit(subredditFilter.getSubreddit(), subredditFilter.getSort(), subredditFilter.getQuery(), postMvpView.getNextPageTrigger(), Schedulers.io(), AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    postMvpView.showPosts(response.body().data.children);
                });
    }
}
