package com.veyndan.redditclient.post;

import com.veyndan.redditclient.Config;
import com.veyndan.redditclient.Presenter;
import com.veyndan.redditclient.SubredditFilter;
import com.veyndan.redditclient.UserFilter;
import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.network.Credentials;
import com.veyndan.redditclient.post.mutator.Mutators;

import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PostPresenter implements Presenter<PostMvpView> {

    private PostMvpView postMvpView;

    private final Reddit reddit;

    private final Mutators mutators = new Mutators();

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
        reddit.subreddit(subredditFilter.getSubreddit(), subredditFilter.getSort(), subredditFilter.getQuery())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Response::body)
                .doOnNext(thing -> postMvpView.getNextPageTrigger()
                        .filter(Boolean::booleanValue)
                        .subscribe(aBoolean -> {
                            subredditFilter.getQuery().after(thing.data.after); // TODO Make an interface with after or QueryBuilder as that what's makes it paginated. Interface implemented by Filters like SubredditFilter and UserFilter.
                            loadPosts(subredditFilter);
                        }))
                .map(thing -> thing.data.children)
                .flatMap(mutators.mutate())
                .subscribe(posts -> {
                    postMvpView.showPosts(posts);
                });
    }

    public void loadPosts(final UserFilter userFilter) {
        reddit.user(userFilter.getUsername(), userFilter.getUser())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    postMvpView.showPosts(response.body().data.children);
                });
    }
}
