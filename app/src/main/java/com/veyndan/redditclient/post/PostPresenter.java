package com.veyndan.redditclient.post;

import com.veyndan.redditclient.Config;
import com.veyndan.redditclient.Presenter;
import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.network.Credentials;
import com.veyndan.redditclient.post.media.mutator.Mutators;
import com.veyndan.redditclient.post.model.Post;

import retrofit2.Response;
import rx.Observable;
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

    public void loadPosts(final PostsFilter postsFilter) {
        postsFilter.getRequestObservable(reddit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Response::body)
                // Paginate the posts
                .doOnNext(thing -> postMvpView.getNextPageTrigger()
                        .takeFirst(Boolean::booleanValue)
                        .subscribe(aBoolean -> {
                            if (thing.data.after == null) {
                                postMvpView.removeProgressBar();
                            } else {
                                postsFilter.setAfter(thing.data.after);
                                loadPosts(postsFilter);
                            }
                        }))
                .map(thing -> thing.data.children)
                .flatMap(Observable::from)
                .map(Post::new)
                .flatMap(Mutators.mutate())
                .toList()
                .subscribe(posts -> {
                    postMvpView.showPosts(posts);
                });
    }
}
