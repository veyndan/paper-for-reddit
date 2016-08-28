package com.veyndan.redditclient.post;

import com.veyndan.redditclient.Config;
import com.veyndan.redditclient.PostsFilter;
import com.veyndan.redditclient.Presenter;
import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.model.Link;
import com.veyndan.redditclient.api.reddit.model.PostHint;
import com.veyndan.redditclient.api.reddit.model.Source;
import com.veyndan.redditclient.api.reddit.network.Credentials;
import com.veyndan.redditclient.post.model.Post;
import com.veyndan.redditclient.post.model.media.Image;
import com.veyndan.redditclient.post.mutator.Mutators;

import retrofit2.Response;
import rx.Observable;
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

    public void loadPosts(final PostsFilter postsFilter) {
        postsFilter.getRequestObservable(reddit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Response::body)
                // Paginate the posts
                .doOnNext(thing -> postMvpView.getNextPageTrigger()
                        .filter(Boolean::booleanValue)
                        .subscribe(aBoolean -> {
                            postsFilter.setAfter(thing.data.after);
                            loadPosts(postsFilter);
                        }))
                .map(thing -> thing.data.children)
                .flatMap(Observable::from)
                .map(redditObject -> {
                    final Post post = new Post(redditObject);
                    if (post.submission instanceof Link && ((Link) post.submission).getPostHint().equals(PostHint.IMAGE)) {
                        final boolean imageDimensAvailable = !((Link) post.submission).preview.images.isEmpty();

                        int width = 0;
                        int height = 0;
                        if (imageDimensAvailable) {
                            final Source source = ((Link) post.submission).preview.images.get(0).source;
                            width = source.width;
                            height = source.height;
                        }

                        final Image image = new Image(post.submission.linkUrl, width, height);
                        post.setImageObservable(Observable.just(image));
                    }
                    return post;
                })
                .toList()
                .flatMap(mutators.mutate())
                .subscribe(posts -> {
                    postMvpView.showPosts(posts);
                });
    }
}
