package com.veyndan.redditclient.post;

import com.veyndan.redditclient.Config;
import com.veyndan.redditclient.Presenter;
import com.veyndan.redditclient.Tree;
import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.post.media.mutator.Mutators;
import com.veyndan.redditclient.post.model.Post;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PostPresenter implements Presenter<PostMvpView> {

    private PostMvpView postMvpView;

    private final Reddit reddit;

    public PostPresenter() {
        reddit = new Reddit.Builder(Config.REDDIT_CREDENTIALS).build();
    }

    @Override
    public void attachView(final PostMvpView view) {
        postMvpView = view;
    }

    @Override
    public void detachView() {
        postMvpView = null;
    }

    public void loadPosts(final PostsFilter postsFilter, final Observable<Boolean> nextPageTrigger) {
        postsFilter.getRequestObservable(reddit)
                .subscribeOn(Schedulers.io())
                .map(Response::body)
                // Paginate the posts
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(thing -> nextPageTrigger
                        .takeFirst(Boolean::booleanValue)
                        .subscribe(aBoolean -> {
                            if (thing.data.after == null) {
                                postMvpView.popNode();
                            } else {
                                postsFilter.setAfter(thing.data.after);
                                loadPosts(postsFilter, nextPageTrigger);
                            }
                        }))
                .observeOn(Schedulers.computation())
                .map(thing -> thing.data.children)
                .flatMap(Observable::from)
                .map(Post::new)
                .flatMap(Mutators.mutate())
                .map(post -> new Tree.Node<>(post, false))
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(posts -> {
                    postMvpView.appendNodes(posts);
                });
    }
}
